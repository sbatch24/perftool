package com.catalinamarketing.omni.message;

import java.util.ArrayList;
import java.util.List;

public class StatusMessage {
	private List<WorkerInfo> workerList;
	private List<String> status;
	
	public StatusMessage() {
		this.workerList = new ArrayList<WorkerInfo>();
		this.status = new ArrayList<String>();
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
	
}
