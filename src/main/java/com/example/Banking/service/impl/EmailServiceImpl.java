package com.example.Banking.service.impl;

import com.example.Banking.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${mail.send.enabled}")
    boolean isMailEnabled;

    public void sendEmail(String to, String subject, String text) {
        if(!isMailEnabled){
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
    public void debitMail(String toMail,String amount){
        String text = "Your money has been debited. Rs:"+amount;
        log.info("Debit mail has been sent");
        sendEmail(toMail,"Debited",text);
        log.info("Debit mail has been sent");
    }
    public void creditMail(String toMail,String amount){
        String text = "Your money has been credit. Rs:"+amount;
        sendEmail(toMail,"credited",text);
        log.info("Credit mail has been sent");
    }

}


