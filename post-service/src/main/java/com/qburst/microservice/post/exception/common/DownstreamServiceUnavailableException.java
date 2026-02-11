package com.qburst.microservice.post.exception.common;

import com.qburst.microservice.post.exception.base.ApplicationException;
import org.springframework.http.HttpStatus;

public class DownstreamServiceUnavailableException extends ApplicationException {
    public DownstreamServiceUnavailableException(String message) {
        super(message, "Downstream service is down!", HttpStatus.SERVICE_UNAVAILABLE);
    }
}
