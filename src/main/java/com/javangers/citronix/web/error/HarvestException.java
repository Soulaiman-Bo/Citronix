package com.javangers.citronix.web.error;

import java.util.UUID;

public class HarvestException {
    public static class DuplicateSeasonHarvestException extends BusinessRuleViolationException {
        public DuplicateSeasonHarvestException(String season) {
            super(String.format("A harvest already exists for season: %s", season), "DUPLICATE_SEASON_HARVEST");
        }
    }

    public static class TreeAlreadyHarvestedException extends BusinessRuleViolationException {
        public TreeAlreadyHarvestedException(UUID treeId) {
            super(String.format("Tree %d already harvested this season", treeId), "TREE_ALREADY_HARVESTED");
        }
    }

    public static class InvalidHarvestQuantityException extends BusinessRuleViolationException {
        public InvalidHarvestQuantityException() {
            super("Invalid harvest quantity for tree's age and capacity", "INVALID_HARVEST_QUANTITY");
        }
    }
}
