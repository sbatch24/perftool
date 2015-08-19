package com.catalinamarketing.omni.api;


import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "stringPrint")
public class StringPrint extends BaseMedia {

    @XmlElement(required = true)
    protected String campaignId;

    @XmlElementWrapper(name = "offers")
    @XmlElement(name = "offer")
    protected List<Offer> offers;

    @XmlElementWrapper(name = "metaData")
    @XmlElement(name = "metaDataItem")
    protected List<MetaDataItem> metaData;

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public List<Offer> getOffers() {
        if (offers == null) {
            this.offers = new ArrayList<>();
        }
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public List<MetaDataItem> getMetaData() {
        if (metaData == null) {
            this.metaData = new ArrayList<>();
        }
        return metaData;
    }

    public void setMetaData(List<MetaDataItem> metaData) {
        this.metaData = metaData;
    }

    @Override
    public String toString() {
        return "StringPrint{" +
            "campaignId='" + campaignId + '\'' +
            ", offers=" + offers +
            ", metaData=" + metaData +
            '}';
    }
}
