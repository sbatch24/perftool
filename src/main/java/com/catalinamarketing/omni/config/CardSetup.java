package com.catalinamarketing.omni.config;

import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="cardSetup")
public class CardSetup {
	private String cardRange;
	private String cardRangeId;
	
	/**
	 * Returns the card range as a list.
	 * Contains two elements.
	 * [0] - first element in the range
	 * [1] - last element in the range
	 * @return List<String> contains first and last element in the list;
	 */
	public List<String> cardRange() {
		List<String> cardRangeList = null;
		if(cardRange.contains("-")) {
			cardRangeList = Arrays.asList(cardRange.split("\\-"));	
		} else {
			cardRangeList = Arrays.asList(cardRange);
		}
		return cardRangeList;
	}
	
	public CardSetup() {
	}
	
	public String getCardRange() {
		return cardRange;
	}
	public void setCardRange(String cardRange) {
		this.cardRange = cardRange;
	}
	public String getCardRangeId() {
		return this.cardRangeId;
	}
	public void setCardRangeId(String id) {
		this.cardRangeId = id;
	}
}
