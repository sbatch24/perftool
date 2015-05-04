package com.catalinamarketing.omni.protocol.message;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Server will send this message to all clients
 * in the pool once the clients report the results of the performance run.
 * @author achavan
 *
 */
@XmlRootElement(name="shutdown")
public class ShutdownMsg {
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
