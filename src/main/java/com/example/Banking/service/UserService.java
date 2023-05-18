package com.example.Banking.service;

import com.example.Banking.model.UserModel;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

public interface UserService {
    public void createAccount(UserModel userModel) throws ResponseStatusException;

    UserDetails loadUserByUserName(String username);
}
