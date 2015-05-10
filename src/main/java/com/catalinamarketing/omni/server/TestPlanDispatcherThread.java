package com.catalinamarketing.omni.server;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.catalinamarketing.omni.config.CardSetup;
import com.catalinamarketing.omni.config.Config;
import com.catalinamarketing.omni.protocol.message.TestPlanMsg;

/**
 * Class responsible for notifying all clients with their respective test plan
 * 
 * @author achavan
 *
 */
public class TestPlanDispatcherThread implements Runnable {
	
	final static Logger logger = LoggerFactory.getLogger(TestPlanDispatcherThread.class);

	
	private final Config config;
	private final ControlServer controlServer;
	private boolean standbyExpired;
	private String startPollDateTime;
	private String endPollDateTime;
	private List<List<BigInteger>> cardRangeList;
	private Random RANDOM = new Random();

	public TestPlanDispatcherThread(Config config, ControlServer cs) {
		this.config = config;
		this.controlServer = cs;
		this.standbyExpired = false;
		this.cardRangeList = new ArrayList<List<BigInteger>>();
	}
	
	@Override
	public void run() {
		try {
			Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
			startPollDateTime = calendar.getTime().toString();
			Thread.sleep(this.config.getServer().getStandby()*1000);
			this.standbyExpired = true;
			endPollDateTime = new Date().toString();
			// TODO Formulate the plan  and pass the data to the clients listening.
			List<TestPlanMsg> testPlanMsgList = formulateTestPlanSetup();
			logger.info("Server will now dispatch test plan to clients available in the pool.");
			Map<String,ClientCommunicationHandler> clientList = controlServer.getClientCommunicationHandlerList();
			int clientHandlerCount = 0;
			for(Map.Entry<String, ClientCommunicationHandler> entry : clientList.entrySet()) {
				entry.getValue().writeMessage(testPlanMsgList.get(clientHandlerCount));
				clientHandlerCount++;
			}
			if(clientHandlerCount == 0) {
				logger.warn("No clients available. Server will exit since no client available to execute test plan.");
			}
		} catch (Exception ex) {
			logger.error("Problem occured in TestPlanDispatcherThread. Error: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	/**
	 * Method returns if standbyPeriod has expired.
	 * @return true or false
	 */
	public boolean hasStandbyPeriodExpired() {
		return standbyExpired == true;
	}
	
	public List<TestPlanMsg> formulateTestPlanSetup() {
		List<TestPlanMsg> msgList = new ArrayList<TestPlanMsg>();
		prepareCardRange();
		int cidCount =  totalNumberOfIdsInPlay(config.getServer().getSetup().getCardSetup());
		logger.info("Total number of customer Id's involved in this test are - " + cidCount);
		int clientCount = controlServer.availableExecutorCount();
		boolean randomCardRangeSelection = false;
		if(clientCount < cardRangeList.size()) {
			randomCardRangeSelection = true;
		}
		
		if(clientCount > 0) {
			for(int i=0; i < clientCount; i++) {
				TestPlanMsg msg = new TestPlanMsg();
				msg.setCappingCallCount(config.getConfiguredSimulation().getCappingCallCount());
				msg.setCappingThreadCount(config.getConfiguredSimulation().getCappingThreadCount());
				msg.setCapReportFrequency(config.getConfiguredSimulation().getCapReportFrequency());
				msg.setCardRangeList(getCardRange(randomCardRangeSelection, i));
				msg.setEventReportFrequency(config.getConfiguredSimulation().getEventReportFrequency());
				msg.setNetworkId(config.getServer().getSetup().getRetailerInfo().getNetworkId());
				msg.setTargetingCallCount(config.getConfiguredSimulation().getTargetingCallCount());
				msg.setTargetingThreadCount(config.getConfiguredSimulation().getTargetingThreadCount());
				msg.setUserName(System.getProperty("user.name"));
				msgList.add(msg);
			}
		}
		return msgList;
	}
	
	/**
	 * Returns  the card range to be used for test plan execution.
	 * @param randomCardRangeSelection
	 * @param currentIndex
	 * @return cardRange
	 */
	public List<BigInteger> getCardRange(boolean randomCardRangeSelection, int currentIndex) {
		if(randomCardRangeSelection) {
			return cardRangeList.get(RANDOM.nextInt(cardRangeList.size()));
		}else {
			return cardRangeList.get(currentIndex % cardRangeList.size());
		}
	}
	
	/**
	 * Returns all cids that have been configured for this environment.
	 * @return List<BigInteger>
	 */
	public List<BigInteger> createCidList() {
		List<BigInteger> cidList = new ArrayList<BigInteger>();
		List<CardSetup> cardSetupList = config.getCardSetupList();
		for (CardSetup cardSetup : cardSetupList) {
			BigInteger firstId = new BigInteger(cardSetup.cardRange().get(0));
			BigInteger lastId = new BigInteger(cardSetup.cardRange().get(1));
			for (; firstId.compareTo(lastId) <= 0; firstId = firstId.add(BigInteger.ONE)) {
				cidList.add(firstId);
			}
		}
		return cidList;
	}
	
	
	/**
	 * Calculates the total number of ids involved in the performance setup.
	 * @param cardSetupList
	 * @return
	 */
	public int totalNumberOfIdsInPlay(List<CardSetup> cardSetupList) {
		int count = 0;
		for(CardSetup cardSetup : cardSetupList) {
			BigInteger firstId = new BigInteger(cardSetup.cardRange().get(0));
			BigInteger lastId = new BigInteger(cardSetup.cardRange().get(1));
			count += lastId.subtract(firstId).intValue();
		}
		return count;
	}
	
	/**
	 * Prepare card range list
	 */
	public void prepareCardRange() {
		for(CardSetup cardSetup : config.getCardSetupList()) {
			BigInteger firstId = new BigInteger(cardSetup.cardRange().get(0));
			BigInteger lastId = new BigInteger(cardSetup.cardRange().get(1));
			List<BigInteger> cardRangeIds = new ArrayList<BigInteger>();
			cardRangeIds.add(firstId);
			cardRangeIds.add(lastId);
			cardRangeList.add(cardRangeIds);
		}
		
	}

	public String getStartPollDateTime() {
		return startPollDateTime;
	}

	public void setStartPollDateTime(String startPollDateTime) {
		this.startPollDateTime = startPollDateTime;
	}

	public String getEndPollDateTime() {
		return endPollDateTime;
	}

	public void setEndPollDateTime(String endPollDateTime) {
		this.endPollDateTime = endPollDateTime;
	}
}
