package com.catalinamarketing.omni.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class OfferStatus {

    @XmlElement(required = true)
    protected String offerId;

    @XmlElement(required = true)
    protected String status;

    /**
     * @return the offerId
     */
    public String getOfferId() {
        return offerId;
    }

    /**
     * @param offerId
     *            the offerId to set
     */
    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

}
