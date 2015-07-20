package com.catalinamarketing.omni.pmr.setup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.catalinamarketing.omni.config.Config;
import com.catalinamarketing.omni.config.ProgramSetup;
import com.catalinamarketing.omni.config.PromotionSetup;
import com.catalinamarketing.omni.message.DataSetupActivityLog;

public class PmrDataOrganizer {
	final static Logger logger = LoggerFactory.getLogger(PmrDataOrganizer.class);

	private List<PmrSetupMessage> pmrSetupMessageList;
	private final Config config;
	private DataSetupActivityLog activityLog;
	
	public PmrDataOrganizer(Config config, DataSetupActivityLog activityLog) {
		this.pmrSetupMessageList = new ArrayList<PmrSetupMessage>();
		this.config = config;
		this.activityLog = activityLog;
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
	
	/**
	 * Initialize PMR data
	 */
	public void initializePmrDataSetup() {
		logger.info("Initializing PMR data");
		
		
		List<ProgramSetup> programSetupList = config.getProgramSetupList();
		
		/**
		 * Iterate through all Program/Contract. For one program contract select all promotionsetups.
		 */
		for(ProgramSetup programSetup : programSetupList) {
			Map<Integer,List<PromotionSetup>> promotionSetupMap = config.getPromotionsByProgramId(programSetup.getProgramId());
			if(promotionSetupMap.size() > 0) {
				PmrSetupMessage pmrSetupMessage = new PmrSetupMessage();
				pmrSetupMessage.setLocale("US");
				pmrSetupMessage.setSetupSystemID("MXP-US");
				ProgramInfo programInfo = new ProgramInfo();
				programInfo.setProgramID(programSetup.getProgramId());
				programInfo.setContractID(programSetup.getContractId());
				programInfo.setCap(programSetup.getCap());
				programInfo.setVariance(programSetup.getVariance());
				for(Map.Entry<Integer, List<PromotionSetup>> entry : promotionSetupMap.entrySet()) {
					List<PromotionSetup> promotionSetupList = entry.getValue();
					for(PromotionSetup promotionSetup : promotionSetupList) {
						AwardInfo awardInfo = populateAwardInfo(promotionSetup);
						programInfo.addAwardInfo(awardInfo);
					}
				}
				pmrSetupMessage.addProgram(programInfo);
				addPmrSetupMessage(pmrSetupMessage);	
			} else {
				logger.error("Program must have promotions under it. Check program id " + programSetup.getProgramId());
				activityLog.addException("Program must have promotions under it. Check program id " + programSetup.getProgramId());
			}
		}
	}

	private AwardInfo populateAwardInfo(PromotionSetup promotionSetup) {
		AwardInfo awardSetup = new AwardInfo();
		awardSetup.setAwardID(promotionSetup.getAwardId().toString());
		awardSetup.setCap(promotionSetup.getAwardCap());
		awardSetup.setVariance(promotionSetup.getAwardVariance());
		MediaInfo mediaInfo = new MediaInfo();
		mediaInfo.setMediaID(promotionSetup.getMediaId().toString());
		mediaInfo.setCap(promotionSetup.getMediaCap());
		mediaInfo.setVariance(promotionSetup.getMediaVariance());
		ChannelMediaInfo channelMediaInfo = new ChannelMediaInfo();
		channelMediaInfo.setChannelMediaID(promotionSetup.getMediaId().toString());
		channelMediaInfo.setChannelType(promotionSetup.getChannelType());
		channelMediaInfo.setStartDate(promotionSetup.getStartDate());
		channelMediaInfo.setEndDate(promotionSetup.getEndDate());
		mediaInfo.addChannelMedia(channelMediaInfo);
		awardSetup.addMedia(mediaInfo);
		return awardSetup;
	}
}
