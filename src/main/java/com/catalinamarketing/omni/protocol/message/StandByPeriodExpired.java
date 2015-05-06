package com.catalinamarketing.omni.protocol.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
/**
 * This message will be sent by the server to any incoming connections that connect
 * after the standby period has expired.
 * @author achavan
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "standbyPeriodMsg")
public class StandByPeriodExpired extends Message {
	
	private String startPollDateTime;
	private String endPollDateTime;
	
	public String getStartPollDateTime() {
		return startPollDateTime;
	}


	public void setStartPollDateTime(String startPollDateTime) {
		this.startPollDateTime = startPollDateTime;
	}


	public String getEndPollDateTime() {
		return endPollDateTime;
	}


	public void setEndPollDateTime(String endPollDateTime) {
		this.endPollDateTime = endPollDateTime;
	}

	public StandByPeriodExpired() {

	}
}
