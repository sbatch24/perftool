package com.catalinamarketing.omni.client;

import java.util.Random;

import com.catalinamarketing.omni.api.MediaEvents;
import com.catalinamarketing.omni.api.TargetedMediaResponse;
import com.catalinamarketing.omni.api.Threshold;
import com.catalinamarketing.omni.api.ThresholdStatus;
import com.catalinamarketing.omni.protocol.message.AwardData;
import com.catalinamarketing.omni.protocol.message.TestPlanMsg;

public class ThresholdEventCreator implements MediaEventCreator {
	
	private Random randomRange = new Random();

	@Override
	public MediaEvents prepareEvent(TargetedMediaResponse targetMediaResponse, MediaEvents mediaEvent, 
			TestPlanMsg testPlan) {
		Random randomBoolean = new Random();

		for(Threshold threshold : targetMediaResponse.getThresholds()) {
			String awardId = threshold.getAwardId();
			AwardData awardData = testPlan.getAwardData(awardId);
			ThresholdStatus status = new ThresholdStatus();
			int randomBalance = randomRange.nextInt((100-0+1)) + 0;
			status.setBalance(randomBalance);
			status.setAwardId(awardId);
			status.setSourceType("MFD");
			status.setMediaNumber(awardData.getChannelMediaId());
			// Randomly chose to reduce the PCV.
			if(randomBoolean.nextBoolean()) {
				int newSequenceNumber = nextThresholdNumber(threshold.getThresholdSequenceNumber(), testPlan.getAwardData(awardId).getThresholdSequence());
				if(newSequenceNumber < 0) {
					newSequenceNumber = 0;
				}
				status.setThresholdSequenceNumber(newSequenceNumber);
			}else {
				status.setThresholdSequenceNumber(threshold.getThresholdSequenceNumber());
			}
			mediaEvent.getCustomerMediaEvents().get(0).getThresholdStatuses().add(status);
		}
		return mediaEvent;
	}
	
	private int nextThresholdNumber(int currentThresholdNumber, int maxThresholdNumberForAward) {
		if(currentThresholdNumber >= 255) {
			currentThresholdNumber = maxThresholdNumberForAward;
		} else {
			currentThresholdNumber--;
		}
		return currentThresholdNumber;
	}
}
