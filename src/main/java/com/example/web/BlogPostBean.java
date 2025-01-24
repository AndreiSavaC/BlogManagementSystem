package com.example.web;

import com.example.entity.BlogPost;
import com.example.entity.Category;
import com.example.entity.Tag;
import com.example.entity.User;
import com.example.service.BlogPostServiceLocal;
import com.example.service.CategoryServiceLocal;
import com.example.service.TagServiceLocal;
import com.example.service.UserServiceLocal;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Named
@SessionScoped
public class BlogPostBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(BlogPostBean.class.getName());

    @EJB
    private BlogPostServiceLocal blogPostService;

    @EJB
    private CategoryServiceLocal categoryService;

    @EJB
    private TagServiceLocal tagService;

    @EJB
    private UserServiceLocal userService;

    private List<BlogPost> blogPosts;
    private List<Category> categories;
    private List<Tag> tags;
    private List<BlogPost> userBlogPosts;
    private List<BlogPost> allBlogPosts;

    private BlogPost selectedPost;
    private String title;
    private String content;
    private Long categoryId;
    private Set<Long> selectedTagIds = new HashSet<>();

    @PostConstruct
    public void init() {
        LOGGER.info("Initializing BlogPostBean");
        loadBlogPosts();
        loadCategories();
        loadTags();
        loadUserBlogPosts();
        loadAllBlogPosts();
    }

    public void loadBlogPosts() {
        try {
            blogPosts = blogPostService.findAll();
            LOGGER.info("Loaded " + blogPosts.size() + " blog posts.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading blog posts.", e);
        }
    }

    public void loadCategories() {
        try {
            categories = categoryService.findAll();
            LOGGER.info("Loaded " + categories.size() + " categories.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading categories.", e);
        }
    }

    public void loadTags() {
        try {
            tags = tagService.findAll();
            LOGGER.info("Loaded " + tags.size() + " tags.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading tags.", e);
        }
    }

    public void loadUserBlogPosts() {
        try {
            User currentUser = getCurrentUser();
            if (currentUser != null) {
                userBlogPosts = blogPostService.findAll()
                        .stream()
                        .filter(post -> post.getUser().getId().equals(currentUser.getId()))
                        .collect(Collectors.toList());
                LOGGER.info("Loaded " + userBlogPosts.size() + " blog posts for user: " + currentUser.getUsername());
            } else {
                LOGGER.warning("No authenticated user found while loading user blog posts.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading user-specific blog posts.", e);
        }
    }

    public void loadAllBlogPosts() {
        try {
            allBlogPosts = blogPostService.findAll();
            LOGGER.info("Loaded all blog posts. Total count: " + allBlogPosts.size());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading all blog posts.", e);
        }
    }

    public List<BlogPost> getBlogPosts() {
        if (blogPosts == null) {
            loadBlogPosts();
        }
        return blogPosts;
    }

    public List<Category> getCategories() {
        if (categories == null) {
            loadCategories();
        }
        return categories;
    }

    public List<Tag> getTags() {
        if (tags == null) {
            loadTags();
        }
        return tags;
    }

    public List<BlogPost> getUserBlogPosts() {
        if (userBlogPosts == null) {
            loadUserBlogPosts();
        }
        return userBlogPosts;
    }

    public List<BlogPost> getAllBlogPosts() {
        if (allBlogPosts == null) {
            loadAllBlogPosts();
        }
        return allBlogPosts;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        LOGGER.fine("Setting title: " + title);
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        LOGGER.fine("Setting content.");
        this.content = content;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        LOGGER.fine("Setting category ID: " + categoryId);
        this.categoryId = categoryId;
    }

    public Set<Long> getSelectedTagIds() {
        return selectedTagIds;
    }

    public void setSelectedTagIds(Set<Long> selectedTagIds) {
        LOGGER.fine("Setting selected tag IDs: " + selectedTagIds);
        this.selectedTagIds = selectedTagIds;
    }

    private User getCurrentUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        String username = context.getExternalContext().getRemoteUser();
        if (username != null) {
            User user = userService.findByUsername(username);
            LOGGER.fine("Current user: " + (user != null ? user.getUsername() : "null"));
            return user;
        }
        LOGGER.fine("No user is currently authenticated.");
        return null;
    }

    public String prepareCreatePost() {
        LOGGER.info("Preparing to create a new blog post.");
        this.title = null;
        this.content = null;
        this.categoryId = null;
        this.selectedTagIds = new HashSet<>();
        this.selectedPost = null;

        return "createPost.xhtml?faces-redirect=true";
    }

    public String createPost() {
        FacesContext context = FacesContext.getCurrentInstance();

        LOGGER.info("Attempting to create a new blog post.");

        if (title == null || title.trim().isEmpty()) {
            LOGGER.warning("Blog post creation failed: Title is required.");
            context.addMessage("createForm:title",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Title is required.", null));
            return null;
        }
        if (content == null || content.trim().isEmpty()) {
            LOGGER.warning("Blog post creation failed: Content is required.");
            context.addMessage("createForm:content",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Content is required.", null));
            return null;
        }
        if (categoryId == null) {
            LOGGER.warning("Blog post creation failed: Category is required.");
            context.addMessage("createForm:category",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Category is required.", null));
            return null;
        }

        try {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                LOGGER.warning("Blog post creation failed: User not authenticated.");
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "User not authenticated.", null));
                return null;
            }

            Category category = categoryService.findById(categoryId);
            if (category == null) {
                LOGGER.warning("Blog post creation failed: Invalid category ID " + categoryId);
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid category.", null));
                return null;
            }

            Set<Tag> selectedTags = selectedTagIds.isEmpty()
                    ? new HashSet<>()
                    : tagService.findByIds(selectedTagIds);

            BlogPost post = new BlogPost();
            post.setTitle(title);
            post.setContent(content);
            post.setCategory(category);
            post.setUser(currentUser);
            post.setTags(selectedTags);
            post.setCreatedAt(LocalDateTime.now());

            blogPostService.create(post);
            LOGGER.info("Blog post created successfully with ID: " + post.getId());

            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Blog post created successfully!", null));

            title = null;
            content = null;
            categoryId = null;
            selectedTagIds.clear();

            loadBlogPosts();
            loadUserBlogPosts();
            loadAllBlogPosts();

            return "user.xhtml?faces-redirect=true";

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating blog post.", e);
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error creating post: " + e.getMessage(), null));
            return null;
        }
    }

    public void toggleTag(Long tagId) {
        if (selectedTagIds.contains(tagId)) {
            selectedTagIds.remove(tagId);
            LOGGER.fine("Tag ID " + tagId + " removed from selected tags.");
        } else {
            selectedTagIds.add(tagId);
            LOGGER.fine("Tag ID " + tagId + " added to selected tags.");
        }
    }

    public String editPost(Long id) {
        LOGGER.info("Preparing to edit blog post with ID: " + id);
        BlogPost post = blogPostService.findById(id);
        if (post == null) {
            LOGGER.warning("Edit post failed: Post not found with ID " + id);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Post not found.", null));
            return null;
        }

        this.selectedPost = post;
        this.title = post.getTitle();
        this.content = post.getContent();
        this.categoryId = (post.getCategory() != null) ? post.getCategory().getId() : null;
        this.selectedTagIds = post.getTags().stream().map(Tag::getId).collect(Collectors.toSet());

        LOGGER.info("Editing blog post with ID: " + id);
        return "editPost.xhtml?faces-redirect=true";
    }

    public String updatePost() {
        FacesContext context = FacesContext.getCurrentInstance();

        LOGGER.info("Attempting to update blog post.");

        if (title == null || title.trim().isEmpty()) {
            LOGGER.warning("Blog post update failed: Title is required.");
            context.addMessage("editForm:title",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Title is required.", null));
            return null;
        }

        if (content == null || content.trim().isEmpty()) {
            LOGGER.warning("Blog post update failed: Content is required.");
            context.addMessage("editForm:content",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Content is required.", null));
            return null;
        }

        try {
            if (selectedPost == null || selectedPost.getId() == null) {
                LOGGER.warning("Blog post update failed: No post selected.");
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "No post selected.", null));
                return null;
            }

            BlogPost post = blogPostService.findById(selectedPost.getId());

            if (post == null) {
                LOGGER.warning("Blog post update failed: Post not found with ID " + selectedPost.getId());
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Post not found.", null));
                return null;
            }

            post.setTitle(title);
            post.setContent(content);

            Category category = categoryService.findById(categoryId);
            if (category != null) {
                post.setCategory(category);
                LOGGER.fine("Blog post category updated to ID: " + categoryId);
            }

            Set<Tag> tags = selectedTagIds.isEmpty() ? new HashSet<>() : tagService.findByIds(selectedTagIds);
            post.setTags(tags);
            LOGGER.fine("Blog post tags updated.");

            blogPostService.update(post);
            LOGGER.info("Blog post updated successfully with ID: " + post.getId());

            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Blog post updated successfully!", null));

            loadBlogPosts();
            loadUserBlogPosts();
            loadAllBlogPosts();

            return "user.xhtml?faces-redirect=true";

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating blog post.", e);
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error updating post: " + e.getMessage(), null));
            return null;
        }
    }

    public void delete(Long id) {
        LOGGER.info("Attempting to delete blog post with ID: " + id);
        try {
            blogPostService.delete(id);
            LOGGER.info("Blog post deleted successfully with ID: " + id);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Blog post deleted successfully!", null));
            loadBlogPosts();
            loadUserBlogPosts();
            loadAllBlogPosts();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting blog post with ID: " + id, e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error deleting post.", null));
        }
    }

    public BlogPost getSelectedPost() {
        return selectedPost;
    }

    public void setSelectedPost(BlogPost selectedPost) {
        this.selectedPost = selectedPost;
    }
}
