package com.catalinamarketing.omni.config;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="setup")
public class Setup {
	
	private RetailerInfo retailerInfo;
	@XmlElementWrapper(name="programSetupList")
	@XmlElement(name="programSetup")
	List<ProgramSetup> programSetup;
	
	@XmlElementWrapper(name="promotionSetupList")
	@XmlElement(name="promotionSetup")
	List<PromotionSetup> promotionSetup;
	
	@XmlElementWrapper(name="cardSetupList")
	@XmlElement(name="cardSetup")
	List<CardSetup> cardSetup;
	
	public List<ProgramSetup> getProgramSetup() {
		return programSetup;
	}

	public void setProgramSetup(List<ProgramSetup> programSetup) {
		this.programSetup = programSetup;
	}

	public List<PromotionSetup> getPromotionSetup() {
		return promotionSetup;
	}

	public void setPromotionSetup(List<PromotionSetup> promotionSetup) {
		this.promotionSetup = promotionSetup;
	}

	public List<CardSetup> getCardSetup() {
		return cardSetup;
	}

	public void setCardSetup(List<CardSetup> cardSetup) {
		this.cardSetup = cardSetup;
	}

	public Setup(List<ProgramSetup> programSetupList, List<PromotionSetup> promotionSetupList, 
				List<CardSetup> cardSetupList) {
		this.programSetup = programSetupList;
		this.promotionSetup = promotionSetupList;
		this.cardSetup = cardSetupList;
	}
	
	public Setup() {
		
	}

	public RetailerInfo getRetailerInfo() {
		return retailerInfo;
	}

	public void setRetailerInfo(RetailerInfo retailer) {
		this.retailerInfo = retailer;
	}
	

}
