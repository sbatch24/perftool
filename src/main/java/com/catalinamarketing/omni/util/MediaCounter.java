package com.catalinamarketing.omni.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * ChannelMedia Counter repository.
 * The reason for using multiset is we want a fast data structure which keeps track of
 * count of the same entities that put in the set.
 * @author achavan
 *
 */
public class MediaCounter {
	private Multiset<String> mediaList;
	
	public MediaCounter() {
		this.mediaList = HashMultiset.create();
	}
	
	/**
	 * Add channelMediaId
	 * @param channelMediaId
	 */
	public void add(String channelMediaId) {
		mediaList.add(channelMediaId);
	}
	
	/**
	 * Remove all channelMediaIds from the list.
	 */
	public void resetAll() {
		mediaList.clear();
	}
	
	/**
	 * Remove all mediaIds from the list that are in the input list
	 * @param channelMediaIdList
	 */
	public void reset(List<String> channelMediaIdList) {
		mediaList.removeAll(channelMediaIdList);
	}
	
	public Map<String, Integer> getChannelMediaUsageCount() {
		Map<String, Integer> usageList = new HashMap<String, Integer>();
		for(String channelMediaId : mediaList.elementSet()) {
			usageList.put(channelMediaId,mediaList.count(channelMediaId));
		}
		return usageList;
	}
}
