package com.catalinamarketing.omni.pmr.setup;

import java.util.ArrayList;
import java.util.List;

/**
 * Pojo object which is serialized to json and published to the queue.
 * @author achavan
 *
 */
public class DynamicControlsMessage {

	private String countryCode;

	private String campaign;

	private String groupName;

	private List<AwardDynamicControl> awardDynamicControls;

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCampaign() {
		return campaign;
	}

	public void setCampaign(String campaign) {
		this.campaign = campaign;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<AwardDynamicControl> getAwardDynamicControls() {
		return awardDynamicControls;
	}

	public void setAwardDynamicControls(
			List<AwardDynamicControl> awardDynamicControls) {
		this.awardDynamicControls = awardDynamicControls;
	}
	
	public void addAwardDynamicControl(AwardDynamicControl control) {
		if(awardDynamicControls == null) {
			this.awardDynamicControls = new ArrayList<AwardDynamicControl>();
		}
		
		awardDynamicControls.add(control);
	}
}
