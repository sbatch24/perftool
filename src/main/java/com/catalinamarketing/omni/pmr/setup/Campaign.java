package com.catalinamarketing.omni.pmr.setup;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;


public class Campaign {
	@SerializedName(value="offerId")
    private List<String> offers = new ArrayList<String>();
    private String campaignId;
    
	public List<String> getOffers() {
		return offers;
	}
	public void setOffers(List<String> offerIds) {
		this.offers = offerIds;
	}
	public String getCampaignId() {
		return campaignId;
	}
	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}
    
	public void addOffers(String offer) {
		if(offers == null) {
			offers = new ArrayList<String>();
		}
		offers.add(offer);
	}
}
