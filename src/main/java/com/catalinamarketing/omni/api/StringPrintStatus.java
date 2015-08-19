package com.catalinamarketing.omni.api;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;


public class StringPrintStatus {
	
	private static final String PRINTED_AT_LEAST_ONE_OFFER = "T";

    @XmlElement(required = true)
    protected String awardId;

    @XmlElement(required = true)
    protected String status;

    @XmlElementWrapper(name = "offerStatuses")
    @XmlElement(name = "offerStatus")
    protected List<OfferStatus> offerStatuses;

    @XmlElement(required = true)
    protected String mediaNumber;

    @XmlElement(required = false)
    private String sourceType;

    public String getAwardId() {
        return awardId;
    }

    public void setAwardId(String awardId) {
        this.awardId = awardId;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the mediaNumber
     */
    public String getMediaNumber() {
        return mediaNumber;
    }

    /**
     * @param mediaNumber the mediaNumber to set
     */
    public void setMediaNumber(String mediaNumber) {
        this.mediaNumber = mediaNumber;
    }

    public void setOfferStatuses(List<OfferStatus> offerStatuses) {
        this.offerStatuses = offerStatuses;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    /**
     * @return list of offer statuses
     */
    public List<OfferStatus> getOfferStatuses() {
        if (offerStatuses == null) {
            offerStatuses = new ArrayList<>();
        }
        return this.offerStatuses;
    }
    
    public void addOfferStatus(OfferStatus offerStatus) {
    	if(offerStatuses == null) {
    		offerStatuses = new ArrayList<>();
    	}
    	offerStatuses.add(offerStatus);
    }
}
