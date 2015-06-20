package com.catalinamarketing.omni.message;

import java.util.ArrayList;
import java.util.List;

public class TestActivityMessage {
	private String status;
	private List<WorkerInfo> workerList;
	
	public TestActivityMessage() {
		this.setWorkerList(new ArrayList<WorkerInfo>());
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<WorkerInfo> getWorkerList() {
		return workerList;
	}

	public void setWorkerList(List<WorkerInfo> workerList) {
		this.workerList = workerList;
	}
	
	public void addWorker(WorkerInfo worker) {
		workerList.add(worker);
	}
}
