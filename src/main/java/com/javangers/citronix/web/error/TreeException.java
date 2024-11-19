package com.javangers.citronix.web.error;

public class TreeException {
    public static class InvalidPlantingSeasonException extends BusinessRuleViolationException {
        public InvalidPlantingSeasonException() {
            super("Trees can only be planted between March and May", "INVALID_PLANTING_SEASON");
        }
    }

    public static class TreeSpacingViolationException extends BusinessRuleViolationException {
        public TreeSpacingViolationException() {
            super("Minimum tree spacing requirement not met", "TREE_SPACING_VIOLATION");
        }
    }

    public static class TreeAgeExceededException extends BusinessRuleViolationException {
        public TreeAgeExceededException() {
            super("Tree age exceeds maximum productive age of 20 years", "TREE_AGE_EXCEEDED");
        }
    }
}