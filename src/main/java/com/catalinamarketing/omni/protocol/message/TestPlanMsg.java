package com.catalinamarketing.omni.protocol.message;

import java.math.BigInteger;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="testPlanMsg")
public class TestPlanMsg extends Message {
	@XmlElementWrapper(name="cardRangeList")
	@XmlElement(name="cardRange")
	private List<BigInteger> cardRange;
	private String retailerId;
	private int targetingThreadCount;
	private int cappingThreadCount;
	private int eventReportFrequency;
	private int capReportFrequency;
	private int targetingCallCount;
	private int cappingCallCount;
	
	
	public TestPlanMsg() {
		
	}
	
	public TestPlanMsg(List<BigInteger> cardRange, String retailerId, int tTC, 
			int cTC, int eRF, int cRF) {
		this.cardRange = cardRange;
		this.retailerId = retailerId;
		this.targetingThreadCount = tTC;
		this.cappingThreadCount = cTC;
		this.eventReportFrequency = eRF;
		this.capReportFrequency = cRF;
	}
	
	public List<BigInteger> getCardRangeList() {
		return cardRange;
	}
	public void setCardRangeList(List<BigInteger> cardRange) {
		this.cardRange = cardRange;
	}
	public String getRetailerId() {
		return retailerId;
	}
	public void setRetailerId(String retailerId) {
		this.retailerId = retailerId;
	}
	public int getTargetingThreadCount() {
		return targetingThreadCount;
	}
	public void setTargetingThreadCount(int targetingThreadCount) {
		this.targetingThreadCount = targetingThreadCount;
	}
	public int getCappingThreadCount() {
		return cappingThreadCount;
	}
	public void setCappingThreadCount(int cappingThreadCount) {
		this.cappingThreadCount = cappingThreadCount;
	}
	public int getEventReportFrequency() {
		return eventReportFrequency;
	}
	public void setEventReportFrequency(int eventReportFrequency) {
		this.eventReportFrequency = eventReportFrequency;
	}
	public int getCapReportFrequency() {
		return capReportFrequency;
	}
	public void setCapReportFrequency(int capReportFrequency) {
		this.capReportFrequency = capReportFrequency;
	}

	public int getTargetingCallCount() {
		return targetingCallCount;
	}

	public void setTargetingCallCount(int targetingCallCount) {
		this.targetingCallCount = targetingCallCount;
	}

	public int getCappingCallCount() {
		return cappingCallCount;
	}

	public void setCappingCallCount(int cappingCallCount) {
		this.cappingCallCount = cappingCallCount;
	}
}
