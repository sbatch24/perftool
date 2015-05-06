package com.catalinamarketing.omni.protocol.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Server will send this message to all clients
 * in the pool once the clients report the results of the performance run.
 * @author achavan
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="shutdown")
public class ShutdownMsg extends Message {
	private String message;
	
	public ShutdownMsg() {
		
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}
