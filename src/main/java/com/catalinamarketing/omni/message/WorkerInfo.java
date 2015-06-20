package com.catalinamarketing.omni.message;

public class WorkerInfo {
	
	private String hostName;
	private String userName;
	private String status;
	
	public WorkerInfo(String hostName, String userName, String status) {
		this.hostName = hostName;
		this.userName = userName;
		this.status = status;
	}
	
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	

}
