package com.catalinamarketing.omni.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="promotionSetup")
public class PromotionSetup {
	private String awardRange;
	private String mediaIdRange;
	private Integer awardCap;
	private Integer awardVariance;
	private Integer mediaCap;
	private Integer mediaVariance;
	private String channelType;
	private String cardRangeId;
	private int consumerCap;
	private String programSetupId;
	private String startDate;
	private String endDate;
	
	/**
	 * Returns the promotion range as a list.
	 * Contains two elements.
	 * [0] - first element in the range
	 * [1] - last element in the range
	 * @return List<String> contains first and last element in the list;
	 */
	public List<Integer> awardRange() {
		List<Integer> awardRangeList = new ArrayList<Integer>();
		if(awardRange.contains("-")) {
			List<String> awardRangeListStr = Arrays.asList(awardRange.split("\\-"));
			Integer startIndex = Integer.parseInt(awardRangeListStr.get(0));
			Integer endIndex = Integer.parseInt(awardRangeListStr.get(1));
			for(;startIndex <= endIndex ; startIndex++) {
				awardRangeList.add(startIndex);
			}
		} else {
			awardRangeList.add(Integer.parseInt(awardRange));
		}
		return awardRangeList;
	}
	
	/**
	 * Returns the mediaId range as a list.
	 * Contains two elements.
	 * [0] - first element in the range
	 * [1] - last element in the range
	 * @return List<String> contains first and last element in the list;
	 */
	public List<Integer> mediaIdRange() {
		List<Integer> mediaIdRangeList = new ArrayList<Integer>();
		if(mediaIdRange.contains("-")) {
			List<String> mediaIdRangeListStr = Arrays.asList(mediaIdRange.split("\\-"));
			Integer startIndex = Integer.parseInt(mediaIdRangeListStr.get(0));
			Integer endIndex = Integer.parseInt(mediaIdRangeListStr.get(1));
			for(;startIndex <= endIndex ; startIndex++) {
				mediaIdRangeList.add(startIndex);
			}
		} else {
			mediaIdRangeList.add(Integer.parseInt(mediaIdRange));
		}
		return mediaIdRangeList;
	}
	
	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	public String getAwardRange() {
		return awardRange;
	}
	public void setAwardRange(String awardRange) {
		this.awardRange = awardRange;
	}
	public Integer getAwardCap() {
		return awardCap;
	}
	public void setAwardCap(Integer cap) {
		this.awardCap = cap;
	}
	public Integer getAwardVariance() {
		return awardVariance;
	}
	public void setAwardVariance(Integer variance) {
		this.awardVariance = variance;
	}
	public String getCardRangeId() {
		return cardRangeId;
	}
	public void setCardRangeId(String cardRangeId) {
		this.cardRangeId = cardRangeId;
	}
	public Integer getConsumerCap() {
		return consumerCap;
	}
	public void setConsumerCap(Integer consumerCap) {
		this.consumerCap = consumerCap;
	}
	public String getProgramSetupId() {
		return programSetupId;
	}
	public void setProgramSetupId(String id) {
		this.programSetupId = id;
	}

	public String getMediaIdRange() {
		return mediaIdRange;
	}

	public void setMediaIdRange(String mediaIdRange) {
		this.mediaIdRange = mediaIdRange;
	}

	public Integer getMediaCap() {
		return mediaCap;
	}

	public void setMediaCap(Integer cap) {
		this.mediaCap = cap;
	}

	public Integer getMediaVariance() {
		return mediaVariance;
	}

	public void setMediaVariance(Integer mediaVariance) {
		this.mediaVariance = mediaVariance;
	}

	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}
}
