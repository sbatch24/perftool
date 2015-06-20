package com.catalinamarketing.omni.message;

import java.util.ArrayList;
import java.util.List;

public class DataSetupActivityLog {
	private List<String> activityLogList;
	
	public DataSetupActivityLog() {
		activityLogList = new ArrayList<String>();
	}
	
	public void addException(String exception) {
		activityLogList.add(exception);
	}
	
	public boolean errorOccured() {
		return activityLogList.size() > 0;
	}
	
	public void addActivityMessage(String message) {
		activityLogList.add(message);
	}
}
