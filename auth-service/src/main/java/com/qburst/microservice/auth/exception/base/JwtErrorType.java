package com.qburst.microservice.auth.exception.base;

public enum JwtErrorType {
    EXPIRED,
    MALFORMED,
    UNSUPPORTED,
    SIGNATURE_INVALID,
    EMPTY,
    BLACKLISTED
}
