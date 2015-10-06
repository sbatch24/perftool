package com.catalinamarketing.omni.pmr.setup.pim;

/**
 * @author Sekou Batchelor <sbatchel@catmktg.com>
 */
public enum TradeItemCodingStdCode {

    UPC("upc"),
    GTIN("gtin"),
    EAN("ean"),
    PLU("plu"),
    SKU("sku");

    private String displayName;

    TradeItemCodingStdCode(String displayName) {
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
