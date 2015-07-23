package com.catalinamarketing.omni.pmr.setup;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class CampaignOfferSetupMessage {
	private String countryCode = new String("US");
	@SerializedName(value="award")
	private List<AwardRank> awardRankList;
	
	@SerializedName(value="campaign")
	private List<Campaign> campaignList;
	
	public void addAwardRank(AwardRank awardRank) {
		if(awardRankList == null) {
			awardRankList = new ArrayList<AwardRank>();
		}
		awardRankList.add(awardRank);
	}
	
	public void addCampaign(Campaign campaign) {
		if(campaignList == null) {
			campaignList = new ArrayList<Campaign>();
		}
		campaignList.add(campaign);
	}

	public List<AwardRank> getAwardRankList() {
		return awardRankList;
	}

	public void setAwardRankList(List<AwardRank> awardRankList) {
		this.awardRankList = awardRankList;
	}

	public List<Campaign> getCampaignList() {
		return campaignList;
	}

	public void setCampaignList(List<Campaign> campaignList) {
		this.campaignList = campaignList;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	
}
