package com.app.secure_api_gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class CryptoConfig {

    @Value("${crypto.aes.key}")
    private String aesKey;

    public String getAesKey() {
        return aesKey;
    }
}
