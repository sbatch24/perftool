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
	private static  Random rand = new Random();
	private final TestPlanMsg testPlan;
	
	public TargetingApiExecutor(int threadNumber, int callCount, 
			String threadGroupIdentifier, CountDownLatch finishedSignal, MediaUsageRepository mediaUsageRepository, 
			HttpResponseRepository responseRepository, TestPlanMsg testPlan) {
		super(threadNumber,threadGroupIdentifier, responseRepository, finishedSignal);
		this.callCount = callCount;
		this.mediaUsageRepository = mediaUsageRepository;
		this.customerIds = new ArrayList<BigInteger>();
		this.testPlan = testPlan;
	}
	
	/**
	 * Returns a psuedo-random number between min and max, inclusive.
	 * The difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 *
	 * @param min Minimim value
	 * @param max Maximim value.  Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	public static int randInt(int min, int max) {
	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
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
		            .queryParam("channel", "web")
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
	
	/**
	 * Report the media printed/flushed to the events api.
	 * @param targetMediaResponse
	 * @param customerId
	 * @return MediaEvents structure which is reported to the events api
	 */
	private MediaEvents prepareMediaEvent(TargetedMediaResponse targetMediaResponse, String customerId) {
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
		
		MediaEvents mediaEvent = new MediaEvents();
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
	
	@Override
	public void run()  {
		populateCids();
		int count = 0;
		Client client = ClientBuilder.newClient();
		int r = 0;
		while(count < callCount && !halted()) {
			try {
				BigInteger customerId = getRandomCidFromRange();
				//System.out.println("Getting historical offers for cid " + customerId.toString());
				final Timer.Context context = TARGET_API_REQUEST.time();
				Response resp = callTargetingApi(client, customerId);
				context.stop();
				incrementResponseCounter("GetTargetedMedia",resp.getStatus());
				Thread.sleep(testPlan.getEventReportFrequency()*1000);
				if(resp.getStatusInfo().getStatusCode() == Response.Status.OK.getStatusCode()) {
					TargetedMediaResponse targetMediaResponse = resp.readEntity(TargetedMediaResponse.class);
					resp.close();
					if(targetMediaResponse != null && targetMediaResponse.getDirectDeposits().size() > 0) {
						MediaEvents mediaEvent = prepareMediaEvent(targetMediaResponse, customerId.toString());
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
						}
						resp.close();
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
		logger.info(runStatus());
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
