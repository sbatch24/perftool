package com.catalinamarketing.omni.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="queueInfo")
public class Queue {
	private String hostName;
	private int port;
	private String userName;
	private String password;
	private String exchange;
	private String setupQueueName;
	private String dcQueueName;
	private String stringPrintQueueName;
	private String routingKey;
	private String pimSetupQueue;
	
	public Queue(String host, int port, String userName, String password, String exchange,
			String queue, String routingKey) {
		this.hostName = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.exchange = exchange;
		this.setupQueueName = queue;
		this.routingKey = routingKey;
	}
	
	public Queue() {
		
	}
	
	
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	public String getSetupQueueName() {
		return setupQueueName;
	}
	public void setSetupQueueName(String queueName) {
		this.setupQueueName = queueName;
	}
	public String getRoutingKey() {
		return routingKey;
	}
	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}

	public String getDcQueueName() {
		return dcQueueName;
	}

	public void setDcQueueName(String dcQueueName) {
		this.dcQueueName = dcQueueName;
	}

	public String getStringPrintQueueName() {
		return stringPrintQueueName;
	}

	public void setStringPrintQueueName(String stringPrintQueueName) {
		this.stringPrintQueueName = stringPrintQueueName;
	}

	public String getPimSetupQueue() {
		return pimSetupQueue;
	}

	public void setPimSetupQueue(String pimSetupQueue) {
		this.pimSetupQueue = pimSetupQueue;
	}
}
