package com.catalinamarketing.omni.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.catalinamarketing.omni.api.CustomerMediaEvent;
import com.catalinamarketing.omni.api.DirectDepositStatus;
import com.catalinamarketing.omni.api.MediaEvents;
import com.catalinamarketing.omni.api.Offer;
import com.catalinamarketing.omni.api.OfferStatus;
import com.catalinamarketing.omni.api.StringPrint;
import com.catalinamarketing.omni.api.StringPrintStatus;
import com.catalinamarketing.omni.api.TargetedMediaResponse;
import com.catalinamarketing.omni.protocol.message.TestPlanMsg;
import com.catalinamarketing.omni.util.SetupConstants;

public class StringPrintEventCreator implements MediaEventCreator {

	@Override
	public MediaEvents prepareEvent(TargetedMediaResponse targetMediaResponse, MediaEvents mediaEvent, TestPlanMsg testPlan) {
		List<String> awardIdPrinted = new ArrayList<String>();
		List<String> awardIdFlushed = new ArrayList<String>();
		Random randomBoolean = new Random();

		for(StringPrint stringPrint : targetMediaResponse.getStringPrints()) {
			if(randomBoolean.nextBoolean()) {
				awardIdPrinted.add(stringPrint.getAwardId());
			}else {
				awardIdFlushed.add(stringPrint.getAwardId());
			}
		}
		
		CustomerMediaEvent customerMediaEvent = mediaEvent.getCustomerMediaEvents().get(0);
		
		for(String awardId : awardIdPrinted) {
			StringPrintStatus stringPrintStatus = new StringPrintStatus();
			stringPrintStatus.setAwardId(awardId);
			stringPrintStatus.setStatus(SetupConstants.PROMOTION_PRINTED);
			
			List<Offer> offerList = targetMediaResponse.getOffersByAwardId(awardId);
			for(Offer offer : offerList) {
				OfferStatus offerStatus = new OfferStatus();
				offerStatus.setOfferId(offer.getOfferId());
				if(randomBoolean.nextBoolean()) {
					offerStatus.setStatus(SetupConstants.OFFER_PRINTED);
				} else {
					offerStatus.setStatus(SetupConstants.OFFER_FLUSHED);
				}
				stringPrintStatus.addOfferStatus(offerStatus);
			}
			customerMediaEvent.getStringPrintStatuses().add(stringPrintStatus);
			
		}
		
		for(String awardId : awardIdFlushed) {
			StringPrintStatus stringPrintStatus = new StringPrintStatus();
			stringPrintStatus.setAwardId(awardId);
			stringPrintStatus.setStatus(SetupConstants.PROMOTION_NOT_PRINTED);
			List<Offer> offerList = targetMediaResponse.getOffersByAwardId(awardId);
			for(Offer offer : offerList) {
				OfferStatus offerStatus = new OfferStatus();
				offerStatus.setOfferId(offer.getOfferId());
				if(randomBoolean.nextBoolean()) {
					offerStatus.setStatus(SetupConstants.OFFER_PRINTED);
				} else {
					offerStatus.setStatus(SetupConstants.OFFER_FLUSHED);
				}
				stringPrintStatus.addOfferStatus(offerStatus);
			}
			customerMediaEvent.getStringPrintStatuses().add(stringPrintStatus);
		}
		
		mediaEvent.getCustomerMediaEvents().add(customerMediaEvent);
		return mediaEvent;
	}

}
