package com.javangers.citronix.web.error;

import lombok.Getter;

@Getter
public class CitronixException extends RuntimeException {
    private final String code;

    public CitronixException(String message, String code) {
        super(message);
        this.code = code;
    }

}