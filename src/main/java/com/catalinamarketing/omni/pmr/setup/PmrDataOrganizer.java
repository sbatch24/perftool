package com.catalinamarketing.omni.pmr.setup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.catalinamarketing.omni.config.Config;
import com.catalinamarketing.omni.config.OfferSetup;
import com.catalinamarketing.omni.config.ProgramSetup;
import com.catalinamarketing.omni.config.PromotionSetup;
import com.catalinamarketing.omni.message.DataSetupActivityLog;

public class PmrDataOrganizer {
	final static Logger logger = LoggerFactory.getLogger(PmrDataOrganizer.class);

	private List<PmrSetupMessage> pmrSetupMessageList;
	private DynamicControlsMessage dynamicControlMessage;
	private CampaignOfferSetupMessage campaignMessage;
	
	public DynamicControlsMessage getDynamicControlMessage() {
		return dynamicControlMessage;
	}

	public void setDynamicControlMessage(
			DynamicControlsMessage dynamicControlMessage) {
		this.dynamicControlMessage = dynamicControlMessage;
	}

	public CampaignOfferSetupMessage getCampaignMessage() {
		return campaignMessage;
	}

	public void setCampaignMessage(CampaignOfferSetupMessage campaignMessage) {
		this.campaignMessage = campaignMessage;
	}

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
		populateCappingData();
		populateStringPrintData();
		populateDynamicControlsData();
	}
	
	/**
	 * Populate dynamic controls data
	 */
	private void populateDynamicControlsData() {
		List<PromotionSetup> promotionSetupList = config.getPromotionSetupList();
		dynamicControlMessage = new DynamicControlsMessage();
		dynamicControlMessage.setCountryCode("US");
		dynamicControlMessage.setCampaign("TEST-CAMPAIGN");
		dynamicControlMessage.setGroupName("TEST-GROUP");
		for(PromotionSetup promotionSetup : promotionSetupList) {
			if(promotionSetup.getControlPercentage() != null && promotionSetup.getRandomValue() != null) {
				AwardDynamicControl awardDc = new AwardDynamicControl();
				awardDc.setAwardID(promotionSetup.getAwardId());
				awardDc.setControlPercentage(promotionSetup.getControlPercentage().doubleValue());
				awardDc.setRandomValue(promotionSetup.getRandomValue());
				dynamicControlMessage.addAwardDynamicControl(awardDc);
			} else {
				logger.debug("No dynamic setup data setup for award " + promotionSetup.getAwardId());
			}
		}
	}

	/**
	 * 
	 */
	private void populateStringPrintData() {
		List<OfferSetup> offerSetupList = config.getOfferSetupList();
		campaignMessage = new CampaignOfferSetupMessage();
		List<PromotionSetup> promotionSetupList = config.getPromotionSetupList();
		for(PromotionSetup promotionSetup : promotionSetupList) {
			campaignMessage.addAwardRank(new AwardRank(promotionSetup.getAwardId().toString(), 
					promotionSetup.getRank(), promotionSetup.getCampaignId()));
		}
		
		for(OfferSetup offerSetup : offerSetupList) {
			Campaign campaign = new Campaign();
			campaign.setCampaignId(offerSetup.getCampaignId());
			campaign.setOffers(offerSetup.getOfferList());
			campaignMessage.addCampaign(campaign);
		}
	}

	private void populateCappingData() {
		List<ProgramSetup> programSetupList = config.getProgramSetupList();
		/**
		 * Iterate through all Program/Contract. For one program contract select all promotionsetups.
		 */
		for(ProgramSetup programSetup : programSetupList) {
			Map<Integer,List<PromotionSetup>> promotionSetupMap = config.getPromotionsByProgramId(programSetup.getProgramId());
			if(promotionSetupMap.size() > 0) {
				PmrSetupMessage pmrSetupMessage = new PmrSetupMessage();
				pmrSetupMessage.setLocale("US");
				pmrSetupMessage.setSetupSystemID("MXP");
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

	/**
	 * Populates awardInfo object with the data it needs before it is published
	 * @param promotionSetup
	 * @return AwardInfo
	 */
	private AwardInfo populateAwardInfo(PromotionSetup promotionSetup) {
		AwardInfo awardSetup = new AwardInfo();
		awardSetup.setAwardID(promotionSetup.getAwardId().toString());
		awardSetup.setCap(promotionSetup.getAwardCap());
		awardSetup.setVariance(promotionSetup.getAwardVariance());
		awardSetup.setHouseholded(promotionSetup.getHouseHolded());
		awardSetup.setPromotionCategory(promotionSetup.getPromotionType());
		awardSetup.setUnlimitedDelivery(promotionSetup.getUnlimited());
		MediaInfo mediaInfo = new MediaInfo();
		mediaInfo.setMediaID(promotionSetup.getMediaId().toString());
		mediaInfo.setCap(promotionSetup.getMediaCap());
		mediaInfo.setVariance(promotionSetup.getMediaVariance());
		mediaInfo.setCapDisabled(promotionSetup.isMediaCapDisabled());
		mediaInfo.setSequenceNo(promotionSetup.getThresholdSequence());
		ChannelMediaInfo channelMediaInfo = new ChannelMediaInfo();
		channelMediaInfo.setChannelMediaID(promotionSetup.getChannelMediaId().toString());
		channelMediaInfo.setChannelType(promotionSetup.getChannelType());
		channelMediaInfo.setCap(promotionSetup.getChannelMediaCap());
		channelMediaInfo.setVariance(promotionSetup.getChannelMediaVariance());
		channelMediaInfo.setStartDate(promotionSetup.getStartDate());
		channelMediaInfo.setEndDate(promotionSetup.getEndDate());
		channelMediaInfo.setCapDisabled(promotionSetup.isChannelCapDisabled());
		mediaInfo.addChannelMedia(channelMediaInfo);
		awardSetup.addMedia(mediaInfo);
		return awardSetup;
	}
}
