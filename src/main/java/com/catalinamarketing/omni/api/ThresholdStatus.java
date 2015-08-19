package com.catalinamarketing.omni.api;

import javax.xml.bind.annotation.XmlElement;

public class ThresholdStatus {

	@XmlElement(required = true)
	protected String awardId;
	
	@XmlElement(required = true)
	protected int thresholdSequenceNumber;

	@XmlElement(required = true)
	protected int balance;

	@XmlElement(required = true)
	private String mediaNumber;

	@XmlElement(required = false)
	private String sourceType;
	
	public ThresholdStatus() {
	}
	
	public String getAwardId() {
		return awardId;
	}

	public void setAwardId(String awardId) {
		this.awardId = awardId;
	}

	public int getThresholdSequenceNumber() {
		return thresholdSequenceNumber;
	}

	public void setThresholdSequenceNumber(int thresholdSequenceNumber) {
		this.thresholdSequenceNumber = thresholdSequenceNumber;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public String getMediaNumber() {
		return mediaNumber;
	}

	public void setMediaNumber(String mediaNumber) {
		this.mediaNumber = mediaNumber;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
}
