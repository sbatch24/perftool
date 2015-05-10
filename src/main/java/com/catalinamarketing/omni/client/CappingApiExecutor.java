package com.catalinamarketing.omni.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.catalinamarketing.omni.util.HttpResponseRepository;
import com.catalinamarketing.omni.util.MediaUsageRepository;


public class CappingApiExecutor  extends ApiExecutor {
	
	final static Logger logger = LoggerFactory.getLogger(CappingApiExecutor.class);

	private final int reportEventDelay;
	private final String threadGroupIdentifier;
	private MediaUsageRepository mediaUsageRepository;
	private CountDownLatch finishedSignal;
	private final int counter;
	//TODO remove this
	private static final List<Response.Status> VALUES =
		    Collections.unmodifiableList(Arrays.asList(Response.Status.values()));
	private static final int SIZE = VALUES.size();
	private static final Random RANDOM = new Random();
	
	public CappingApiExecutor(int threadIndex ,int reportEventDelay,int callCount, 
			String threadGroupIdentifier, CountDownLatch finishedSignal, 
			MediaUsageRepository mediaUsageRepository, HttpResponseRepository responseRepository) {
		super(threadIndex, "CappingApi", responseRepository);
		this.reportEventDelay =  reportEventDelay;
		this.threadGroupIdentifier = threadGroupIdentifier;
		this.mediaUsageRepository = mediaUsageRepository;
		this.finishedSignal = finishedSignal;
		this.counter = callCount;
	}
	
	@Override
	public void run() {
		int count = 0;
		try {
			while(count < counter) {
				try {
					//logger.info("Making capping usage call");
					Thread.sleep(reportEventDelay*1000);
					incrementResponseCounter(VALUES.get(RANDOM.nextInt(SIZE)));
					Map<String, Integer>channelMediaCapList = mediaUsageRepository.getMediaCounter(threadGroupIdentifier);
					for (Map.Entry<String, Integer> entry : channelMediaCapList.entrySet()) {
					    String key = entry.getKey();
					    Integer value = entry.getValue();
					    logger.info("Cap reported ChannelMedia Id " + key + " cap " + value.toString());
					}
					mediaUsageRepository.resetMediaCounters(threadGroupIdentifier);
					count++;
				}catch(Exception ex) {
					logger.error("Problem occured in Capping API Executor thread. Error: "+ ex.getMessage());
					ex.printStackTrace();
				}
			}
		}catch(Exception ex) {
			logger.error("Problem occured in Capping API Executor thread. Error: "+ ex.getMessage());
			ex.printStackTrace();
		}finally {
			logger.info("Capping API thread#"+this.threadIndex() +" finish execution. ThreadGroup Identifier "+ threadGroupIdentifier + " countdown " + finishedSignal.getCount());
			finishedSignal.countDown();	
		}
	}

	public int getReportEventDelay() {
		return reportEventDelay;
	}
}
