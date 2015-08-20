package com.catalinamarketing.omni.client;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.catalinamarketing.omni.api.CustomerMediaEvent;
import com.catalinamarketing.omni.api.DirectDeposit;
import com.catalinamarketing.omni.api.DirectDepositStatus;
import com.catalinamarketing.omni.api.MediaEvents;
import com.catalinamarketing.omni.api.StringPrintStatus;
import com.catalinamarketing.omni.api.TargetedMediaResponse;
import com.catalinamarketing.omni.protocol.message.TestPlanMsg;
import com.catalinamarketing.omni.util.HttpResponseRepository;
import com.catalinamarketing.omni.util.MediaUsageRepository;
import com.codahale.metrics.Timer;
import com.google.gson.Gson;



/**
 * This thread class is reponsible for executing the targeting api.
 * @author achavan
 *
 */
public class TargetingApiExecutor extends ApiExecutor {
	final static Logger logger = LoggerFactory.getLogger(TargetingApiExecutor.class);

	private final int callCount;
	private MediaUsageRepository mediaUsageRepository;
	private List<BigInteger> customerIds;
	private final TestPlanMsg testPlan;
	private MediaEventCreator thresholdEventCreator;
	private MediaEventCreator stringPrintEventCreator;
	private MediaEventCreator directDepositEventCreator;
	
	public TargetingApiExecutor(int threadNumber, int callCount, 
			String threadGroupIdentifier, CountDownLatch finishedSignal, MediaUsageRepository mediaUsageRepository, 
			HttpResponseRepository responseRepository, TestPlanMsg testPlan) {
		super(threadNumber,threadGroupIdentifier, responseRepository, finishedSignal);
		this.callCount = callCount;
		this.mediaUsageRepository = mediaUsageRepository;
		this.customerIds = new ArrayList<BigInteger>();
		this.testPlan = testPlan;
		this.thresholdEventCreator = new ThresholdEventCreator();
		this.directDepositEventCreator = new DirectDepositEventCreator();
		this.stringPrintEventCreator = new StringPrintEventCreator();
	}
	
	/**
	 * Returns an random Cid
	 * @return Cid as bigInteger
	 */
	private BigInteger getRandomCidFromRange() {
		return customerIds.get(randInt(0, (this.customerIds.size()-1)));
	}
	
	
	private Response callTargetingApi(Client client, BigInteger customerId) {
		Response resp = null;
		try {
			resp = client.target(String.format(this.testPlan.getTargetingApiUrl(), testPlan.getRetailerId(),customerId))
		            .queryParam("channel", "store")
		            .queryParam("transactionid", customerId)  // Use customer id as transaction id
		            .request()
		            .accept(MediaType.APPLICATION_XML)
		            .get();
		}catch(Exception ex) {
			logger.error("Problem occured during call to MEP targeting api. Error : " + ex.getMessage());
			if(ex.getMessage() == null) {
				haltedOnException();
			}
			if(ex.getMessage() != null && ex.getMessage().contains("UnknownHostException")) {
				haltedOnException();
			}
			seriousException("GetTargetedMedia", ex.getMessage());	
		}
		return resp;
	}
	
	@Override
	public void run()  {
		populateCids();
		int count = 0;
		Client client = ClientBuilder.newClient();
		while(count < callCount && !halted()) {
			try {
				BigInteger customerId = getRandomCidFromRange();
				final Timer.Context requestMediaContext = TARGET_API_REQUEST.time();
				Response resp = callTargetingApi(client, customerId);
				requestMediaContext.stop();
				incrementResponseCounter("GetTargetedMedia",resp.getStatus());
				Thread.sleep((randInt(0,testPlan.getEventReportFrequency()))*1000);
				if(resp.getStatusInfo().getStatusCode() == Response.Status.OK.getStatusCode()) {
					TargetedMediaResponse targetMediaResponse = resp.readEntity(TargetedMediaResponse.class);
					resp.close();
					if(targetMediaResponse != null && (targetMediaResponse.getDirectDeposits().size() > 0 || 
							targetMediaResponse.getStringPrints().size() > 0 || targetMediaResponse.getThresholds().size() > 0	)) {
						MediaEvents mediaEvent = new MediaEvents();
						mediaEvent.setTransactionId(""+ UUID.randomUUID().toString());
						CustomerMediaEvent customerMediaEvent = new CustomerMediaEvent();
						customerMediaEvent.setCustomerId(customerId.toString());
						mediaEvent.getCustomerMediaEvents().add(customerMediaEvent);
						mediaEvent = directDepositEventCreator.prepareEvent(targetMediaResponse,mediaEvent, testPlan);
						mediaEvent = stringPrintEventCreator.prepareEvent(targetMediaResponse, mediaEvent, testPlan);
						mediaEvent = thresholdEventCreator.prepareEvent(targetMediaResponse, mediaEvent, testPlan);
						
						Gson gson = new Gson();
						final Timer.Context eventContext = EVENT_API_REQUEST.time();
						resp  = client.target(String.format(this.testPlan.getEventsApiUrl(), testPlan.getRetailerId()))
					            .queryParam("channel", "web")
					            .request()
					            .accept(MediaType.APPLICATION_JSON)
					            .header("Content-Type", MediaType.APPLICATION_JSON)
					            .post(Entity.entity(gson.toJson(mediaEvent), MediaType.APPLICATION_JSON));
						eventContext.stop();
						incrementResponseCounter("ReportEvents",resp.getStatus());
						if(resp.getStatusInfo().getStatusCode() == Response.Status.OK.getStatusCode() || 
							resp.getStatusInfo().getStatusCode() == Response.Status.ACCEPTED.getStatusCode()) {
							for(DirectDepositStatus directDepositStatus : mediaEvent.getMediaPrintEventForCustomer(customerId.toString())) {
								mediaUsageRepository.incrementMediaCounter(threadGroupIdentifier, testPlan.getChannelMediaId(directDepositStatus.getAwardId()));
							}
							
							for(StringPrintStatus status : mediaEvent.getStringPrintEventForCustomer(customerId.toString())) {
								mediaUsageRepository.incrementMediaCounter(threadGroupIdentifier, testPlan.getChannelMediaId(status.getAwardId()));
							}
						}
						resp.close();
					} else {
						logger.info("Targeting api did not return anything for this cid " + customerId.toString());
					}
				}
			}catch (Exception e) {
				logger.error("Problem occured in Targeting API thread. Error : " + e.getMessage());
				if(e.getMessage() == null) {
					haltedOnException();
				}
				if(e.getMessage() != null && e.getMessage().contains("UnknownHostException")) {
					haltedOnException();
				} 
				seriousException("ReportEvents", e.getMessage());
			}finally{
				count++;
			}
		}
		if(count >= callCount) {
			completedExecution();
		}
		//logger.info(runStatus());
		finishedRun();
		client.close();
	}

	/**
	 * Populate cids given in the range
	 */
	private void populateCids() {
		BigInteger firstCid = new BigInteger(testPlan.getCardRangeList().get(0).toString());
		BigInteger lastCid = new BigInteger(testPlan.getCardRangeList().get(1).toString());
		
		for(; firstCid.compareTo(lastCid) <= 0; firstCid = firstCid.add(BigInteger.ONE)) {
			this.customerIds.add(firstCid);
		}
	}
	
	public int getCallCount() {
		return callCount;
	}

	public String getPoolIdentifier() {
		return threadGroupIdentifier;
	}

	public MediaUsageRepository getMediaUsageRepository() {
		return mediaUsageRepository;
	}

	public void setMediaUsageRepository(MediaUsageRepository mediaUsageRepository) {
		this.mediaUsageRepository = mediaUsageRepository;
	}

	@Override
	public String apiName() {
		return "TargetingApi";
	}
}
