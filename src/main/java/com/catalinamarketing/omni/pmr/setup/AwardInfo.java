package com.catalinamarketing.omni.pmr.setup;

import java.util.ArrayList;
import java.util.List;

public class AwardInfo {
	private String awardID;
	private int cap;
	private int variance;
	private List<MediaInfo> mediaList;
	
	public AwardInfo() {
		
	}
	
	public AwardInfo(String awardId, int cap, int variance, List<MediaInfo> list) {
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
	public int getCap() {
		return cap;
	}
	public void setCap(int cap) {
		this.cap = cap;
	}
	public int getVariance() {
		return variance;
	}
	public void setVariance(int variance) {
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
}
