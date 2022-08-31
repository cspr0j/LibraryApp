package com.user.app.server.service;

import com.user.app.server.model.User;

import java.util.List;

public interface UserService {

    User saveUser(User user);

    User getUser(String username);

    List<User> getAllUsers();
}
