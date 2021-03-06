package com.catalinamarketing.omni.pmr.setup;

import java.util.ArrayList;
import java.util.List;

public class ProgramInfo {
	private String programID;
	private String contractID;
	private Integer cap;
	private Integer variance;
	private List<AwardInfo> awards;
	
	public ProgramInfo(){
		
	}
	
	public ProgramInfo(String programId, String contractId, int cap, int variance, List<AwardInfo> awards) {
		this.cap = cap;
		this.variance = variance;
		this.programID = programId;
		this.contractID = contractId;
		this.awards = awards;
	}
	
	
	public String getProgramID() {
		return programID;
	}
	public void setProgramID(String programID) {
		this.programID = programID;
	}
	public String getContractID() {
		return contractID;
	}
	public void setContractID(String contractID) {
		this.contractID = contractID;
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
	public List<AwardInfo> getAwards() {
		return awards;
	}
	public void setAwards(List<AwardInfo> awards) {
		this.awards = awards;
	}
	
	public void addAwardInfo(AwardInfo awardInfo) {
		if(awards == null) {
			awards = new ArrayList<AwardInfo>();
		}
		if(!programContainsAward(awardInfo)) {
			awards.add(awardInfo);	
		}else {
			AwardInfo award = getAwardInfo(awardInfo);
			for(MediaInfo mediaInfo : awardInfo.getMediaList()) {
				award.addMedia(mediaInfo);
			}
		}
	}
	
	/**
	 * Returns the channelMediaId under an award.
	 * @param awardNumber
	 * @return String
	 */
	public String getChannelMediaId(String awardNumber) {
		String channelMediaId = null;
		for(AwardInfo award : awards) {
			if(award.getAwardID().equalsIgnoreCase(awardNumber)) {
				channelMediaId = award.getChannelMediaId();
				break;
			}
		}
		return channelMediaId;
	}
	
	public boolean programContainsAward(AwardInfo awardInfo) {
		for(AwardInfo info : awards) {
			if(info.getAwardID().equalsIgnoreCase(awardInfo.getAwardID())) {
				return true;
			}
		}
		return false;
	}
	
	private AwardInfo getAwardInfo(AwardInfo info) {
		for(AwardInfo awardInfo : awards) {
			if(awardInfo.getAwardID().equalsIgnoreCase(info.getAwardID())) {
				return awardInfo;
			}
		}
		return null;
	}
	
	
}
