package com.catalinamarketing.omni.pmr.setup;


public class ChannelMediaInfo {
	private String channelMediaID;
	private String channelType;
	private Integer cap;
	private Integer variance;
	private String startDate;
	private String endDate;
	private boolean capDisabled;
	private Integer sequenceNo;

	public Integer getSequenceNo() {
		return sequenceNo;
	}
	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}
	
	public boolean isCapDisabled() {
		return capDisabled;
	}

	public void setCapDisabled(boolean capDisabled) {
		this.capDisabled = capDisabled;
	}
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
