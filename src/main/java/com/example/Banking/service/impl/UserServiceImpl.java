package com.example.Banking.service.impl;

import com.example.Banking.model.UserModel;
import com.example.Banking.repo.UserRepo;
import com.example.Banking.service.UserDataService;
import com.example.Banking.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserDataService userDataService;

    @Override
    public void createAccount(UserModel userModel)throws ResponseStatusException {
        log.info("Creating account");
        if (userRepo.existsUserModelByUserEmail(userModel.getUserEmail())) {
            log.info("User already available");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User already available");
        }
        userRepo.insert(userModel);
        log.info("User added in User Table");
        if(userModel.getRole()=="admin"){
            return;
        }
        userDataService.createBankDetails(userModel);
    }

    @Override
    public UserDetails loadUserByUserName(String userEmail) throws UsernameNotFoundException {
        UserModel user = userRepo.findByUserEmail(userEmail);
        if (user == null) {
            log.info("User not available");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User not available");
        }
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUserEmail())
                .password(user.getPassword())
                .roles(user.getRole()!=null?user.getRole():"user")
                .build();
    }
}
