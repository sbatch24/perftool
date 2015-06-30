package com.catalinamarketing.omni.protocol.message;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="testExecutionResult")
public class TestExecutionResultMsg extends Message {
	
	private List<ApiHttpResponseCounter> apiResponseCounterList;
	private List<ApiException> apiExceptionList;
	private List<ApiMetricRegistry> metricRegistryList;

	public List<ApiHttpResponseCounter> getApiResponseCounterList() {
		return apiResponseCounterList;
	}

	public void setApiResponseCounterList(
			List<ApiHttpResponseCounter> apiResponseCounterList) {
		this.apiResponseCounterList = apiResponseCounterList;
	}

	@Override
	public String printMessage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Test results from worker node : " + userName + "\n");
		if(apiResponseCounterList != null) {
			for(ApiHttpResponseCounter counter : apiResponseCounterList) {
				buffer.append("ApiType : " + counter.getApiType() + "\n");
				for(HttpResponseCodeCounter httpCodeCounter : counter.getHttpResposeCodeCounter()) {
					buffer.append("\t httpCode : "+ httpCodeCounter.getResponseCode() + " count : " + httpCodeCounter.getCount());
					buffer.append("\n");
				}
			}	
		} 
		
		if(apiExceptionList != null) {
			for(ApiException exception : apiExceptionList) {
				buffer.append("\t ApiType : " + exception.getApiType() + " exception : " + exception.getExceptionMessage() + " count : " + exception.getCount());
				buffer.append("\n");
			}
		}
		return buffer.toString();
	}
	
	public void addCounter(String apiType, HttpResponseCodeCounter httpResponseCounter) {
		ApiHttpResponseCounter counter = null;
		if(apiResponseCounterList == null) {
			apiResponseCounterList = new ArrayList<ApiHttpResponseCounter>();
		}
		for(ApiHttpResponseCounter apiHttpCounter : apiResponseCounterList) {
			if(apiHttpCounter.getApiType().equalsIgnoreCase(apiType)) {
				counter = apiHttpCounter;
				break;
			}
		}
		if(counter == null) {
			counter = new ApiHttpResponseCounter();
			counter.setApiType(apiType);
			apiResponseCounterList.add(counter);
		}
		counter.addHttpResponseCodeCounter(httpResponseCounter);
	}

	public List<ApiException> getApiExceptionList() {
		return apiExceptionList;
	}

	public void setApiExceptionList(List<ApiException> apiExceptionList) {
		this.apiExceptionList = apiExceptionList;
	}
	
	public void addException(String apiType, String exceptionMessage, int count) {
		if(apiExceptionList == null) {
			apiExceptionList = new ArrayList<ApiException>();
		}
		
		ApiException exception = new ApiException();
		exception.setApiType(apiType);
		exception.setCount(count);
		exception.setExceptionMessage(exceptionMessage);
		apiExceptionList.add(exception);
	}

	public List<ApiMetricRegistry> getMetricRegistryList() {
		return metricRegistryList;
	}

	public void setMetricRegistryList(List<ApiMetricRegistry> metricRegistryList) {
		this.metricRegistryList = metricRegistryList;
	}
	
	public void addMetriRegistry(ApiMetricRegistry registry) {
		if(metricRegistryList == null) {
			metricRegistryList = new ArrayList<ApiMetricRegistry>();
		}
		metricRegistryList.add(registry);
	}
}
