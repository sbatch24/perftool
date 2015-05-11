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
}
