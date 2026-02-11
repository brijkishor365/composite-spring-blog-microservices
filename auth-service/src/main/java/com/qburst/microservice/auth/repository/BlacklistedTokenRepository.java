package com.qburst.microservice.auth.repository;

import com.qburst.microservice.auth.document.BlacklistedToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlacklistedTokenRepository
        extends MongoRepository<BlacklistedToken, String> {

    boolean existsByToken(String token);
}
