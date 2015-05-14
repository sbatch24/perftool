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
	
	public ApiHttpResponseCounter(){
	}
	
	public void addHttpResponseCodeCounter(HttpResponseCodeCounter counter) {
		if(httpResposeCodeCounter == null) {
			httpResposeCodeCounter = new ArrayList<HttpResponseCodeCounter>();
		}
		boolean found = false;
		for(HttpResponseCodeCounter item : httpResposeCodeCounter) {
			if(item.getResponseCode() == counter.getResponseCode()) {
				item.setCount(item.getCount() + counter.getCount());
				found = true;
			}
		}
		if(!found) {
			httpResposeCodeCounter.add(counter);
		}
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
