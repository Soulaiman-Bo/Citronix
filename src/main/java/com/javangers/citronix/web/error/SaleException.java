package com.javangers.citronix.web.error;

public class SaleException {
    public static class InsufficientHarvestQuantityException extends BusinessRuleViolationException {
        public InsufficientHarvestQuantityException() {
            super("Insufficient harvest quantity available for sale", "INSUFFICIENT_HARVEST_QUANTITY");
        }
    }

    public static class InvalidSalePriceException extends BusinessRuleViolationException {
        public InvalidSalePriceException() {
            super("Sale price deviates too much from market average", "INVALID_SALE_PRICE");
        }
    }

    public static class SaleStorageTimeExceededException extends BusinessRuleViolationException {
        public SaleStorageTimeExceededException() {
            super("Maximum storage time before sale (30 days) exceeded", "STORAGE_TIME_EXCEEDED");
        }
    }
}
