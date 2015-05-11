package com.catalinamarketing.omni.api;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "mediaEvents")
public class MediaEvents {

    @XmlElement
    protected String transactionId;

    @XmlElementWrapper(name = "customerMediaEvents")
    @XmlElement(name = "customerMediaEvent")
    protected List<CustomerMediaEvent> customerMediaEvents;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public List<CustomerMediaEvent> getCustomerMediaEvents() {
        if (customerMediaEvents == null) {
            customerMediaEvents = new ArrayList<>();
        }
        return customerMediaEvents;
    }
    
    public List<DirectDepositStatus> getMediaPrintEventForCustomer(String customerId) {
    	List<DirectDepositStatus> directDepositStatusList = new ArrayList<DirectDepositStatus>();
    	for(CustomerMediaEvent customerMediaEvent : customerMediaEvents) {
    		if(customerMediaEvent.getCustomerId().equalsIgnoreCase(customerId)) {
    			List<DirectDepositStatus> directDeposits = customerMediaEvent.getDirectDepositStatuses();
    			for(DirectDepositStatus directDeposit : directDeposits) {
    				if(directDeposit.printedSuccessfully()) {
    					directDepositStatusList.add(directDeposit);
    				}
    			}
    		}
    	}
    	return directDepositStatusList;
    }

}