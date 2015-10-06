
package com.catalinamarketing.omni.pmr.setup.pim;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.annotations.SerializedName;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "trigger_type",
    "trigger_trade_item_amount",
    "creative_discount_type_code",
    "creative_discount_value_amount",
    "description"
})
public class TriggerPurchaseEvent {

    @JsonProperty("trigger_type")
    @SerializedName("trigger_type")
    private String triggerType;
    @JsonProperty("trigger_trade_item_amount")
    @SerializedName("trigger_trade_item_amount")
    private String triggerTradeItemAmount;
    @JsonProperty("creative_discount_type_code")
    @SerializedName("creative_discount_type_code")
    private String creativeDiscountTypeCode;
    @JsonProperty("creative_discount_value_amount")
    @SerializedName("creative_discount_value_amount")
    private String creativeDiscountValueAmount;
    @JsonProperty("description")
    @SerializedName("description")
    private String description;

    /**
     * 
     * @return
     *     The triggerType
     */
    @JsonProperty("trigger_type")
    public String getTriggerType() {
        return triggerType;
    }

    /**
     * 
     * @param triggerType
     *     The trigger_type
     */
    @JsonProperty("trigger_type")
    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public TriggerPurchaseEvent withTriggerType(String triggerType) {
        this.triggerType = triggerType;
        return this;
    }

    /**
     * 
     * @return
     *     The triggerTradeItemAmount
     */
    @JsonProperty("trigger_trade_item_amount")
    public String getTriggerTradeItemAmount() {
        return triggerTradeItemAmount;
    }

    /**
     * 
     * @param triggerTradeItemAmount
     *     The trigger_trade_item_amount
     */
    @JsonProperty("trigger_trade_item_amount")
    public void setTriggerTradeItemAmount(String triggerTradeItemAmount) {
        this.triggerTradeItemAmount = triggerTradeItemAmount;
    }

    public TriggerPurchaseEvent withTriggerTradeItemAmount(String triggerTradeItemAmount) {
        this.triggerTradeItemAmount = triggerTradeItemAmount;
        return this;
    }

    /**
     * 
     * @return
     *     The creativeDiscountTypeCode
     */
    @JsonProperty("creative_discount_type_code")
    public String getCreativeDiscountTypeCode() {
        return creativeDiscountTypeCode;
    }

    /**
     * 
     * @param creativeDiscountTypeCode
     *     The creative_discount_type_code
     */
    @JsonProperty("creative_discount_type_code")
    public void setCreativeDiscountTypeCode(String creativeDiscountTypeCode) {
        this.creativeDiscountTypeCode = creativeDiscountTypeCode;
    }

    public TriggerPurchaseEvent withCreativeDiscountTypeCode(String creativeDiscountTypeCode) {
        this.creativeDiscountTypeCode = creativeDiscountTypeCode;
        return this;
    }

    /**
     * 
     * @return
     *     The creativeDiscountValueAmount
     */
    @JsonProperty("creative_discount_value_amount")
    public String getCreativeDiscountValueAmount() {
        return creativeDiscountValueAmount;
    }

    /**
     * 
     * @param creativeDiscountValueAmount
     *     The creative_discount_value_amount
     */
    @JsonProperty("creative_discount_value_amount")
    public void setCreativeDiscountValueAmount(String creativeDiscountValueAmount) {
        this.creativeDiscountValueAmount = creativeDiscountValueAmount;
    }

    public TriggerPurchaseEvent withCreativeDiscountValueAmount(String creativeDiscountValueAmount) {
        this.creativeDiscountValueAmount = creativeDiscountValueAmount;
        return this;
    }

    /**
     * 
     * @return
     *     The description
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     * 
     * @param description
     *     The description
     */
    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    public TriggerPurchaseEvent withDescription(String description) {
        this.description = description;
        return this;
    }

}
