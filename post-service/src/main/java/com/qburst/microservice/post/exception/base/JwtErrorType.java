package com.qburst.microservice.post.exception.base;

public enum JwtErrorType {
    EXPIRED,
    MALFORMED,
    UNSUPPORTED,
    SIGNATURE_INVALID,
    EMPTY,
    BLACKLISTED
}
