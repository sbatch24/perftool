package com.catalinamarketing.omni.client;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

import javax.ws.rs.core.Response;

import com.catalinamarketing.omni.util.HttpResponseRepository;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

public abstract class ApiExecutor implements Runnable {
	HttpResponseRepository httpResponseRepository;
	protected final int threadIndex;
	protected final String threadGroupIdentifier;
	protected CountDownLatch finishedSignal;
	private boolean halt;
	private boolean completeExecution;
	private boolean haltOnException;
	protected static final MetricRegistry metrics = new MetricRegistry();
	protected static final Timer TARGET_API_REQUEST = metrics.timer("GetTargetedMedia");
	protected static final Timer EVENT_API_REQUEST = metrics.timer("ReportEvents");
	protected static final Timer CAPPING_API_REQUEST = metrics.timer("ReportUsage");
	private static  Random rand = new Random();

	
	public abstract String apiName();
	
	public ApiExecutor(int threadIndex, String threadGroupIdentifier, HttpResponseRepository httpResponseRepository,
			CountDownLatch finishedSignal) {
		this.threadIndex = threadIndex;
		this.threadGroupIdentifier = threadGroupIdentifier;
		this.httpResponseRepository = httpResponseRepository;
		this.halt = false;
		this.finishedSignal = finishedSignal;
		completeExecution = false;
		haltOnException = false;
	}
	
	public void seriousException(String apiType, String exception) {
		httpResponseRepository.addException(exception, apiType);
	}
	
	public void incrementResponseCounter(String apiType,Integer status) {
		httpResponseRepository.addHttpResponseCode(apiType, status);
	}
	
	public int threadIndex() {
		return this.threadIndex;
	}

	protected void halt() {
		this.halt = true;
	}
	
	protected void completedExecution() {
		this.completeExecution = true;
	}
	
	protected boolean didCompleteExecution() {
		return this.completeExecution;
	}
	
	protected void haltedOnException() {
		haltOnException = true;
		this.halt = true;
	}
	
	protected boolean didExceptionOccured() {
		return haltOnException;
	}
	
	protected boolean halted() {
		return this.halt;
	}
	
	protected void finishedRun() {
		finishedSignal.countDown();
	}
	
	private String statusCode() {
		if(haltOnException) {
			return "Halted execution due to exception";
		} else if(halted()) { 
			return "Server requested to halt execution";
		} else if(didCompleteExecution()) {
			return "Api thread completed execution";
		}
		return "";
	}
	
	
	protected String runStatus() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(apiName() + " thread#"+threadIndex() +" finish execution. ThreadGroup Identifier "+ threadGroupIdentifier + 
				" countdown " + finishedSignal.getCount() + " StatusCode " + statusCode());
		return buffer.toString();
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
	protected static int randInt(int min, int max) {
	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
}
