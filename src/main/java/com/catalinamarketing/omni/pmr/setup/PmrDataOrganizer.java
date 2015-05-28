package com.catalinamarketing.omni.pmr.setup;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.catalinamarketing.omni.config.Config;
import com.catalinamarketing.omni.config.ProgramSetup;
import com.catalinamarketing.omni.config.PromotionSetup;

public class PmrDataOrganizer {
	final static Logger logger = LoggerFactory.getLogger(PmrDataOrganizer.class);

	private List<PmrSetupMessage> pmrSetupMessageList;
	private final Config config;
	
	public PmrDataOrganizer(Config config) {
		this.pmrSetupMessageList = new ArrayList<PmrSetupMessage>();
		this.config = config;
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
		List<PromotionSetup> promotionSetupList = config.getServer().getSetup()
				.getPromotionSetup();
		for (PromotionSetup promoSetup : promotionSetupList) {
			ProgramSetup programSetup = config.getProgramSetup(promoSetup
					.getProgramSetupId());
			if (programSetup != null) {
				PmrSetupMessage pmrSetupMessage = new PmrSetupMessage();
				pmrSetupMessage.setLocale("US");
				pmrSetupMessage.setSetupSystemID("MXP-US");
				ProgramInfo programSetupMessage = new ProgramInfo();
				programSetupMessage.setProgramID(programSetup.getProgramId());
				programSetupMessage.setContractID(programSetup.getContractId());
				programSetupMessage.setCap(programSetup.getCap());
				programSetupMessage.setVariance(programSetup.getVariance());

				List<AwardInfo> awards = getAwardSetupList(promoSetup);
				programSetupMessage.setAwards(awards);
				pmrSetupMessage.addProgram(programSetupMessage);
				addPmrSetupMessage(pmrSetupMessage);
			} else {
				logger.error("PromotionSetup should always contain a bill No. Check config.xml file for promotionSetup - "
						+ promoSetup.getAwardRange());
			}
		}
	}

	private List<AwardInfo> getAwardSetupList(PromotionSetup promotionSetup) {
		List<Integer> awardRange = promotionSetup.awardRange();
		List<AwardInfo> awardSetupList = new ArrayList<AwardInfo>();
		List<Integer> mediaRange = promotionSetup.mediaIdRange();
		int index = 0;
		for (Integer awardId : awardRange) {
			AwardInfo awardSetup = new AwardInfo();
			awardSetup.setAwardID("" + awardId);
			awardSetup.setCap(promotionSetup.getAwardCap());
			awardSetup.setVariance(promotionSetup.getAwardVariance());
			MediaInfo mediaInfo = new MediaInfo();
			mediaInfo.setMediaID(mediaRange.get(index).toString());
			mediaInfo.setCap(promotionSetup.getMediaCap());
			mediaInfo.setVariance(promotionSetup.getMediaVariance());
			ChannelMediaInfo channelMediaInfo = new ChannelMediaInfo();
			channelMediaInfo.setChannelMediaID(mediaInfo.getMediaID());
			channelMediaInfo.setChannelType(promotionSetup.getChannelType());
			channelMediaInfo.setStartDate(promotionSetup.getStartDate());
			channelMediaInfo.setEndDate(promotionSetup.getEndDate());
			mediaInfo.addChannelMedia(channelMediaInfo);
			awardSetup.addMedia(mediaInfo);
			awardSetupList.add(awardSetup);
			index++;
		}
		return awardSetupList;
	}
}
