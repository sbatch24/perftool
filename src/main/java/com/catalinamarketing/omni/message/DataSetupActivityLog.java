package com.catalinamarketing.omni.message;

import java.util.ArrayList;
import java.util.List;

public class DataSetupActivityLog {
	private List<String> activityLogList;
	private List<String> errorLogList;
	
	public DataSetupActivityLog() {
		activityLogList = new ArrayList<String>();
		errorLogList = new ArrayList<String>();
	}
	
	
	public void addException(String exception) {
		errorLogList.add(exception);
	}
	
	public boolean errorOccured() {
		return errorLogList.size() > 0;
	}
	
	public void addActivityMessage(String message) {
		activityLogList.add(message);
	}
	
	public void clearActivityLog() {
		activityLogList.clear();
		errorLogList.clear();
	}
	
	public List<String> getActivityLog() {
		return activityLogList;
	}

	public List<String> getErrorLog() {
		return errorLogList;
	}
}
