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
import com.catalinamarketing.omni.pmr.setup.AwardInfo;
import com.catalinamarketing.omni.pmr.setup.PmrDataOrganizer;
import com.catalinamarketing.omni.pmr.setup.PmrSetupMessage;
import com.catalinamarketing.omni.pmr.setup.ProgramInfo;
import com.catalinamarketing.omni.protocol.message.AwardData;
import com.catalinamarketing.omni.protocol.message.TestPlanMsg;
import com.catalinamarketing.omni.server.ClientCommunicationHandler.STATUS;

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
	private final  List<PmrSetupMessage> pmrSetupMessageList;
	private List<List<BigInteger>> cardRangeList;
	private Random RANDOM = new Random();

	public TestPlanDispatcherThread(Config config, ControlServer cs, List<PmrSetupMessage> pmrSetupMessageList) {
		this.config = config;
		this.controlServer = cs;
		this.pmrSetupMessageList = pmrSetupMessageList;
		this.cardRangeList = new ArrayList<List<BigInteger>>();
	}
	
	@Override
	public void run() {
		try {
			List<TestPlanMsg> testPlanMsgList = formulateTestPlanSetup();
			logger.info(""+controlServer.getClientCommunicationHandlerList().size()+" clients in the pool.Server will now dispatch test plan to clients available in the pool. Time " + Calendar.getInstance().getTime().toString());
			Map<String,ClientCommunicationHandler> clientList = controlServer.getClientCommunicationHandlerList();
			int clientHandlerCount = 0;
			for(Map.Entry<String, ClientCommunicationHandler> entry : clientList.entrySet()) {
				entry.getValue().writeMessage(testPlanMsgList.get(clientHandlerCount));
				entry.getValue().setStatus(STATUS.BUSY_EXECUTING_TEST);
				clientHandlerCount++;
			}
			if(clientHandlerCount == 0) {
				logger.warn("No clients available. Retry again when clients are available.");
			}
		} catch (Exception ex) {
			logger.error("Problem occured in TestPlanDispatcherThread. Error: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	/**
	 * Based on number of clients available in pool and the type of setup a TestPlan will be formulated
	 * and distributed to each client.
	 * @return List of TestPlanMsg.
	 */
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
				msg.setRetailerId(config.getServer().getSetup().getRetailerInfo().getRetailerId());
				msg.setTargetingCallCount(config.getConfiguredSimulation().getTargetingCallCount());
				msg.setTargetingThreadCount(config.getConfiguredSimulation().getTargetingThreadCount());
				msg.setTestPlanVersion(config.getServer().getTestPlanVersion());
				msg.setTargetingApiUrl(config.getTargetingApiUrl());
				msg.setCappingUsageApiUrl(config.getCappingApiUrl());
				msg.setEventsApiUrl(config.getEventsApiUrl());
				msg.setUserName(System.getProperty("user.name"));
				for(PmrSetupMessage pmrSetupMessage : this.pmrSetupMessageList) {
					for(ProgramInfo programInfo :pmrSetupMessage.getPrograms()) {
						for(AwardInfo awardInfo  : programInfo.getAwards()) {
							AwardData awardData = new AwardData();
							awardData.setAwardId(awardInfo.getAwardID());
							awardData.setChannelMediaId(awardInfo.getChannelMediaId());
							msg.addAwardData(awardData);
						}
					}
				}
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
}
