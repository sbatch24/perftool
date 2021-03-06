package com.catalinamarketing.omni.pmr.setup;

import java.util.ArrayList;
import java.util.List;

import com.catalinamarketing.omni.util.SetupConstants;

public class AwardInfo {
	private String awardID;
	private Integer cap;
	private Integer variance;
	private boolean householded;
	private String promotionCategory;
	private boolean unlimitedDelivery;
	
	
	private List<MediaInfo> mediaList;
	
	public int highestThresholdSequenceNumber() {
		// I can either mediaList.size or go through mediaList and return the max thresholdSequenceNo +1.
		int initialThresholdSequence = 0;
		for(MediaInfo mediaInfo : mediaList) {
			// This assumes only one channelMediaId under mediaInfo.
			int currentThresholdSequenceNo = mediaInfo.getChannels().get(0).getSequenceNo();
			initialThresholdSequence =  currentThresholdSequenceNo > initialThresholdSequence ? currentThresholdSequenceNo : initialThresholdSequence;
		}
		return initialThresholdSequence+1;
	}
	
	public AwardInfo() {
		
	}
	
	public boolean isTransactional() {
		return promotionCategory.equalsIgnoreCase(SetupConstants.TRANSACTIONAL);
	}
	
	public AwardInfo(String awardId, Integer cap, Integer variance, List<MediaInfo> list) {
		this.awardID = awardId;
		this.cap = cap;
		this.variance = variance;
		this.mediaList = list;
	}
	
	public String getAwardID() {
		return awardID;
	}
	public void setAwardID(String awardID) {
		this.awardID = awardID;
	}
	public Integer getCap() {
		return cap;
	}
	public void setCap(Integer cap) {
		this.cap = cap;
	}
	public Integer getVariance() {
		return variance;
	}
	public void setVariance(Integer variance) {
		this.variance = variance;
	}
	public List<MediaInfo> getMediaList() {
		return mediaList;
	}
	public void setMediaList(List<MediaInfo> mediaList) {
		this.mediaList = mediaList;
	}
	
	public void addMedia(MediaInfo media) {
		if(this.mediaList == null) {
			this.mediaList = new ArrayList<MediaInfo>();
		}
		this.mediaList.add(media);
	}
	
	/**
	 * Get the channelMediaId under the Award.
	 * @return ChannelMediaId under this award.
	 */
	public String getChannelMediaId() {
		String channelMediaId = null;
		
		if(this.mediaList != null) {
			/**
			 * In the first implementation of the perf tool an Award will only have one mediaId under it 
			 * and that mediaId will have only one channelMediaId. Hence the code below to extract the very first
			 * child element under each hierarchy.
			 */
			channelMediaId = mediaList.get(0).getChannels().get(0).getChannelMediaID();
		}
		return channelMediaId;
	}

	public boolean isHouseholded() {
		return householded;
	}

	public void setHouseholded(boolean householded) {
		this.householded = householded;
	}

	public String getPromotionCategory() {
		return promotionCategory;
	}

	public void setPromotionCategory(String promotionCategory) {
		this.promotionCategory = promotionCategory;
	}

	public boolean isUnlimitedDelivery() {
		return unlimitedDelivery;
	}

	public void setUnlimitedDelivery(boolean unlimitedDelivery) {
		this.unlimitedDelivery = unlimitedDelivery;
	}
	
}
