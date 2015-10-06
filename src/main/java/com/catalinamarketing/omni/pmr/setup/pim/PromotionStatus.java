package com.catalinamarketing.omni.pmr.setup.pim;

/**
 * @author Sekou Batchelor <sbatchel@catmktg.com>
 */
public enum PromotionStatus {

    ACTIVE("Active"),
    PUBLISHED("Published"),
    PAUSED("Paused"),
    PENDING_APPROVAL("Pending approval"),
    REJECTED("Rejected"),
    DRAFT("Draft"),
    EXPIRED("Expired"),
    DELETED("Deleted"),
    CAPPED("Capped");

    private String displayName;

    PromotionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
