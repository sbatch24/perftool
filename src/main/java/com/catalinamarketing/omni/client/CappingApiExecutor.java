package com.catalinamarketing.omni.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.catalinamarketing.omni.protocol.message.TestPlanMsg;
import com.catalinamarketing.omni.util.HttpResponseRepository;
import com.catalinamarketing.omni.util.MediaUsageRepository;
import com.google.gson.Gson;


public class CappingApiExecutor  extends ApiExecutor {
	
	final static Logger logger = LoggerFactory.getLogger(CappingApiExecutor.class);
	private final int reportEventDelay;
	private final String threadGroupIdentifier;
	private MediaUsageRepository mediaUsageRepository;
	private CountDownLatch finishedSignal;
	private final int counter;
	private TestPlanMsg testPlan;
	
	public CappingApiExecutor(int threadIndex ,int callCount, 
			String threadGroupIdentifier, CountDownLatch finishedSignal, 
			MediaUsageRepository mediaUsageRepository, 
			HttpResponseRepository responseRepository, TestPlanMsg testPlan) {
		super(threadIndex, responseRepository);
		this.reportEventDelay =  testPlan.getCapReportFrequency();
		this.threadGroupIdentifier = threadGroupIdentifier;
		this.mediaUsageRepository = mediaUsageRepository;
		this.finishedSignal = finishedSignal;
		this.counter = callCount;
		this.testPlan = testPlan;
	}
	
	@Override
	public void run() {
		int count = 0;
		try {
			while(count < counter && !halted()) {
				try {
					Gson gson = new Gson();
					Thread.sleep(reportEventDelay*1000);
					List<UsageReport> usageReportList = new ArrayList<UsageReport>();
					Map<String, Integer>channelMediaCapList = mediaUsageRepository.getMediaCounter(threadGroupIdentifier);
					for (Map.Entry<String, Integer> entry : channelMediaCapList.entrySet()) {
					    String channelMediaId = entry.getKey();
					    Integer usedCounter = entry.getValue();
					    UsageReport usageReport = new UsageReport();
					    usageReport.setChannelMediaID(channelMediaId);
					    usageReport.setUsed(usedCounter);
					    usageReportList.add(usageReport);
					    
					}
					Client client = ClientBuilder.newClient();
					Response response = client.target(String.format(this.testPlan.getEventsApiUrl(), testPlan.getRetailerId()))
		            .queryParam("channel", "web")
		            .request()
		            .accept(MediaType.APPLICATION_JSON)
		            .header("Content-Type", MediaType.APPLICATION_JSON)
		            .put(Entity.entity(gson.toJson(""), MediaType.APPLICATION_JSON));
					incrementResponseCounter("ReportUsage", Status.fromStatusCode(response.getStatus()));
					if(response.getStatusInfo().getStatusCode() == Response.Status.OK.getStatusCode()) {
						mediaUsageRepository.resetMediaCounters(threadGroupIdentifier);
					}
					count++;
				}catch(Exception ex) {
					logger.error("Problem occured in Capping API Executor thread. Error: "+ ex.getMessage());
					ex.printStackTrace();
				}
			}
		}catch(Exception ex) {
			logger.error("Problem occured in Capping API Executor thread. Error: "+ ex.getMessage());
			ex.printStackTrace();
		}finally {
			logger.info("Capping API thread#"+this.threadIndex() +" finish execution. ThreadGroup Identifier "+ threadGroupIdentifier + " countdown " + finishedSignal.getCount());
			finishedSignal.countDown();	
		}
	}

	public int getReportEventDelay() {
		return reportEventDelay;
	}
}
