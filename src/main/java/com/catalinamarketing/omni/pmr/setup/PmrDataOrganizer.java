package com.catalinamarketing.omni.pmr.setup;

import java.util.ArrayList;
import java.util.List;

public class PmrDataOrganizer {
	private List<PmrSetupMessage> pmrSetupMessageList;
	
	public PmrDataOrganizer() {
		this.pmrSetupMessageList = new ArrayList<PmrSetupMessage>();
	}
	
	public List<PmrSetupMessage> getPmrSetupMessageList() {
		return this.pmrSetupMessageList;
	}
	
	public void addPmrSetupMessage(PmrSetupMessage msg) {
		pmrSetupMessageList.add(msg);
	}
	
	/**
	 * Find the channelMediaId for an Award.
	 * @param awardId
	 * @return the channelMediaId. null if cannot find channelId.
	 */
	public String getChannelMediaId(String awardId) {
		String channelMedia = null;
		for(PmrSetupMessage programSetup : pmrSetupMessageList) {
			channelMedia = programSetup.getChannelMediaId(awardId);
			if(channelMedia != null) {
				break;
			}
		}
		return channelMedia;
	}
}
