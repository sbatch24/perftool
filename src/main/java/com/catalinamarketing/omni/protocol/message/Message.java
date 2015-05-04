package com.catalinamarketing.omni.protocol.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Message {
	
	@XmlElement(name="userName")
	protected String userName;
	
	public Message() {
		this.userName = System.getProperty("user.name");
	}
	
	public String getUserName() {
		return this.userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
