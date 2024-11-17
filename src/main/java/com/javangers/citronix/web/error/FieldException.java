package com.javangers.citronix.web.error;

public class FieldException {
    public static class FieldAreaTooSmallException extends BusinessRuleViolationException {
        public FieldAreaTooSmallException() {
            super("Field area must be at least 0.1 hectares", "FIELD_AREA_TOO_SMALL");
        }
    }

    public static class FieldAreaTooLargeException extends BusinessRuleViolationException {
        public FieldAreaTooLargeException() {
            super("Field area exceeds 50% of farm's total area", "FIELD_AREA_TOO_LARGE");
        }
    }

    public static class TreeDensityExceededException extends BusinessRuleViolationException {
        public TreeDensityExceededException() {
            super("Maximum tree density of 100 trees per hectare exceeded", "TREE_DENSITY_EXCEEDED");
        }
    }
}
