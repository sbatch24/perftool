package com.catalinamarketing.omni.pmr.setup;


public class AwardRank {
	
    private String awardId;
    private int rank;
    private String campaignId;
    
    public AwardRank() {
    }
    
    public AwardRank(String awardId, int rank, String campaignId) {
    	this.awardId = awardId;
    	this.rank = rank;
    	this.campaignId = campaignId;
    }
    
	public String getAwardId() {
		return awardId;
	}
	public void setAwardId(String awardId) {
		this.awardId = awardId;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public String getCampaignId() {
		return campaignId;
	}
	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}
    
    

}
