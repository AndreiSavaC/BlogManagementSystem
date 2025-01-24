package com.example.service;

import com.example.entity.User;
import java.util.List;

public interface UserServiceLocal {
    void create(User user);
    User findById(Long id);
    User findByUsername(String username);
    List<User> findAll();
    void update(User user);
    void delete(Long id);
    User findByActivationToken(String token);
}
