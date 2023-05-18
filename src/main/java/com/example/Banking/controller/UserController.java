package com.example.Banking.controller;

import com.example.Banking.model.UserModel;
import com.example.Banking.service.EmailService;
import com.example.Banking.service.SessionService;
import com.example.Banking.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Slf4j
@RequestMapping("/account")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    EmailService emailService;
    @Autowired
    private SessionService sessionService;
    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody UserModel user) {
        ResponseEntity responseEntity;
        try{
            userService.createAccount(user);
            responseEntity = new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        } catch (ResponseStatusException exception){
            responseEntity = new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return responseEntity;
    }

    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestParam("userMail") String userMail, @RequestParam("password") String password, HttpSession session) {
        try{
            UserDetails user = userService.loadUserByUserName(userMail);
            if (user == null || !password.equals(user.getPassword())) {
                return new ResponseEntity<>("Invalid Credentials",HttpStatus.UNAUTHORIZED);
            }
            session.setAttribute("userMail",userMail);
            sessionService.createSession(session.getId(), session);
            return ResponseEntity.ok("Login successful");
        } catch (RuntimeException exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        sessionService.destroySession(session.getId());
        return ResponseEntity.ok("Logout successful");
    }
    @GetMapping("/check-session/{userMail}")
    public ResponseEntity<String> checkSession(@PathVariable String userMail, HttpSession session) {
        if (sessionService.isValidSession(session.getId(),userMail)) {
            return ResponseEntity.ok("Valid session");
        } else {
            return new ResponseEntity<>("Invalid session",HttpStatus.UNAUTHORIZED);
        }
    }
}
