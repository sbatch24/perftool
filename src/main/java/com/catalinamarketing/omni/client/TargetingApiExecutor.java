package com.catalinamarketing.omni.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.catalinamarketing.omni.util.HttpResponseRepository;
import com.catalinamarketing.omni.util.MediaCounter;
import com.catalinamarketing.omni.util.MediaUsageRepository;



/**
 * This thread class is reponsible for executing the targeting api.
 * @author achavan
 *
 */
public class TargetingApiExecutor extends ApiExecutor {
	final static Logger logger = LoggerFactory.getLogger(TargetingApiExecutor.class);
	private final int reportEventDelay;
	private final int callCount;
	private final String threadGroupIdentifier;
	private MediaUsageRepository mediaUsageRepository;
	private static final List<Response.Status> VALUES =
		    Collections.unmodifiableList(Arrays.asList(Response.Status.values()));
	private static final int SIZE = VALUES.size();
	private static final Random RANDOM = new Random();
	private CountDownLatch finishedSignal;
	
	public TargetingApiExecutor(int threadNumber, int reportEventDelay, int callCount, 
			String threadGroupIdentifier, CountDownLatch finishedSignal, MediaUsageRepository mediaUsageRepository, HttpResponseRepository responseRepository) {
		super(threadNumber, "TargetingApi", responseRepository);
		this.reportEventDelay =  reportEventDelay;
		this.callCount = callCount;
		this.threadGroupIdentifier = threadGroupIdentifier;
		this.finishedSignal = finishedSignal;
		this.mediaUsageRepository = mediaUsageRepository;
	}
	
	@Override
	public void run() {
		List<String> medias = Arrays.asList("6012", "6013", "6014", "6015", "6016");
		int count = 0;
		try {
			while(count < callCount && !halted()) {
				try {
					logger.info("Making targeting call");
					Thread.sleep(reportEventDelay*1000);
					incrementResponseCounter(VALUES.get(RANDOM.nextInt(SIZE)));
					String m1 = medias.get(RANDOM.nextInt(5));
					String m2 = medias.get(RANDOM.nextInt(5));
					mediaUsageRepository.incrementMediaCounter(threadGroupIdentifier, m1);
					mediaUsageRepository.incrementMediaCounter(threadGroupIdentifier, m2);
					count++;
				}catch(Exception ex) {
					logger.error("Problem occured in Targeting API Executor thread. Error: "+ ex.getMessage());
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
}
