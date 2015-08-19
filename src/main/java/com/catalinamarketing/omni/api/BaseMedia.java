package com.catalinamarketing.omni.api;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonInclude;

@XmlAccessorType(XmlAccessType.FIELD)
public class BaseMedia {

    @XmlElement(required = true)
    protected String awardId;

    @XmlElement
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Boolean controlled;

    public String getAwardId() {
        return awardId;
    }

    public void setAwardId(String awardId) {
        this.awardId = awardId;
    }

    public Boolean isControlled() {
        return controlled;
    }

    public void setControlled(Boolean controlled) {
        this.controlled = controlled;
    }

    @Override
    public String toString() {
        return "BaseMedia{" +
            "awardId='" + awardId + '\'' +
            ", controlled=" + controlled +
            '}';
    }
}