package com.catalinamarketing.omni.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.catalinamarketing.omni.api.CustomerMediaEvent;
import com.catalinamarketing.omni.api.DirectDeposit;
import com.catalinamarketing.omni.api.DirectDepositStatus;
import com.catalinamarketing.omni.api.MediaEvents;
import com.catalinamarketing.omni.api.TargetedMediaResponse;

public class DirectDepositEventCreator implements MediaEventCreator {

	@Override
	public MediaEvents prepareEvent(TargetedMediaResponse targetMediaResponse,
			String customerId, MediaEvents mediaEvent) {
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
		
		mediaEvent.setTransactionId(""+ UUID.randomUUID().toString());
		CustomerMediaEvent customerMediaEvent = new CustomerMediaEvent();
		customerMediaEvent.setCustomerId(customerId.toString());
		for(String awardId : awardIdPrinted) {
			DirectDepositStatus directDepositStatus = new DirectDepositStatus();
			directDepositStatus.setAwardId(awardId);
			directDepositStatus.setStatus(DirectDepositStatus.PRINTED_OFFER);
			customerMediaEvent.getDirectDepositStatuses().add(directDepositStatus);
		}
		for(String awardId : awardIdFlushed) {
			DirectDepositStatus directDepositStatus = new DirectDepositStatus();
			directDepositStatus.setAwardId(awardId);
			directDepositStatus.setStatus(DirectDepositStatus.NOT_PRINTED);
			customerMediaEvent.getDirectDepositStatuses().add(directDepositStatus);
		}
		mediaEvent.getCustomerMediaEvents().add(customerMediaEvent);
		return mediaEvent;
	}

}
