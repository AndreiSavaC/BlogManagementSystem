package com.example.service;

import com.example.entity.Tag;
import java.util.List;
import java.util.Set;

import jakarta.ejb.Local;

@Local
public interface TagServiceLocal {
    void create(Tag tag);
    Tag findById(Long id);
    List<Tag> findAll();
    void update(Tag tag);
    void delete(Long id);
    Set<Tag> findByIds(Set<Long> ids);
}
