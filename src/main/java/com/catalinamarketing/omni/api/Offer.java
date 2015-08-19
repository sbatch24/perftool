package com.catalinamarketing.omni.api;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an offer.
 * <p/>
 * Created by mgarland on 12/18/2014.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "offer")
public class Offer {

    @XmlElements({@XmlElement(name = "source_id", required = true),
                  @XmlElement(name = "offerId", required = true)})
    protected String offerId;

    @XmlElementWrapper(name = "metaData")
    @XmlElement(name = "metaDataItem")
    protected List<MetaDataItem> metaData;

    public Offer() {
    }

    public Offer(String offerId) {
        this.offerId = offerId;
    }

    /**
     * @return the offerId
     */
    public String getOfferId() {
        return offerId;
    }


    /**
     * @param offerId the offerId to set
     */
    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    /**
     * @return list of {@link MetaDataItem}
     */
    public List<MetaDataItem> getMetaData() {
        if (metaData == null) {
            metaData = new ArrayList<>();
        }
        return this.metaData;
    }

    @Override
    public String toString() {
        return "Offer{" +
            "offerId='" + offerId + '\'' +
            ", metaData=" + metaData +
            '}';
    }
}