package com.catalinamarketing.omni.protocol.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="haltExecution")
public class HaltExecutionMsg extends Message {
	private boolean halt;
	private String reason;

	public HaltExecutionMsg() {
		
	}
	
	public boolean isHalt() {
		return halt;
	}

	public void setHalt(boolean halt) {
		this.halt = halt;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public String printMessage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\nHalt  - " +this.halt);
		buffer.append("\nReason - "+ this.reason);
		return buffer.toString();
	}
}
