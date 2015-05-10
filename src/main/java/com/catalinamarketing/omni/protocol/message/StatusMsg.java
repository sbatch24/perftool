package com.catalinamarketing.omni.protocol.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="status")
public class StatusMsg extends Message {

	private String testPlanVersion;
	private String executionStatus;
	
	public StatusMsg() {
		
	}
	
	public String getTestPlanVersion() {
		return testPlanVersion;
	}
	public void setTestPlanVersion(String testPlanVersion) {
		this.testPlanVersion = testPlanVersion;
	}
	public String getExecutionStatus() {
		return executionStatus;
	}
	public void setExecutionStatus(String executionStatus) {
		this.executionStatus = executionStatus;
	}
	
	public String printMessage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\nStatus - "+this.executionStatus);
		buffer.append("\nTest plan version - "+ this.testPlanVersion);
		return buffer.toString();
	}

}
