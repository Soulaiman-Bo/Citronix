package com.javangers.citronix.web.error;

public class BusinessRuleViolationException extends CitronixException {
    public BusinessRuleViolationException(String message, String code) {
        super(message, code);
    }
}