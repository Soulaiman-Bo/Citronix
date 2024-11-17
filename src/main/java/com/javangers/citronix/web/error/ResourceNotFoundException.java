package com.javangers.citronix.web.error;

public class ResourceNotFoundException extends CitronixException {
    public ResourceNotFoundException(String resource, String id) {
        super(String.format("%s not found with id: %s", resource, id), "RESOURCE_NOT_FOUND");
    }
}
