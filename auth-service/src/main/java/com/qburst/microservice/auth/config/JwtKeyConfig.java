package com.qburst.microservice.auth.config;

import com.qburst.microservice.auth.security.KeyLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.PrivateKey;
import java.security.PublicKey;

@Configuration
public class JwtKeyConfig {

    @Bean
    public PrivateKey privateKey() throws Exception {
        return KeyLoader.loadPrivateKey("private.pem");
    }

    @Bean
    public PublicKey publicKey() throws Exception {
        return KeyLoader.loadPublicKey("public.pem");
    }
}
