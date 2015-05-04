package com.catalinamarketing.omni.pmr.setup;

import java.util.ArrayList;
import java.util.List;

public class MediaInfo {
	private String mediaID;
	private int cap;
	private int variance;
	private List<ChannelMediaInfo> channels;
	
	public String getMediaID() {
		return mediaID;
	}
	public void setMediaID(String mediaID) {
		this.mediaID = mediaID;
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
