package com.example.Banking.service;

import com.example.Banking.model.UserModel;
import com.example.Banking.repo.UserRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {

    @Autowired
    UserRepo userRepo;
    private Map<String, HttpSession> sessions = new ConcurrentHashMap<>();


    public void createSession(String sessionId, HttpSession session) {
        sessions.put(sessionId, session);
    }

    public void destroySession(String sessionId) {
        HttpSession session = sessions.get(sessionId);
        if (session != null) {
            session.invalidate();
            sessions.remove(sessionId);
        }
    }

    public boolean isValidSession(String sessionId,String userMail) {
        return sessions.containsKey(sessionId) && sessions.get(sessionId).getAttribute("userMail").equals(userMail);
    }
    public boolean isAdmin(String sessionId){
        if(!sessions.containsKey(sessionId)){
            return false;
        }
        String userMail =  String.valueOf(sessions.get(sessionId).getAttribute("userMail"));
        UserModel user = userRepo.findByUserEmail(userMail);
        return user.getRole()!=null && user.getRole().equalsIgnoreCase("admin");
    }
}

