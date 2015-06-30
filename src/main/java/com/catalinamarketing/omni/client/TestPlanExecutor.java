package com.catalinamarketing.omni.client;

import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import com.codahale.metrics.Timer;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.catalinamarketing.omni.protocol.message.ApiMetricRegistry;
import com.catalinamarketing.omni.protocol.message.HttpResponseCodeCounter;
import com.catalinamarketing.omni.protocol.message.Metric;
import com.catalinamarketing.omni.protocol.message.StatusMsg;
import com.catalinamarketing.omni.protocol.message.TestExecutionResultMsg;
import com.catalinamarketing.omni.protocol.message.TestPlanMsg;
import com.catalinamarketing.omni.util.HttpResponseRepository;
import com.catalinamarketing.omni.util.MediaUsageRepository;
import com.catalinamarketing.omni.util.MessageMarshaller;
import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.google.common.collect.Multiset;

public class TestPlanExecutor implements Runnable {
	final static Logger logger = LoggerFactory.getLogger(TestPlanExecutor.class);
	private PrintWriter out;
	private final TestPlanMsg testPlan;
	private boolean finishedTestExecution;
	@SuppressWarnings("unused")
	private List<ApiExecutor> apiExecutorList;
	private boolean respondWithHaltedExecutionMessage;
	
	public TestPlanExecutor(TestPlanMsg msg, PrintWriter out) {
		this.testPlan = msg;
		this.out = out;
		finishedTestExecution = false;
		respondWithHaltedExecutionMessage = false;
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
			/*final Graphite graphite = new Graphite(new InetSocketAddress("localhost", 8080));
			final GraphiteReporter reporters = GraphiteReporter.forRegistry(apiExecutorList.get(0).metrics)
			                                                  .prefixedWith("web1.example.com")
			                                                  .convertRatesTo(TimeUnit.SECONDS)
			                                                  .convertDurationsTo(TimeUnit.MILLISECONDS)
			                                                  .filter(MetricFilter.ALL)
			                                                  .build(graphite);
			reporters.start(1, TimeUnit.MINUTES);*/
			finishedSignal.await();
			final ConsoleReporter reporter = ConsoleReporter.forRegistry(apiExecutorList.get(0).metrics)
	                .convertRatesTo(TimeUnit.MILLISECONDS)
	                .convertDurationsTo(TimeUnit.SECONDS)
	                .build();
			reporter.forRegistry(apiExecutorList.get(0).metrics).outputTo(System.out);
			reporter.report();
			
			
			finishedTestExecution = true;
			if(isRespondWithHaltedExecutionMessage()) {
				StatusMsg statusMsg = new StatusMsg();
				statusMsg.setTestPlanVersion(getTestPlan().getTestPlanVersion());
				statusMsg.setExecutionStatus("Execution of test plan - "+ getTestPlan().getTestPlanVersion() + " halted");
				out.println(MessageMarshaller.marshalMessage(statusMsg));
				out.flush();
			} else {
				logger.info("Api threads done executing");
				aggregateTestResults(responseRepository);	
			}
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
		
		for(Entry<String, Timer> entry :  ApiExecutor.metrics.getTimers().entrySet()) {
			String apiName = entry.getKey();
			ApiMetricRegistry apiMetricRegistry = new ApiMetricRegistry(apiName);
			populateApiMetricRegistry(apiMetricRegistry, entry.getValue());
			executionResultMsg.addMetriRegistry(apiMetricRegistry);
		}
		out.println(MessageMarshaller.marshalMessage(executionResultMsg));
		out.flush();
	}
	
	private void populateApiMetricRegistry(ApiMetricRegistry apiMetricRegistry, Timer timer) {
		Metric metric = new Metric("75thPercentile",timer.getSnapshot().get75thPercentile());
		apiMetricRegistry.addMetric(metric);
		metric = new Metric("95thPercentile", timer.getSnapshot().get95thPercentile());
		apiMetricRegistry.addMetric(metric);
		
		metric = new Metric("99thPercentile", timer.getSnapshot().get999thPercentile());
		apiMetricRegistry.addMetric(metric);
		
		metric = new Metric("Median", timer.getSnapshot().getMedian());
		apiMetricRegistry.addMetric(metric);
		
		metric = new Metric("Avg", timer.getSnapshot().getMean());
		apiMetricRegistry.addMetric(metric);
		
		metric = new Metric("Max", timer.getSnapshot().getMax());
		apiMetricRegistry.addMetric(metric);
		
		metric = new Metric("Min", timer.getSnapshot().getMin());
		apiMetricRegistry.addMetric(metric);
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
		setRespondWithHaltedExecutionMessage(true);
	}

	public boolean isRespondWithHaltedExecutionMessage() {
		return respondWithHaltedExecutionMessage;
	}

	public void setRespondWithHaltedExecutionMessage(
			boolean respondWithHaltedExecutionMessage) {
		this.respondWithHaltedExecutionMessage = respondWithHaltedExecutionMessage;
	}
}
