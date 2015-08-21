package com.catalinamarketing.omni.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.catalinamarketing.omni.api.CustomerMediaEvent;
import com.catalinamarketing.omni.api.DirectDeposit;
import com.catalinamarketing.omni.api.DirectDepositStatus;
import com.catalinamarketing.omni.api.MediaEvents;
import com.catalinamarketing.omni.api.TargetedMediaResponse;
import com.catalinamarketing.omni.protocol.message.TestPlanMsg;
import com.catalinamarketing.omni.util.SetupConstants;

public class DirectDepositEventCreator implements MediaEventCreator {

	@Override
	public MediaEvents prepareEvent(TargetedMediaResponse targetMediaResponse, MediaEvents mediaEvent, TestPlanMsg testPlan) {
		List<String> awardIdPrinted = new ArrayList<String>();
		List<String> awardIdFlushed = new ArrayList<String>();
		Random randomBoolean = new Random();

		for(DirectDeposit directDeposit : targetMediaResponse.getDirectDeposits()) {
			if(randomBoolean.nextBoolean()) {
				awardIdPrinted.add(directDeposit.getAwardId());
			}else {
				awardIdFlushed.add(directDeposit.getAwardId());
			}
		}
		
		CustomerMediaEvent customerMediaEvent = mediaEvent.getCustomerMediaEvents().get(0);
		for(String awardId : awardIdPrinted) {
			DirectDepositStatus directDepositStatus = new DirectDepositStatus();
			directDepositStatus.setAwardId(awardId);
			directDepositStatus.setStatus(SetupConstants.PROMOTION_PRINTED);
			directDepositStatus.setMediaNumber(testPlan.getAwardData(awardId).getChannelMediaId());
			customerMediaEvent.getDirectDepositStatuses().add(directDepositStatus);
		}
		for(String awardId : awardIdFlushed) {
			DirectDepositStatus directDepositStatus = new DirectDepositStatus();
			directDepositStatus.setAwardId(awardId);
			directDepositStatus.setStatus(SetupConstants.PROMOTION_NOT_PRINTED);
			directDepositStatus.setMediaNumber(testPlan.getAwardData(awardId).getChannelMediaId());
			customerMediaEvent.getDirectDepositStatuses().add(directDepositStatus);
		}
		return mediaEvent;
	}

}
