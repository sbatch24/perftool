package com.catalinamarketing.omni.protocol.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="awardData")
public class AwardData {
	private String awardId;
	private String mediaId;
	private String channelMediaId;
	private String type;
	private int thresholdSequence;
	
	
	public AwardData(){
		
	}
	
	
	public String getAwardId() {
		return awardId;
	}
	public void setAwardId(String awardId) {
		this.awardId = awardId;
	}
	public String getMediaId() {
		return mediaId;
	}
	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}
	public String getChannelMediaId() {
		return channelMediaId;
	}
	public void setChannelMediaId(String channelMediaId) {
		this.channelMediaId = channelMediaId;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public int getThresholdSequence() {
		return thresholdSequence;
	}


	public void setThresholdSequence(int thresholdSequence) {
		this.thresholdSequence = thresholdSequence;
	}
}
