package com.example.service;

import com.example.entity.BlogPost;
import jakarta.ejb.Local;

import java.time.LocalDateTime;
import java.util.List;

@Local
public interface BlogPostServiceLocal {
    void create(BlogPost post);
    BlogPost findById(Long id);
    List<BlogPost> findAll();
    void update(BlogPost post);
    void delete(Long id);
}
