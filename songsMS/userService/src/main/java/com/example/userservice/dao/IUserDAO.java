package com.example.userservice.dao;

import com.example.userservice.model.User;

public interface IUserDAO {
    public User getUserByUserId(String userId);
    public User getUserByToken(String token);

    public void setToken(String userId, String token);

    public boolean isAuthenticated(String userId, String token);
}
