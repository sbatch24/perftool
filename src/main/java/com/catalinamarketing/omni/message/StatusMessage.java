package com.catalinamarketing.omni.message;

import java.util.ArrayList;
import java.util.List;

public class StatusMessage {
	private List<WorkerInfo> workerList;
	private List<String> status;
	private boolean testGoingOn = false;
	
	public StatusMessage() {
		this.workerList = new ArrayList<WorkerInfo>();
		this.status = new ArrayList<String>();
		setTestGoingOn(false);
	}
	
	public void addWorker(WorkerInfo worker) {
		workerList.add(worker);
	}
	
	public List<WorkerInfo> getWorkerList() {
		return workerList;
	}

	public void setWorkerList(List<WorkerInfo> workerList) {
		this.workerList = workerList;
	}

	public List<String> getStatus() {
		return status;
	}

	public void setStatus(String msg) {
		this.status.add(msg);
	}
	
	public void updateStatus(List<String> statusList) {
		this.status.addAll(statusList);
	}

	public boolean isTestGoingOn() {
		return testGoingOn;
	}

	public void setTestGoingOn(boolean testGoingOn) {
		this.testGoingOn = testGoingOn;
	}
	
	public WorkerInfo getWorkerInfo(String userName, String hostName) {
		for(WorkerInfo workerInfo : workerList) {
			if(workerInfo.getHostName().equalsIgnoreCase(userName) && 
					workerInfo.getUserName().equalsIgnoreCase(hostName)) {
				return workerInfo;
			}
		}
		return null;
	}
	
}
