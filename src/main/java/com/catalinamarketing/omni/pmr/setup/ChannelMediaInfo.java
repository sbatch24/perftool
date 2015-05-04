package com.catalinamarketing.omni.pmr.setup;


public class ChannelMediaInfo {
	private String channelMediaID;
	private String channelType;
	private int cap;
	private int variance;
	private String startDate;
	private String endDate;
	
	public String getChannelMediaID() {
		return channelMediaID;
	}
	public void setChannelMediaID(String channelMediaID) {
		this.channelMediaID = channelMediaID;
	}
	public String getChannelType() {
		return channelType;
	}
	public void setChannelType(String channelType) {
		this.channelType = channelType;
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
}
