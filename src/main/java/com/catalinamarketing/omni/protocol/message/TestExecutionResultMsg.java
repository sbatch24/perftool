package com.catalinamarketing.omni.protocol.message;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="testExecutionResult")
public class TestExecutionResultMsg extends Message {
	
	private List<ApiHttpResponseCounter> apiResponseCounter;
	
	@Override
	public String printMessage() {
		return null;
	}


}
