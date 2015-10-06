
package com.catalinamarketing.omni.pmr.setup.pim;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "attributes"
})
public class GeoLoc {

    @JsonProperty("attributes")
    @SerializedName("attributes")
    private List<Attribute> attributes;

    /**
     * 
     * @return
     *     The attributes
     */
    @JsonProperty("attributes")
    public List<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * 
     * @param attributes
     *     The attributes
     */
    @JsonProperty("attributes")
    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public GeoLoc withAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
        return this;
    }

}
