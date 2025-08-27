// C:/Users/dotie/Documents/course_java/src/main/java/com/example/demo/configuration/VnPayProperties.java
package com.example.demo.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "payment.vnpay")
@Getter
@Setter
public class VnPayProperties {
    private String url;
    private String backendReturnUrl;
    private String tmnCode;
    private String hashSecret;
    private String version;
    private String frontendReturnUrl; // FIX: Add this missing field
}