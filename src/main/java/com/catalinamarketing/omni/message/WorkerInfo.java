package com.catalinamarketing.omni.message;

import java.util.List;

import com.catalinamarketing.omni.protocol.message.ApiException;
import com.catalinamarketing.omni.protocol.message.ApiHttpResponseCounter;
import com.catalinamarketing.omni.protocol.message.ApiMetricRegistry;

public class WorkerInfo {
	
	private String hostName;
	private String userName;
	private String status;
	private List<ApiHttpResponseCounter> apiResponseCounterList;
	private List<ApiException> apiExceptionList;
	private List<ApiMetricRegistry> metricRegistryList;
	
	
	public WorkerInfo(String hostName, String userName, String status) {
		this.hostName = hostName;
		this.userName = userName;
		this.status = status;
	}
	
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<ApiHttpResponseCounter> getApiResponseCounterList() {
		return apiResponseCounterList;
	}

	public void setApiResponseCounterList(List<ApiHttpResponseCounter> apiResponseCounterList) {
		this.apiResponseCounterList = apiResponseCounterList;
	}

	public List<ApiException> getApiExceptionList() {
		return apiExceptionList;
	}

	public void setApiExceptionList(List<ApiException> apiExceptionList) {
		this.apiExceptionList = apiExceptionList;
	}

	public List<ApiMetricRegistry> getMetricRegistryList() {
		return metricRegistryList;
	}

	public void setMetricRegistryList(List<ApiMetricRegistry> metricRegistryList) {
		this.metricRegistryList = metricRegistryList;
	}
	

}
