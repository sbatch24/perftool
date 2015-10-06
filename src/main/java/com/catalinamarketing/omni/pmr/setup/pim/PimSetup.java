
package com.catalinamarketing.omni.pmr.setup.pim;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "promotion_id",
    "promotion_status",
    "legal_entity_id",
    "legal_entity_subsidiary_id",
    "promotion_distribution_start_date",
    "promotion_distribution_end_date",
    "creative_image_graphic_rank_number",
    "promo_name",
    "geo_loc",
    "creative_description",
    "promotion_graphics_details",
    "trigger_purchase_event",
    "trade_items"
})
public class PimSetup {

    @JsonProperty("promotion_id")
    @SerializedName("promotion_id")
    private String promotionId;
    @JsonProperty("promotion_status")
    @SerializedName("promotion_status")
    private String promotionStatus;
    @JsonProperty("legal_entity_id")
    @SerializedName("legal_entity_id")
    private String legalEntityId;
    @JsonProperty("legal_entity_subsidiary_id")
    @SerializedName("legal_entity_subsidiary_id")
    private String legalEntitySubsidiaryId;
    @JsonProperty("promotion_distribution_start_date")
    @SerializedName("promotion_distribution_start_date")
    private String promotionDistributionStartDate;
    @JsonProperty("promotion_distribution_end_date")
    @SerializedName("promotion_distribution_end_date")
    private String promotionDistributionEndDate;
    @JsonProperty("creative_image_graphic_rank_number")
    @SerializedName("creative_image_graphic_rank_number")
    private String creativeImageGraphicRankNumber;
    @JsonProperty("promo_name")
    @SerializedName("promo_name")
    private String promoName;
    @JsonProperty("geo_loc")
    @SerializedName("geo_loc")
    private GeoLoc geoLoc;
    @JsonProperty("creative_description")
    @SerializedName("creative_description")
    private List<CreativeDescription> creativeDescription;
    @JsonProperty("promotion_graphics_details")
    @SerializedName("promotion_graphics_details")
    private PromotionGraphicsDetail promotionGraphicsDetails;
    @JsonProperty("trigger_purchase_event")
    @SerializedName("trigger_purchase_event")
    private List<TriggerPurchaseEvent> triggerPurchaseEvent;
    @JsonProperty("trade_items")
    @SerializedName("trade_items")
    private List<TradeItem> tradeItems;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The promotionId
     */
    @JsonProperty("promotion_id")
    public String getPromotionId() {
        return promotionId;
    }

    /**
     * 
     * @param promotionId
     *     The promotion_id
     */
    @JsonProperty("promotion_id")
    public void setPromotionId(String promotionId) {
        this.promotionId = promotionId;
    }

    public PimSetup withPromotionId(String promotionId) {
        this.promotionId = promotionId;
        return this;
    }

    /**
     *
     * @return
     *     The promotionStatus
     */
    @JsonProperty("promotion_status")
    public String getPromotionStatus() {
        return promotionStatus;
    }

    /**
     *
     * @param promotionStatus
     *     The promotionStatus
     */
    @JsonProperty("promotion_status")
    public void setPromotionStatus(String promotionStatus) {
        this.promotionStatus = promotionStatus;
    }

    public PimSetup withPromotionStatus(String promotionStatus) {
        this.promotionStatus = promotionStatus;
        return this;
    }

    /**
     * 
     * @return
     *     The legalEntityId
     */
    @JsonProperty("legal_entity_id")
    public String getLegalEntityId() {
        return legalEntityId;
    }

    /**
     * 
     * @param legalEntityId
     *     The legal_entity_id
     */
    @JsonProperty("legal_entity_id")
    public void setLegalEntityId(String legalEntityId) {
        this.legalEntityId = legalEntityId;
    }

    public PimSetup withLegalEntityId(String legalEntityId) {
        this.legalEntityId = legalEntityId;
        return this;
    }

    /**
     * 
     * @return
     *     The legalEntitySubsidiaryId
     */
    @JsonProperty("legal_entity_subsidiary_id")
    public String getLegalEntitySubsidiaryId() {
        return legalEntitySubsidiaryId;
    }

    /**
     * 
     * @param legalEntitySubsidiaryId
     *     The legal_entity_subsidiary_id
     */
    @JsonProperty("legal_entity_subsidiary_id")
    public void setLegalEntitySubsidiaryId(String legalEntitySubsidiaryId) {
        this.legalEntitySubsidiaryId = legalEntitySubsidiaryId;
    }

    public PimSetup withLegalEntitySubsidiaryId(String legalEntitySubsidiaryId) {
        this.legalEntitySubsidiaryId = legalEntitySubsidiaryId;
        return this;
    }

    /**
     * 
     * @return
     *     The promotionDistributionStartDate
     */
    @JsonProperty("promotion_distribution_start_date")
    public String getPromotionDistributionStartDate() {
        return promotionDistributionStartDate;
    }

    /**
     * 
     * @param promotionDistributionStartDate
     *     The promotion_distribution_start_date
     */
    @JsonProperty("promotion_distribution_start_date")
    public void setPromotionDistributionStartDate(String promotionDistributionStartDate) {
        this.promotionDistributionStartDate = promotionDistributionStartDate;
    }

    public PimSetup withPromotionDistributionStartDate(String promotionDistributionStartDate) {
        this.promotionDistributionStartDate = promotionDistributionStartDate;
        return this;
    }

    /**
     * 
     * @return
     *     The promotionDistributionEndDate
     */
    @JsonProperty("promotion_distribution_end_date")
    public String getPromotionDistributionEndDate() {
        return promotionDistributionEndDate;
    }

    /**
     * 
     * @param promotionDistributionEndDate
     *     The promotion_distribution_end_date
     */
    @JsonProperty("promotion_distribution_end_date")
    public void setPromotionDistributionEndDate(String promotionDistributionEndDate) {
        this.promotionDistributionEndDate = promotionDistributionEndDate;
    }

    public PimSetup withPromotionDistributionEndDate(String promotionDistributionEndDate) {
        this.promotionDistributionEndDate = promotionDistributionEndDate;
        return this;
    }

    /**
     * 
     * @return
     *     The creativeImageGraphicRankNumber
     */
    @JsonProperty("creative_image_graphic_rank_number")
    public String getCreativeImageGraphicRankNumber() {
        return creativeImageGraphicRankNumber;
    }

    /**
     * 
     * @param creativeImageGraphicRankNumber
     *     The creative_image_graphic_rank_number
     */
    @JsonProperty("creative_image_graphic_rank_number")
    public void setCreativeImageGraphicRankNumber(String creativeImageGraphicRankNumber) {
        this.creativeImageGraphicRankNumber = creativeImageGraphicRankNumber;
    }

    public PimSetup withCreativeImageGraphicRankNumber(String creativeImageGraphicRankNumber) {
        this.creativeImageGraphicRankNumber = creativeImageGraphicRankNumber;
        return this;
    }

    /**
     * 
     * @return
     *     The promoName
     */
    @JsonProperty("promo_name")
    public String getPromoName() {
        return promoName;
    }

    /**
     * 
     * @param promoName
     *     The promo_name
     */
    @JsonProperty("promo_name")
    public void setPromoName(String promoName) {
        this.promoName = promoName;
    }

    public PimSetup withPromoName(String promoName) {
        this.promoName = promoName;
        return this;
    }

    /**
     * 
     * @return
     *     The geoLoc
     */
    @JsonProperty("geo_loc")
    public GeoLoc getGeoLoc() {
        return geoLoc;
    }

    /**
     * 
     * @param geoLoc
     *     The geo_loc
     */
    @JsonProperty("geo_loc")
    public void setGeoLoc(GeoLoc geoLoc) {
        this.geoLoc = geoLoc;
    }

    public PimSetup withGeoLoc(GeoLoc geoLoc) {
        this.geoLoc = geoLoc;
        return this;
    }

    /**
     * 
     * @return
     *     The creativeDescription
     */
    @JsonProperty("creative_description")
    public List<CreativeDescription> getCreativeDescription() {
        return creativeDescription;
    }

    /**
     * 
     * @param creativeDescription
     *     The creative_description
     */
    @JsonProperty("creative_description")
    public void setCreativeDescription(List<CreativeDescription> creativeDescription) {
        this.creativeDescription = creativeDescription;
    }

    public PimSetup withCreativeDescription(List<CreativeDescription> creativeDescription) {
        this.creativeDescription = creativeDescription;
        return this;
    }

    /**
     *
     * @return
     *     The promotionGraphicsDetails
     */
    @JsonProperty("promotion_graphics_details")
    public PromotionGraphicsDetail getPromotionGraphicsDetails() {
        return promotionGraphicsDetails;
    }

    /**
     *
     * @param promotionGraphicsDetails
     *     The promotionGraphicsDetails
     */
    @JsonProperty("promotion_graphics_details")
    public void setPromotionGraphicsDetails(PromotionGraphicsDetail promotionGraphicsDetails) {
        this.promotionGraphicsDetails = promotionGraphicsDetails;
    }

    public PimSetup withPromotionGraphicsDetail(PromotionGraphicsDetail promotionGraphicsDetail) {
        this.promotionGraphicsDetails = promotionGraphicsDetail;
        return this;
    }

    /**
     * 
     * @return
     *     The triggerPurchaseEvent
     */
    @JsonProperty("trigger_purchase_event")
    public List<TriggerPurchaseEvent> getTriggerPurchaseEvent() {
        return triggerPurchaseEvent;
    }

    /**
     * 
     * @param triggerPurchaseEvent
     *     The trigger_purchase_event
     */
    @JsonProperty("trigger_purchase_event")
    public void setTriggerPurchaseEvent(List<TriggerPurchaseEvent> triggerPurchaseEvent) {
        this.triggerPurchaseEvent = triggerPurchaseEvent;
    }

    public PimSetup withTriggerPurchaseEvent(List<TriggerPurchaseEvent> triggerPurchaseEvent) {
        this.triggerPurchaseEvent = triggerPurchaseEvent;
        return this;
    }

    /**
     * 
     * @return
     *     The tradeItems
     */
    @JsonProperty("trade_items")
    public List<TradeItem> getTradeItems() {
        return tradeItems;
    }

    /**
     * 
     * @param tradeItems
     *     The trade_item
     */
    @JsonProperty("trade_items")
    public void setTradeItems(List<TradeItem> tradeItems) {
        this.tradeItems = tradeItems;
    }

    public PimSetup withTradeItem(List<TradeItem> tradeItem) {
        this.tradeItems = tradeItem;
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

    public PimSetup withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }
}
