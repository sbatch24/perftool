package com.catalinamarketing.omni.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="promotionSetup")
public class PromotionSetup {
	private Integer awardId;
	private Integer rank;
	private Integer mediaId;
	private Integer awardCap;
	private Integer awardVariance;
	private Integer mediaCap;
	private Integer mediaVariance;
	private Integer channelMediaId;
	private Integer channelMediaCap;
	private Integer channelMediaVariance;
	private String channelType;
	private String cardRangeId;
	private int consumerCap;
	private int thresholdSequence;
	private boolean houseHolded = false;
	private boolean unlimited = false;
	private String promotionType = new String ("Transactional");
	private String campaignId;
	private String programSetupId;
	private Integer controlPercentage;
	private Integer randomValue;
	private String startDate;
	private String endDate;
	
	
	public Integer getAwardId() {
		return awardId;
	}

	public void setAwardId(Integer awardId) {
		this.awardId = awardId;
	}

	public Integer getMediaId() {
		return mediaId;
	}

	public void setMediaId(Integer mediaId) {
		this.mediaId = mediaId;
	}

	public Integer getChannelMediaId() {
		return channelMediaId;
	}

	public void setChannelMediaId(Integer channelMediaId) {
		this.channelMediaId = channelMediaId;
	}

	public Integer getChannelMediaCap() {
		return channelMediaCap;
	}

	public void setChannelMediaCap(Integer channelMediaCap) {
		this.channelMediaCap = channelMediaCap;
	}

	public Integer getChannelMediaVariance() {
		return channelMediaVariance;
	}

	public void setChannelMediaVariance(Integer channelMediaVariance) {
		this.channelMediaVariance = channelMediaVariance;
	}

	public int getThresholdSequence() {
		return thresholdSequence;
	}

	public void setThresholdSequence(int thresholdSequence) {
		this.thresholdSequence = thresholdSequence;
	}

	public boolean getHouseHolded() {
		return houseHolded;
	}

	public void setHouseHolded(boolean houseHolded) {
		this.houseHolded = houseHolded;
	}

	public boolean getUnlimited() {
		return unlimited;
	}

	public void setUnlimited(boolean unlimited) {
		this.unlimited = unlimited;
	}

	public String getPromotionType() {
		return promotionType;
	}

	public void setPromotionType(String promotionType) {
		this.promotionType = promotionType;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public void setConsumerCap(int consumerCap) {
		this.consumerCap = consumerCap;
	}

	
	
	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Integer getAwardCap() {
		return awardCap;
	}
	public void setAwardCap(Integer cap) {
		this.awardCap = cap;
	}
	public Integer getAwardVariance() {
		return awardVariance;
	}
	public void setAwardVariance(Integer variance) {
		this.awardVariance = variance;
	}
	public String getCardRangeId() {
		return cardRangeId;
	}
	public void setCardRangeId(String cardRangeId) {
		this.cardRangeId = cardRangeId;
	}
	
	public Integer getConsumerCap() {
		return consumerCap;
	}
	
	public String getProgramSetupId() {
		return programSetupId;
	}
	public void setProgramSetupId(String id) {
		this.programSetupId = id;
	}

	public Integer getMediaCap() {
		return mediaCap;
	}

	public void setMediaCap(Integer cap) {
		this.mediaCap = cap;
	}

	public Integer getMediaVariance() {
		return mediaVariance;
	}

	public void setMediaVariance(Integer mediaVariance) {
		this.mediaVariance = mediaVariance;
	}

	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}
	
	public String getPromotionTypeForDmp() {
		if(promotionType != null) {
			if(promotionType.equalsIgnoreCase("StringPrints")) {
				return "string prints";
			} else if(promotionType.equalsIgnoreCase("Threshold")){
				return "thresholds";
			} else if(promotionType.equalsIgnoreCase("GType")) {
				return "direct";
			}
		}
		return "Transactional";
	}

	public Integer getControlPercentage() {
		return controlPercentage;
	}

	public void setControlPercentage(Integer controlPercentage) {
		this.controlPercentage = controlPercentage;
	}

	public Integer getRandomValue() {
		return randomValue;
	}

	public void setRandomValue(Integer randomValue) {
		this.randomValue = randomValue;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}
	
	/**
	 * Method returns true if promotion is String Print.
	 * @return true if String Print
	 */
	public boolean isStringPrint() {
		return promotionType.equalsIgnoreCase("StringPrints");
	}
	
	/**
	 * Method returns true if promotion is GType, StringPrints, Thresholds and false if Transactional
	 * @return true if promotion is historical and false is transactional.
	 */
	public boolean isHistoricalPrint() {
		return !promotionType.equalsIgnoreCase("Transactional");
	}
}
