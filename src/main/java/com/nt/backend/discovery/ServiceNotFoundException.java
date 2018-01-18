package com.nt.backend.discovery;

import static java.lang.String.format;

public class ServiceNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Service '%s' not found!";

    public ServiceNotFoundException(String serviceId) {
        super(format(MESSAGE, serviceId));
    }
}
