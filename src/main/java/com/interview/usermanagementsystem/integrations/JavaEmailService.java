package com.interview.usermanagementsystem.integrations;

import com.interview.usermanagementsystem.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Profile("default")
@Component
@Slf4j
public class JavaEmailService implements EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendText(String from, String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        emailSender.send(message);
    }
}

