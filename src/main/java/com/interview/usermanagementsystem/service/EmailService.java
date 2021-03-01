package com.interview.usermanagementsystem.service;


public interface EmailService {

    void sendText(String from, String to, String subject, String body);
}