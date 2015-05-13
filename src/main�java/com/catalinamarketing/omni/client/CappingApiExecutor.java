package com.catalinamarketing.omni.client;

import java.net.UnknownHostException;
import java.util.List;
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

/**
 * Class responsible for exercising capping usage api.
 * @author achavan
 *
 */
public class CappingApiExecutor  extends ApiExecutor {
	
	final static Logger logger = LoggerFactory.getLogger(CappingApiExecutor.class);
	private final int capReportFrequency;
	private MediaUsageRepository mediaUsageRepository;
	private final int counter;
	private TestPlanMsg testPlan;
	
	public CappingApiExecutor(int threadIndex ,int callCount, 
			String threadGroupIdentifier, CountDownLatch finishedSignal, 
			MediaUsageRepository mediaUsageRepository, 
			HttpResponseRepository responseRepository, TestPlanMsg testPlan) {
		super(threadIndex,threadGroupIdentifier, responseRepository, finishedSignal);
		this.capReportFrequency =  testPlan.getCapReportFrequency();
		this.mediaUsageRepository = mediaUsageRepository;
		this.counter = callCount;
		this.testPlan = testPlan;
	}
	
	@Override
	public void run() {
		int count = 0;
		Client client = ClientBuilder.newClient();
		while(count < counter && !halted()) {
			try {
				Gson gson = new Gson();
				Thread.sleep(capReportFrequency*1000);
				List<UsageReport> usageReportList = mediaUsageRepository.prepareMediaUsageReportList(threadGroupIdentifier);
				Response response = client.target(String.format(this.testPlan.getCappingUsageApiUrl(), testPlan.getRetailerId()))
	            .request()
	            .accept(MediaType.APPLICATION_JSON)
	            .header("Content-Type", MediaType.APPLICATION_JSON)
	            .put(Entity.entity(gson.toJson(usageReportList), MediaType.APPLICATION_JSON));
				incrementResponseCounter("ReportUsage", Status.fromStatusCode(response.getStatus()));
				if(response.getStatusInfo().getStatusCode() == Response.Status.OK.getStatusCode()|| 
						response.getStatusInfo().getStatusCode() == Response.Status.ACCEPTED.getStatusCode()) {
					mediaUsageRepository.resetMediaCounters(threadGroupIdentifier);
				}
			}catch(Exception ex) {
				logger.error("Problem occured in Capping API Executor thread. Error: "+ ex.getMessage());
				haltedOnException();
			}finally{
				count++;
			}
		}
		if(count >= counter) {
			completedExecution();
		}
		logger.info(runStatus());
		finishedRun();
		client.close();
	}

	public int getCapReportFrequency() {
		return capReportFrequency;
	}
	
	@Override
	public String apiName() {
		return "CappingApi";
	}
}
