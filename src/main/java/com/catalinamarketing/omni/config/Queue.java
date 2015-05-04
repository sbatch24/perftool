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
	private String queueName;
	private String routingKey;
	
	public Queue(String host, int port, String userName, String password, String exchange,
			String queue, String routingKey) {
		this.hostName = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.exchange = exchange;
		this.queueName = queue;
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
	public String getQueueName() {
		return queueName;
	}
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	public String getRoutingKey() {
		return routingKey;
	}
	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}
}
