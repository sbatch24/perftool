package com.catalinamarketing.omni.client;

import com.catalinamarketing.omni.protocol.message.TestPlanMsg;

public class TestPlanExecutor {

	private final TestPlanMsg testPlan;
	
	public TestPlanExecutor(TestPlanMsg msg) {
		this.testPlan = msg;
	}

	public TestPlanMsg getTestPlan() {
		return testPlan;
	}
	
	public void executeTestPlan() {
		// TODO kick start test plan execution.
		// 1. Spawn threads and initialize data structures.
		
	}
	
}
