package com.catalinamarketing.omni.protocol.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="handshake")
public class HandShakeMsg extends Message {
	
	private String initializationMessage;
	
	public HandShakeMsg() {
		
	}

	public String getInitializationMessage() {
		return initializationMessage;
	}

	public void setInitializationMessage(String initializationMessage) {
		this.initializationMessage = initializationMessage;
	}

	@Override
	public String printMessage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\nHostName  - " +this.userName);
		buffer.append("\nMessage - "+ this.initializationMessage);
		return buffer.toString();
	}
	
}
