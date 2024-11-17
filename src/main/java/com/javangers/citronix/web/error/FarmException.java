package com.javangers.citronix.web.error;

public class FarmException {
    public static class MaxFieldsExceededException extends BusinessRuleViolationException {
        public MaxFieldsExceededException() {
            super("Maximum number of fields (10) exceeded for this farm", "MAX_FIELDS_EXCEEDED");
        }
    }

    public static class TotalAreaExceededException extends BusinessRuleViolationException {
        public TotalAreaExceededException() {
            super("Total area of fields exceeds farm area", "TOTAL_AREA_EXCEEDED");
        }
    }

    public static class DuplicateFarmNameException extends BusinessRuleViolationException {
        public DuplicateFarmNameException(String name) {
            super(String.format("Farm with name '%s' already exists in this region", name), "DUPLICATE_FARM_NAME");
        }
    }
}