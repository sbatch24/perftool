package com.catalinamarketing.omni.client;

import javax.ws.rs.core.Response;

import com.catalinamarketing.omni.util.HttpResponseRepository;

public abstract class ApiExecutor implements Runnable {
	private final String apiType;
	HttpResponseRepository httpResponseRepository;
	protected final int threadIndex;
	
	public ApiExecutor(int threadIndex, String apiType, HttpResponseRepository httpResponseRepository) {
		this.threadIndex = threadIndex;
		this.apiType = apiType;
		this.httpResponseRepository = httpResponseRepository;
	}
	
	public void incrementResponseCounter(Response.Status status) {
		httpResponseRepository.addHttpResponseCode("", status);
	}
	
	public int threadIndex() {
		return this.threadIndex;
	}

	public String getApiType() {
		return apiType;
	}
}
