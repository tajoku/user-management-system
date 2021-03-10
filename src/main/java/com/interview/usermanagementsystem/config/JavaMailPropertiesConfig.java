package com.interview.usermanagementsystem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mail")
@Data
public class JavaMailPropertiesConfig {

    private String username;

    private String password;
}
