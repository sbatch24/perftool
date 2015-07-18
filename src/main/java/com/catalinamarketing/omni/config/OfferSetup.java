package com.catalinamarketing.omni.config;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * @author achavan
 * Represents the offer setup configuration.
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="offerSetup")
public class OfferSetup {
	private String campaignId;

	@XmlElementWrapper(name="offers")
	@XmlElement(name="offer")
	private List<String> offerList;

	public List<String> getOfferList() {
		return offerList;
	}

	public void setOfferList(List<String> offerList) {
		this.offerList = offerList;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}
}
