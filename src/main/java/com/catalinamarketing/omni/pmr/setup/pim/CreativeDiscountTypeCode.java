package com.catalinamarketing.omni.pmr.setup.pim;

/**
 * @author Sekou Batchelor <sbatchel@catmktg.com>
 */
public enum CreativeDiscountTypeCode {

    LOYALTY_POINTS("Loyalty Points"),
    PERCENTAGE("Percentage"),
    MONETARY_VALUE("Monetary value"),
    FREE_TRADE_ITEM("Free trade item");

    private String displayName;

    CreativeDiscountTypeCode(String displayName) {
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
