package com.catalinamarketing.omni.util;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class HttpResponseRepository {
	private Object mutex;
	private Map<String,Multiset<Response.Status>> httpResponseCodes;
	
	
	public HttpResponseRepository() {
		httpResponseCodes = new HashMap<>();
		mutex = new Object();
	}
	
	/**
	 * Add http response code to the repository per apiType.
	 * Repository will be maintained for get Targeting calls, report Event calls, report usage capping api.
	 * @param apiType
	 * @param status
	 */
	public void addHttpResponseCode(String apiType, Response.Status status) {
		synchronized (mutex) {
			if(!httpResponseCodes.containsKey(apiType)) {
				 Multiset<Response.Status> responseCodes = HashMultiset.create();
				 httpResponseCodes.put(apiType, responseCodes);
			}
			
			 Multiset<Response.Status> responseCodes = httpResponseCodes.get(apiType);
			 responseCodes.add(status);
		}
	}
}
