package com.catalinamarketing.omni.pmr.setup;

import java.util.ArrayList;
import java.util.List;

public class AwardInfo {
	private String awardID;
	private Integer cap;
	private Integer variance;
	private boolean householded;
	private String promotionCategory;
	private boolean capDisabled;
	
	private List<MediaInfo> mediaList;
	
	public AwardInfo() {
		
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
}
