package com.catalinamarketing.omni.pmr.setup;

import java.util.ArrayList;
import java.util.List;

public class MediaInfo {
	private String mediaID;
	private Integer cap;
	private Integer variance;
	private List<ChannelMediaInfo> channels;
	
	public String getMediaID() {
		return mediaID;
	}
	public void setMediaID(String mediaID) {
		this.mediaID = mediaID;
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
	public List<ChannelMediaInfo> getChannels() {
		return channels;
	}
	public void setChannels(List<ChannelMediaInfo> channels) {
		this.channels = channels;
	}
	
	public void addChannelMedia(ChannelMediaInfo channelMediaInfo) {
		if(this.channels == null) {
			this.channels = new ArrayList<ChannelMediaInfo>();
		}
		this.channels.add(channelMediaInfo);
	}
	
}
