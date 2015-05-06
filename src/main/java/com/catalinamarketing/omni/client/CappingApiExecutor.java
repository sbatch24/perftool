package com.catalinamarketing.omni.client;


public class CappingApiExecutor  extends ApiExecutor {
	private final int reportEventDelay;
	
	public CappingApiExecutor(int reportEventDelay) {
		this.reportEventDelay =  reportEventDelay;
	}
	
	@Override
	public void run() {
		
	}

	public int getReportEventDelay() {
		return reportEventDelay;
	}
}
