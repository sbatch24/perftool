package com.catalinamarketing.omni.util;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * 
 * @author achavan
 *
 */
public class HttpResponseRepository {
	private Object mutex;
	/**
	 * Map<ApiCallName, MultiSet<httpresponseCodes>>
	 */
	private Map<String,Multiset<Integer>> httpResponseCodes;
	
	private Map<String,Multiset<String>> exceptionMessageList;
	
	public HttpResponseRepository() {
		httpResponseCodes = new HashMap<>();
		exceptionMessageList = new HashMap<>();
		mutex = new Object();
	}
	
	/**
	 * Add http response code to the repository per apiType.
	 * Repository will be maintained for get Targeting calls, report Event calls, report usage capping api.
	 * @param apiType
	 * @param status
	 */
	public void addHttpResponseCode(String apiType, int status) {
		synchronized (mutex) {
			if(!httpResponseCodes.containsKey(apiType)) {
				 Multiset<Integer> responseCodes = HashMultiset.create();
				 httpResponseCodes.put(apiType, responseCodes);
			}
			
			 Multiset<Integer> responseCodes = httpResponseCodes.get(apiType);
			 responseCodes.add(status);
		}
	}
	
	public Map<String,Multiset<Integer>> getHttpCodeResponseRepository() {
		return this.httpResponseCodes;
	}

	

	public Map<String,Multiset<String>> getExceptionMessageList() {
		return exceptionMessageList;
	}

	
	
	public void addException(String message, String apiType) {
		synchronized (mutex) {
			if(exceptionMessageList.get(apiType) == null) {
				Multiset<String> exceptions = HashMultiset.create();
				exceptionMessageList.put(apiType, exceptions);
			}
			Multiset<String> exceptions = exceptionMessageList.get(apiType);
			exceptions.add(message);
		}
	}
}
