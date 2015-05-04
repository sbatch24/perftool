package com.catalinamarketing.omni.pmr.setup;

import java.util.List;

public class ProgramInfo {
	private String programID;
	private String contractID;
	private int cap;
	private int variance;
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
	public List<AwardInfo> getAwards() {
		return awards;
	}
	public void setAwards(List<AwardInfo> awards) {
		this.awards = awards;
	}
}
