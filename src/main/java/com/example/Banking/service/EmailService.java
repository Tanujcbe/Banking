package com.example.Banking.service;

public interface EmailService {
    public void sendEmail(String to, String subject, String text);
    public void creditMail(String toMail,String amount);
    public void debitMail(String toMail,String amount);
}
