package com.catalinamarketing.omni.api;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class CustomerMediaEvent {
	@XmlElement(required = true)
	protected String customerId;

	@XmlElement
	protected String householdId;

	@XmlElementWrapper(name = "directDepositStatuses")
	@XmlElement(name = "directDepositStatus")
	protected List<DirectDepositStatus> directDepositStatuses;

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	/**
	 * @return list of direct deposits
	 */
	public List<DirectDepositStatus> getDirectDepositStatuses() {
		if (directDepositStatuses == null) {
			directDepositStatuses = new ArrayList<>();
		}
		return directDepositStatuses;
	}
}
