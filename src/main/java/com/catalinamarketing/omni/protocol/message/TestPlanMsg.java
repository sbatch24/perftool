package com.catalinamarketing.omni.protocol.message;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
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
	private String testPlanVersion;
	private String targetingApiUrl;
	private String eventsApiUrl;
	private String cappingUsageApiUrl;
	@XmlElementWrapper(name="awardDataList")
	@XmlElement(name="awardData")
	private List<AwardData> awardDataList;
	
	
	public TestPlanMsg() {
	}
	
	public List<AwardData> getAwardDataList() {
		return awardDataList;
	}
	
	public void addAwardData(AwardData awardData) {
		if(awardDataList == null) {
			awardDataList = new ArrayList<AwardData>();
		}
		awardDataList.add(awardData);
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
	
	public String printMessage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Test Plan to execute - \n");
		buffer.append("\nCid Range - (" + this.cardRange.get(0) +" - " + cardRange.get(1) + " )");
		buffer.append("\nTargeting Thread count - " + this.targetingThreadCount);
		buffer.append("\nCapping Thread count - " + this.cappingThreadCount);
		buffer.append("\nCap usage report frequency - " + this.capReportFrequency +" seconds");
		buffer.append("\nTargeting event report frequency - "+ this.eventReportFrequency + " seconds");
		buffer.append("\nUsage report call count - "+this.cappingCallCount);
		buffer.append("\nTargeting call count - "+ this.targetingCallCount);
		buffer.append("\nTest performed for retailer - "+ this.retailerId);
		buffer.append("\nTest plan version - "+this.testPlanVersion);
		return buffer.toString();
	}

	public String getTestPlanVersion() {
		return testPlanVersion;
	}

	public void setTestPlanVersion(String testPlanVersion) {
		this.testPlanVersion = testPlanVersion;
	}

	public String getRetailerId() {
		return retailerId;
	}

	public void setRetailerId(String retailerId) {
		this.retailerId = retailerId;
	}

	public String getTargetingApiUrl() {
		return targetingApiUrl;
	}

	public void setTargetingApiUrl(String targetingApiUrl) {
		this.targetingApiUrl = targetingApiUrl;
	}

	public String getCappingUsageApiUrl() {
		return cappingUsageApiUrl;
	}

	public void setCappingUsageApiUrl(String cappingUsageApiUrl) {
		this.cappingUsageApiUrl = cappingUsageApiUrl;
	}
	
	/**
	 * Finds the channelMediaId corresponding to the awardId.
	 * @param awardId
	 * @return channelMediaId
	 */
	public String getChannelMediaId(String awardId) {
		String channelMediaId = null;
		for(AwardData data : awardDataList) {
			if(data.getAwardId().equalsIgnoreCase(awardId)) {
				channelMediaId = data.getChannelMediaId();
				break;
			}
		}
		return channelMediaId;
	}

	public String getEventsApiUrl() {
		return eventsApiUrl;
	}

	public void setEventsApiUrl(String eventsApiUrl) {
		this.eventsApiUrl = eventsApiUrl;
	}
}
