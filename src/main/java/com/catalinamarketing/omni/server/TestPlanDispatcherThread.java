package com.catalinamarketing.omni.server;

import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

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
	
	public TestPlanDispatcherThread(Config config, ControlServer cs) {
		this.config = config;
		this.controlServer = cs;
		this.standbyExpired = false;
	}
	
	@Override
	public void run() {
		//Sleep for standby amount of seconds.
		try {
			Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
			startPollDateTime = calendar.getTime().toString();
			Thread.sleep(this.config.getServer().getStandby()*1000);
			this.standbyExpired = true;
			endPollDateTime = new Date().toString();
			// TODO Formulate the plan  and pass the data to the clients listening.
			List<TestPlanMsg> testPlanMsgList = formulateTestPlanSetup();
			logger.info("Server will now dispatch test plan to clients available in the pool.");
			List<ClientCommunicationHandler> clientList = controlServer.getClientCommunicationHandlerList();
			int clientHandlerCount = 0;
			for(ClientCommunicationHandler handler : clientList) {
				handler.writeMessage(testPlanMsgList.get(clientHandlerCount));
				clientHandlerCount++;
			}
			if(clientHandlerCount == 0) {
				logger.error("No clients available. Server will exit since no client available to execute test plan.");
				controlServer.shutdown();
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
		int cidCount =  totalNumberOfIdsInPlay(config.getServer().getSetup().getCardSetup());
		logger.info("Total number of customer Id's involved in this test are - " + cidCount);
		if(controlServer.availableExecutorCount() > 0) {
			List<BigInteger> cidList = createCidList();
			int cidDistributionCount = cidList.size() / controlServer.availableExecutorCount();
			for(int startIndex = 0;startIndex < cidList.size() ; startIndex += cidDistributionCount) {
				List<BigInteger> cardRange = new ArrayList<BigInteger>();
				cardRange.add(cidList.get(startIndex));
				
				if(((cidList.size()) - (startIndex+cidDistributionCount)) < cidDistributionCount ) {
					cardRange.add(cidList.get(cidList.size()-1));
					startIndex += (cidList.size() - (startIndex + cidDistributionCount));
				} else {
					cardRange.add(cidList.get(startIndex+cidDistributionCount));
				}
				TestPlanMsg msg = new TestPlanMsg();
				msg.setCappingCallCount(config.getConfiguredSimulation().getCappingCallCount());
				msg.setCappingThreadCount(config.getConfiguredSimulation().getCappingThreadCount());
				msg.setCapReportFrequency(config.getConfiguredSimulation().getCapReportFrequency());
				msg.setCardRangeList(cardRange);
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
