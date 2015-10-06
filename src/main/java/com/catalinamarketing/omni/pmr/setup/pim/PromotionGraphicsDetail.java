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
        "creative_external_url",
        "creative_internal_url",
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
    @JsonProperty("creative_external_url")
    @SerializedName("creative_external_url")
    protected String creativeExternalUrl;
    @JsonProperty("creative_internal_url")
    @SerializedName("creative_internal_url")
    protected String creativeInternalUrl;
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

    @JsonProperty("creative_external_url")
    public String getCreativeExternalUrl() {
        return creativeExternalUrl;
    }

    @JsonProperty("creative_external_url")
    public void setCreativeExternalUrl(String creativeExternalUrl) {
        this.creativeExternalUrl = creativeExternalUrl;
    }

    @JsonProperty("creative_internal_url")
    public String getCreativeInternalUrl() {
        return creativeInternalUrl;
    }

    @JsonProperty("creative_internal_url")
    public void setCreativeInternalUrl(String creativeInternalUrl) {
        this.creativeInternalUrl = creativeInternalUrl;
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
