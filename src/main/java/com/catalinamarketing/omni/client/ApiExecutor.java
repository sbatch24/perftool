package com.catalinamarketing.omni.client;

import javax.ws.rs.core.Response;

import com.catalinamarketing.omni.util.HttpResponseRepository;

public abstract class ApiExecutor implements Runnable {
	HttpResponseRepository httpResponseRepository;
	protected final int threadIndex;
	private boolean halt;
	
	public ApiExecutor(int threadIndex, HttpResponseRepository httpResponseRepository) {
		this.threadIndex = threadIndex;
		this.httpResponseRepository = httpResponseRepository;
		this.halt = false;
	}
	
	public void incrementResponseCounter(String apiType,Response.Status status) {
		httpResponseRepository.addHttpResponseCode(apiType, status);
	}
	
	public int threadIndex() {
		return this.threadIndex;
	}

	protected void halt() {
		this.halt = true;
	}
	
	protected boolean halted() {
		return this.halt;
	}
	
}
