package com.catalinamarketing.omni.pmr.setup.pim;

/**
 * @author Sekou Batchelor <sbatchel@catmktg.com>
 */
public enum TriggerType {

    PURCHASE_EVENT("purchase_event"),
    PROMOTION_EVENT("promotion_event"),
    BEACON_EVENT("beacon_event"),
    CHECKOUT_EVENT("checkout_event");

    private String displayName;

    TriggerType(String displayName) {
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
