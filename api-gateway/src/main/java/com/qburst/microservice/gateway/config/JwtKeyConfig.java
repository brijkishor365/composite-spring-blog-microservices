package com.qburst.microservice.gateway.config;

import com.qburst.microservice.gateway.security.KeyLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.PublicKey;

@Configuration
public class JwtKeyConfig {

    @Bean
    public PublicKey publicKey() throws Exception {
        return KeyLoader.loadPublicKey("public.pem");
    }
}
