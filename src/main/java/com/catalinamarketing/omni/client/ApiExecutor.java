package com.catalinamarketing.omni.client;

import javax.ws.rs.core.Response;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public abstract class ApiExecutor implements Runnable {
	
	private Multiset<Response.Status> httpResponseCodes = HashMultiset.create();
	
	public void incrementResponseCounter(Response.Status status) {
		httpResponseCodes.add(status);
	}
}
