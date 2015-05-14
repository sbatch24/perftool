package com.catalinamarketing.omni.client;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.catalinamarketing.omni.protocol.message.HttpResponseCodeCounter;
import com.catalinamarketing.omni.protocol.message.TestExecutionResultMsg;
import com.catalinamarketing.omni.protocol.message.TestPlanMsg;
import com.catalinamarketing.omni.util.HttpResponseRepository;
import com.catalinamarketing.omni.util.MediaUsageRepository;
import com.catalinamarketing.omni.util.MessageMarshaller;
import com.google.common.collect.Multiset;

public class TestPlanExecutor implements Runnable {
	final static Logger logger = LoggerFactory.getLogger(TestPlanExecutor.class);
	private PrintWriter out;
	private final TestPlanMsg testPlan;
	private boolean finishedTestExecution;
	@SuppressWarnings("unused")
	private List<ApiExecutor> apiExecutorList;
	
	public TestPlanExecutor(TestPlanMsg msg, PrintWriter out) {
		this.testPlan = msg;
		this.out = out;
		finishedTestExecution = false;
		apiExecutorList = new ArrayList<ApiExecutor>();
	}

	/**
	 * Returns true is testPlanExecution has finished or else false.
	 * @return finishedTestExecution.
	 */
	public boolean isTestPlanExecutionComplete() {
		return finishedTestExecution;
	}
	
	/**
	 * Returns TestPlanMessage that was assigned by server.
	 * @return testPlan
	 */
	public TestPlanMsg getTestPlan() {
		return testPlan;
	}
	
	/**
	 * Kick off API threads which will execute the test plan prescribed by the server.
	 */
	public void executeTestPlan() {
		int targetingCallCountPerThread = targetingCallsPerThread();
		int targetingThreadCountPerCapThread = testPlan.getTargetingThreadCount() / testPlan.getCappingThreadCount();
		int cappingCallCountPerThread = cappingCallsPerThread();
		logger.info("One capping usage report thread for "+ targetingThreadCountPerCapThread +" targeting threads");
		int count = 1;
		String threadGroupIdentifier = null;
		MediaUsageRepository mediaUsageRepository = new MediaUsageRepository();
		HttpResponseRepository responseRepository = new HttpResponseRepository();
		CountDownLatch finishedSignal = new CountDownLatch(testPlan.getTargetingThreadCount() + testPlan.getCappingThreadCount());
		for(int i = 0; i < testPlan.getTargetingThreadCount(); i++, count++) {
			if ((i%targetingThreadCountPerCapThread) == 0) {
				threadGroupIdentifier = UUID.randomUUID().toString();
				if(!mediaUsageRepository.isMediaCounterRepositoryCreated(threadGroupIdentifier)) {
					mediaUsageRepository.createMediaRepository(threadGroupIdentifier);
					ApiExecutor api = new CappingApiExecutor(count, cappingCallCountPerThread, 
							threadGroupIdentifier, finishedSignal, mediaUsageRepository,
							responseRepository, testPlan);
					new Thread(api).start();
					apiExecutorList.add(api);
				}
			}
			ApiExecutor api = new TargetingApiExecutor(count, 
					targetingCallCountPerThread, threadGroupIdentifier,finishedSignal, mediaUsageRepository,responseRepository,
					testPlan);
			new Thread(api).start();
			apiExecutorList.add(api);
		}
		try {
			logger.info("Waiting for api threads to finish");
			finishedSignal.await();
			finishedTestExecution = true;
			logger.info("Api threads done executing");
			aggregateTestResults(responseRepository);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void aggregateTestResults(HttpResponseRepository responseRepository) {
		logger.info("Aggregating test results");
		TestExecutionResultMsg executionResultMsg = new TestExecutionResultMsg();
		Map<String,Multiset<Integer>> httpResponseRepository = responseRepository.getHttpCodeResponseRepository();
		for(Map.Entry<String, Multiset<Integer>> entry : httpResponseRepository.entrySet()) {
			Multiset<Integer> statuses = entry.getValue();
			for(Integer status : statuses.elementSet()) {
				HttpResponseCodeCounter counter = new HttpResponseCodeCounter();
				counter.setResponseCode(status);
				counter.setCount(statuses.count(status));
				executionResultMsg.addCounter(entry.getKey(), counter);
			}
		}
		
		Map<String, Multiset<String>> exceptionList = responseRepository.getExceptionMessageList();
		for(Map.Entry<String, Multiset<String>> entry : exceptionList.entrySet()) {
			Multiset<String> exceptions = entry.getValue();
			for(String exceptionMessage : exceptions.elementSet()) {
				executionResultMsg.addException(entry.getKey(), exceptionMessage, exceptions.count(exceptionMessage));
			}
		}
		out.println(MessageMarshaller.marshalMessage(executionResultMsg));
		out.flush();
	}

	/**
	 * Returns the number of targeting calls each thread must make. 
	 * If the configuration is incorrect an error is logged and default number of calls count is returned.
	 * @return targetingCallsPerThread
	 */
	public int targetingCallsPerThread() {
		int count = this.testPlan.getTargetingCallCount() / testPlan.getTargetingThreadCount();
		if(count <= 0) {
			logger.error("");
			count = testPlan.getTargetingCallCount();
		}
		return count;
	}
	
	/**
	 * Returns the number of capping calls each thread must make. 
	 * If the configuration is incorrect an error is logged and default number of calls count is returned.
	 * @return cappingCallsPerThread
	 */
	public int cappingCallsPerThread() {
		int count = testPlan.getCappingCallCount() / testPlan.getCappingThreadCount();
		if(count <= 0) {
			logger.error("");
			count = testPlan.getCappingCallCount();
		}
		return count;
	}
	
	@Override
	public void run() {
		executeTestPlan();
	}
	
	public String testPlanStatus() {
		if(finishedTestExecution) {
			return "Finished Execution";
		}else {
			return "Executing test plan";
		}
	}
	
	/**
	 * Set the halt flag for all api threads currently
	 * executing so they will not continue to run further.
	 */
	public void haltApiThread() {
		for (ApiExecutor apiExecutor : apiExecutorList) {
			apiExecutor.halt();
		}
	}
}
