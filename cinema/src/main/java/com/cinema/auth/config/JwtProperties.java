package com.cinema.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {
    
    private String secret;
    private long accessTokenExpiration;
    private long refreshTokenExpiration;
    private Cookie cookie = new Cookie();
    
    @Getter
    @Setter
    public static class Cookie {
        private String name = "refresh_token";
        private int maxAge = 2592000; // 30 days
        private boolean secure = false;
        private String sameSite = "Strict";
    }
}
