
package com.catalinamarketing.omni.pmr.setup.pim;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "trade_item_coding_std_code",
    "trade_item_code",
    "trade_item_owner_base_price_amount",
    "trade_item_owner_sale_price_amount",
    "trade_item_hierarchy_owner",
    "trade_item_hierarchy_level_5",
    "trade_item_hierarchy_level_4",
    "trade_item_hierarchy_level_3",
    "trade_item_hierarchy_level_2",
    "trade_item_hierarchy_level_1"
})
public class TradeItem {

    @JsonProperty("trade_item_coding_std_code")
    private String tradeItemCodingStdCode;
    @JsonProperty("trade_item_code")
    private String tradeItemCode;
    @JsonProperty("trade_item_owner_base_price_amount")
    private String tradeItemOwnerBasePriceAmount;
    @JsonProperty("trade_item_owner_sale_price_amount")
    private String tradeItemOwnerSalePriceAmount;
    @JsonProperty("trade_item_hierarchy_owner")
    private String tradeItemHierarchyOwner;
    @JsonProperty("trade_item_hierarchy_level_5")
    private String tradeItemHierarchyLevel5;
    @JsonProperty("trade_item_hierarchy_level_4")
    private String tradeItemHierarchyLevel4;
    @JsonProperty("trade_item_hierarchy_level_3")
    private String tradeItemHierarchyLevel3;
    @JsonProperty("trade_item_hierarchy_level_2")
    private String tradeItemHierarchyLevel2;
    @JsonProperty("trade_item_hierarchy_level_1")
    private String tradeItemHierarchyLevel1;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The tradeItemCodingStdCode
     */
    @JsonProperty("trade_item_coding_std_code")
    public String getTradeItemCodingStdCode() {
        return tradeItemCodingStdCode;
    }

    /**
     * 
     * @param tradeItemCodingStdCode
     *     The trade_item_coding_std_code
     */
    @JsonProperty("trade_item_coding_std_code")
    public void setTradeItemCodingStdCode(String tradeItemCodingStdCode) {
        this.tradeItemCodingStdCode = tradeItemCodingStdCode;
    }

    public TradeItem withTradeItemCodingStdCode(String tradeItemCodingStdCode) {
        this.tradeItemCodingStdCode = tradeItemCodingStdCode;
        return this;
    }

    /**
     * 
     * @return
     *     The tradeItemCode
     */
    @JsonProperty("trade_item_code")
    public String getTradeItemCode() {
        return tradeItemCode;
    }

    /**
     * 
     * @param tradeItemCode
     *     The trade_item_code
     */
    @JsonProperty("trade_item_code")
    public void setTradeItemCode(String tradeItemCode) {
        this.tradeItemCode = tradeItemCode;
    }

    public TradeItem withTradeItemCode(String tradeItemCode) {
        this.tradeItemCode = tradeItemCode;
        return this;
    }

    /**
     * 
     * @return
     *     The tradeItemOwnerBasePriceAmount
     */
    @JsonProperty("trade_item_owner_base_price_amount")
    public String getTradeItemOwnerBasePriceAmount() {
        return tradeItemOwnerBasePriceAmount;
    }

    /**
     * 
     * @param tradeItemOwnerBasePriceAmount
     *     The trade_item_owner_base_price_amount
     */
    @JsonProperty("trade_item_owner_base_price_amount")
    public void setTradeItemOwnerBasePriceAmount(String tradeItemOwnerBasePriceAmount) {
        this.tradeItemOwnerBasePriceAmount = tradeItemOwnerBasePriceAmount;
    }

    public TradeItem withTradeItemOwnerBasePriceAmout(String tradeItemOwnerBasePriceAmout) {
        this.tradeItemOwnerBasePriceAmount = tradeItemOwnerBasePriceAmout;
        return this;
    }

    /**
     * 
     * @return
     *     The tradeItemOwnerSalePriceAmount
     */
    @JsonProperty("trade_item_owner_sale_price_amount")
    public String getTradeItemOwnerSalePriceAmount() {
        return tradeItemOwnerSalePriceAmount;
    }

    /**
     * 
     * @param tradeItemOwnerSalePriceAmount
     *     The trade_item_owner_sale_price_amount
     */
    @JsonProperty("trade_item_owner_sale_price_amount")
    public void setTradeItemOwnerSalePriceAmount(String tradeItemOwnerSalePriceAmount) {
        this.tradeItemOwnerSalePriceAmount = tradeItemOwnerSalePriceAmount;
    }

    public TradeItem withTradeItemOwnerSalePriceAmount(String tradeItemOwnerSalePriceAmount) {
        this.tradeItemOwnerSalePriceAmount = tradeItemOwnerSalePriceAmount;
        return this;
    }

    /**
     * 
     * @return
     *     The tradeItemHierarchyOwner
     */
    @JsonProperty("trade_item_hierarchy_owner")
    public String getTradeItemHierarchyOwner() {
        return tradeItemHierarchyOwner;
    }

    /**
     * 
     * @param tradeItemHierarchyOwner
     *     The trade_item_hierarchy_owner
     */
    @JsonProperty("trade_item_hierarchy_owner")
    public void setTradeItemHierarchyOwner(String tradeItemHierarchyOwner) {
        this.tradeItemHierarchyOwner = tradeItemHierarchyOwner;
    }

    public TradeItem withTradeItemHierarchyOwner(String tradeItemHierarchyOwner) {
        this.tradeItemHierarchyOwner = tradeItemHierarchyOwner;
        return this;
    }

    /**
     * 
     * @return
     *     The tradeItemHierarchyLevel5
     */
    @JsonProperty("trade_item_hierarchy_level_5")
    public String getTradeItemHierarchyLevel5() {
        return tradeItemHierarchyLevel5;
    }

    /**
     * 
     * @param tradeItemHierarchyLevel5
     *     The trade_item_hierarchy_level_5
     */
    @JsonProperty("trade_item_hierarchy_level_5")
    public void setTradeItemHierarchyLevel5(String tradeItemHierarchyLevel5) {
        this.tradeItemHierarchyLevel5 = tradeItemHierarchyLevel5;
    }

    public TradeItem withTradeItemHierarchyLevel5(String tradeItemHierarchyLevel5) {
        this.tradeItemHierarchyLevel5 = tradeItemHierarchyLevel5;
        return this;
    }

    /**
     * 
     * @return
     *     The tradeItemHierarchyLevel4
     */
    @JsonProperty("trade_item_hierarchy_level_4")
    public String getTradeItemHierarchyLevel4() {
        return tradeItemHierarchyLevel4;
    }

    /**
     * 
     * @param tradeItemHierarchyLevel4
     *     The trade_item_hierarchy_level_4
     */
    @JsonProperty("trade_item_hierarchy_level_4")
    public void setTradeItemHierarchyLevel4(String tradeItemHierarchyLevel4) {
        this.tradeItemHierarchyLevel4 = tradeItemHierarchyLevel4;
    }

    public TradeItem withTradeItemHierarchyLevel4(String tradeItemHierarchyLevel4) {
        this.tradeItemHierarchyLevel4 = tradeItemHierarchyLevel4;
        return this;
    }

    /**
     * 
     * @return
     *     The tradeItemHierarchyLevel3
     */
    @JsonProperty("trade_item_hierarchy_level_3")
    public String getTradeItemHierarchyLevel3() {
        return tradeItemHierarchyLevel3;
    }

    /**
     * 
     * @param tradeItemHierarchyLevel3
     *     The trade_item_hierarchy_level_3
     */
    @JsonProperty("trade_item_hierarchy_level_3")
    public void setTradeItemHierarchyLevel3(String tradeItemHierarchyLevel3) {
        this.tradeItemHierarchyLevel3 = tradeItemHierarchyLevel3;
    }

    public TradeItem withTradeItemHierarchyLevel3(String tradeItemHierarchyLevel3) {
        this.tradeItemHierarchyLevel3 = tradeItemHierarchyLevel3;
        return this;
    }

    /**
     * 
     * @return
     *     The tradeItemHierarchyLevel2
     */
    @JsonProperty("trade_item_hierarchy_level_2")
    public String getTradeItemHierarchyLevel2() {
        return tradeItemHierarchyLevel2;
    }

    /**
     * 
     * @param tradeItemHierarchyLevel2
     *     The trade_item_hierarchy_level_2
     */
    @JsonProperty("trade_item_hierarchy_level_2")
    public void setTradeItemHierarchyLevel2(String tradeItemHierarchyLevel2) {
        this.tradeItemHierarchyLevel2 = tradeItemHierarchyLevel2;
    }

    public TradeItem withTradeItemHierarchyLevel2(String tradeItemHierarchyLevel2) {
        this.tradeItemHierarchyLevel2 = tradeItemHierarchyLevel2;
        return this;
    }

    /**
     * 
     * @return
     *     The tradeItemHierarchyLevel1
     */
    @JsonProperty("trade_item_hierarchy_level_1")
    public String getTradeItemHierarchyLevel1() {
        return tradeItemHierarchyLevel1;
    }

    /**
     * 
     * @param tradeItemHierarchyLevel1
     *     The trade_item_hierarchy_level_1
     */
    @JsonProperty("trade_item_hierarchy_level_1")
    public void setTradeItemHierarchyLevel1(String tradeItemHierarchyLevel1) {
        this.tradeItemHierarchyLevel1 = tradeItemHierarchyLevel1;
    }

    public TradeItem withTradeItemHierarchyLevel1(String tradeItemHierarchyLevel1) {
        this.tradeItemHierarchyLevel1 = tradeItemHierarchyLevel1;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public TradeItem withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
