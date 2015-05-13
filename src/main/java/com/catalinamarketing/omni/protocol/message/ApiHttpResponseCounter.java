package com.catalinamarketing.omni.protocol.message;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="apiType")
public class ApiHttpResponseCounter {
	private String apiType;
	private List<HttpResponseCodeCounter> httpResposeCodeCounter;
	private float averageResponseTime;
	private int count;
	
	public ApiHttpResponseCounter(){
	}
	
	public void addHttpResponseCodeCounter(HttpResponseCodeCounter counter) {
		if(httpResposeCodeCounter == null) {
			httpResposeCodeCounter = new ArrayList<HttpResponseCodeCounter>();
		}
		httpResposeCodeCounter.add(counter);
	}
	
	public List<HttpResponseCodeCounter> getHttpResposeCodeCounter() {
		return httpResposeCodeCounter;
	}

	public void setHttpResposeCodeCounter(List<HttpResponseCodeCounter> httpResposeCodeCounter) {
		this.httpResposeCodeCounter = httpResposeCodeCounter;
	}

	public String getApiType() {
		return apiType;
	}

	public void setApiType(String apiType) {
		this.apiType = apiType;
	}
}
