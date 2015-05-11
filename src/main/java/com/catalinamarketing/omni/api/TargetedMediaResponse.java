package com.catalinamarketing.omni.api;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="media")
public class TargetedMediaResponse {
	
	@XmlElementWrapper(name="directDeposits")
	private List<DirectDeposit> directDeposit;
	
	public List<DirectDeposit> getDirectDeposits() {
		return directDeposit;
	}

	public void setDirectDeposits(List<DirectDeposit> directDeposits) {
		this.directDeposit = directDeposits;
	}

	public TargetedMediaResponse() {
	}
	
	
}
