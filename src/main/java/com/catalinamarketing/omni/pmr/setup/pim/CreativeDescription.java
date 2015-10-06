
package com.catalinamarketing.omni.pmr.setup.pim;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "creative_text_type_code",
    "creative_text_type_value"
})
public class CreativeDescription {

    @JsonProperty("creative_text_type_code")
    @SerializedName("creative_text_type_code")
    private String creativeTextTypeCode;
    @JsonProperty("creative_text_type_value")
    @SerializedName("creative_text_type_value")
    private String creativeTextTypeValue;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The creativeTextTypeCode
     */
    @JsonProperty("creative_text_type_code")
    public String getCreativeTextTypeCode() {
        return creativeTextTypeCode;
    }

    /**
     * 
     * @param creativeTextTypeCode
     *     The creative_text_type_code
     */
    @JsonProperty("creative_text_type_code")
    public void setCreativeTextTypeCode(String creativeTextTypeCode) {
        this.creativeTextTypeCode = creativeTextTypeCode;
    }

    public CreativeDescription withCreativeTextTypeCode(String creativeTextTypeCode) {
        this.creativeTextTypeCode = creativeTextTypeCode;
        return this;
    }

    /**
     * 
     * @return
     *     The creativeTextTypeValue
     */
    @JsonProperty("creative_text_type_value")
    public String getCreativeTextTypeValue() {
        return creativeTextTypeValue;
    }

    /**
     * 
     * @param creativeTextTypeValue
     *     The creative_text_type_value
     */
    @JsonProperty("creative_text_type_value")
    public void setCreativeTextTypeValue(String creativeTextTypeValue) {
        this.creativeTextTypeValue = creativeTextTypeValue;
    }

    public CreativeDescription withCreativeTextTypeValue(String creativeTextTypeValue) {
        this.creativeTextTypeValue = creativeTextTypeValue;
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

    public CreativeDescription withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }
}
