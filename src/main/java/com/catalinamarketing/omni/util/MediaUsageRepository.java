package com.catalinamarketing.omni.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class maintains a repository of mediaUsage counters for each targeting api thread group.
 * Targeting API thread group - The performance tool will be requested to spawn multiple targeting api
 * and capping api threads which will exercise the respective apis. The design of the tool is such that 
 * for there will be one capping thread for 'X' number of targeting thread. The calculation of 'X' is below
 * X = Number of targeting thread / Number of capping threads.
 * @author achavan
 *
 */
public class MediaUsageRepository {
	final static Logger logger = LoggerFactory.getLogger(MediaUsageRepository.class);

	
	private Map<String, MediaCounter> repository;
	private Object mutex;
	
	public MediaUsageRepository() {
		this.repository = new HashMap<String, MediaCounter>();
		mutex = new Object();
	}
	
	public Map<String, MediaCounter> getRepository() {
		return repository;
	}
	
	/**
	 * Method checks to see if MediaCounter has been created for.
	 * The targeting api executor threads are grouped so that for each group of target api executors
	 * there is only one capping api executor.
	 * @param threadGroupIdentifier
	 * @return true if bucket MediaCounter for this set of apiexecutors is created.
	 */
	public boolean isMediaCounterRepositoryCreated(String threadGroupIdentifier) {
		return repository.containsKey(threadGroupIdentifier);
	}
	
	/**
	 * 
	 * @param threadGroupIdentifier
	 * @return
	 */
	public void createMediaRepository(String threadGroupIdentifier) {
		synchronized (mutex) {
			MediaCounter counter = new MediaCounter();
			if(repository.containsKey(threadGroupIdentifier)) {
				logger.error("MediaCounter exists for threadGroupIdentifier " + threadGroupIdentifier);
			} else{
				repository.put(threadGroupIdentifier, counter);	
			}
		}
	}
	
	/**
	 * Finds the correct bucket 
	 * @param threadGroupIdentifier
	 * @param mediaId
	 */
	public void incrementMediaCounter(String threadGroupIdentifier, String mediaId) {
		synchronized (mutex) {
			repository.get(threadGroupIdentifier).add(mediaId);
		}
	}
	
	public Map<String, Integer> getMediaCounter(String bucketId) {
		synchronized (mutex) {
			MediaCounter mediaCounter = repository.get(bucketId);
			return mediaCounter.getChannelMediaUsageCount();
		}
	}
	
	/**
	 * Clears the channelMedia counters from the repository for this
	 * threadGroup.
	 * @param threadGroupIdentifier
	 */
	public void resetMediaCounters(String threadGroupIdentifier) {
		synchronized (mutex) {
			MediaCounter counter = repository.get(threadGroupIdentifier);
			counter.resetAll();
		}
	}
}
