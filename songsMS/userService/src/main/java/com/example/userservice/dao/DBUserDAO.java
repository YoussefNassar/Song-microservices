package com.example.userservice.dao;

import com.example.userservice.model.User;
import com.example.userservice.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DBUserDAO implements IUserDAO {

    final UserRepository userRepository;

    @Autowired
    public DBUserDAO(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserByUserId(String userId) {
        return this.userRepository.findById(userId).get();
    }

    @Override
    public User getUserByToken(String token) {
        return this.userRepository.findUserByToken(token);
    }

    @Override
    public void setToken(String userId, String token) {
        Optional<User> userById = this.userRepository.findById(userId);

        if (userById.isPresent()) {
            userById.get().setToken(token);
            this.userRepository.save(userById.get());
        }
    }

    @Override
    public boolean isAuthenticated(String userId, String token) {
        return false;
    }
}
