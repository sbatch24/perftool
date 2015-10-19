package com.catalinamarketing.omni.pmr.setup.pim;

/**
 * @author Sekou Batchelor <sbatchel@catmktg.com>
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.annotations.SerializedName;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "creative_external_id",
        "creative_internal_id",
        "creative_external_value",
        "creative_internal_value",
        "creative_external_source_system_name",
        "creative_internal_source_system_name",
        "channel_type"
})
public class PromotionGraphicsDetail {

    @JsonProperty("creative_external_id")
    @SerializedName("creative_external_id")
    protected String creativeExternalId;
    @JsonProperty("creative_internal_id")
    @SerializedName("creative_internal_id")
    protected String creativeInternalId;
    @JsonProperty("creative_external_value")
    @SerializedName("creative_external_value")
    protected String creativeExternalValue;
    @JsonProperty("creative_internal_value")
    @SerializedName("creative_internal_value")
    protected String creativeInternalValue;
    @JsonProperty("creative_external_source_system_name")
    @SerializedName("creative_external_source_system_name")
    protected String creativeExternalSourceSystemName;
    @JsonProperty("creative_internal_source_system_name")
    @SerializedName("creative_internal_source_system_name")
    protected String creativeInternalSourceSystemName;
    @JsonProperty("channel_type")
    @SerializedName("channel_type")
    protected String channelType;

    @JsonProperty("creative_external_id")
    public String getCreativeExternalId() {
        return creativeExternalId;
    }

    @JsonProperty("creative_external_id")
    public void setCreativeExternalId(String creativeExternalId) {
        this.creativeExternalId = creativeExternalId;
    }

    @JsonProperty("creative_internal_id")
    public String getCreativeInternalId() {
        return creativeInternalId;
    }

    @JsonProperty("creative_internal_id")
    public void setCreativeInternalId(String creativeInternalId) {
        this.creativeInternalId = creativeInternalId;
    }

    @JsonProperty("creative_external_value")
    public String getCreativeExternalValue() {
        return creativeExternalValue;
    }

    @JsonProperty("creative_external_value")
    public void setCreativeExternalValue(String creativeExternalValue) {
        this.creativeExternalValue = creativeExternalValue;
    }

    @JsonProperty("creative_internal_value")
    public String getCreativeInternalValue() {
        return creativeInternalValue;
    }

    @JsonProperty("creative_internal_value")
    public void setCreativeInternalValue(String creativeInternalValue) {
        this.creativeInternalValue = creativeInternalValue;
    }

    @JsonProperty("creative_external_source_system_name")
    public String getCreativeExternalSourceSystemName() {
        return creativeExternalSourceSystemName;
    }

    @JsonProperty("creative_external_source_system_name")
    public void setCreativeExternalSourceSystemName(String creativeExternalSourceSystemName) {
        this.creativeExternalSourceSystemName = creativeExternalSourceSystemName;
    }

    @JsonProperty("creative_internal_source_system_name")
    public String getCreativeInternalSourceSystemName() {
        return creativeInternalSourceSystemName;
    }

    @JsonProperty("creative_internal_source_system_name")
    public void setCreativeInternalSourceSystemName(String creativeInternalSourceSystemName) {
        this.creativeInternalSourceSystemName = creativeInternalSourceSystemName;
    }

    @JsonProperty("channel_type")
    public String getChannelType() {
        return channelType;
    }

    @JsonProperty("channel_type")
    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }
}
