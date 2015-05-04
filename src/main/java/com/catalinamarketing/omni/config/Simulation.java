package com.catalinamarketing.omni.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="simulation")
public class Simulation {
	private Retailer retailer;
	private String type;
	private int targetingThreadCount;
	private int cappingThreadCount;
	
	private int eventReportFrequency;
	private int capReportFrequency;
	
	private int targetingCallCount;
	private int cappingCallCount;
	
	public Simulation() {
		
	}
	public Simulation(Retailer retailer, String type, int targetingThreadCount,
			int cappingThreadCount, int eventReportFq, int capReportFq,
			int targetingCallCount, int cappingCallCount) {
		this.retailer = retailer;
		this.type = type;
		this.targetingThreadCount = targetingThreadCount;
		this.cappingThreadCount = cappingThreadCount;
		this.eventReportFrequency = eventReportFq;
		this.capReportFrequency = capReportFq;
		this.setTargetingCallCount(targetingCallCount);
		this.setCappingCallCount(cappingCallCount);
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
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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

	public Retailer getRetailer() {
		return retailer;
	}
	public void setRetailer(Retailer retailer) {
		this.retailer = retailer;
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
