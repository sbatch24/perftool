package com.catalinamarketing.omni.controller;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.catalinamarketing.omni.PerfToolApplication;
import com.catalinamarketing.omni.config.Config;
import com.catalinamarketing.omni.config.Environment;
import com.catalinamarketing.omni.message.DataSetupActivityLog;
import com.catalinamarketing.omni.message.StatusMessage;
import com.catalinamarketing.omni.message.TestActivityMessage;
import com.catalinamarketing.omni.message.WorkerInfo;
import com.catalinamarketing.omni.pmr.setup.PmrDataOrganizer;
import com.catalinamarketing.omni.pmr.setup.PmrSetupMessage;
import com.catalinamarketing.omni.pmr.setup.pim.PimSetup;
import com.catalinamarketing.omni.protocol.message.HaltExecutionMsg;
import com.catalinamarketing.omni.server.ClientCommunicationHandler;
import com.catalinamarketing.omni.server.ClientCommunicationHandler.STATUS;
import com.catalinamarketing.omni.server.ControlServer;
import com.catalinamarketing.omni.server.ControlServer.TESTSTATUS;
import com.catalinamarketing.omni.server.DataSetupHandler;
import com.catalinamarketing.omni.server.TestPlanDispatcherThread;
import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

@RestController
@RequestMapping("/")
public class CommandController {
	final static Logger logger = LoggerFactory.getLogger(CommandController.class);
	
	private DataSetupHandler handler;
	private Lock publisherLock;
	
	public CommandController() {
		handler = new DataSetupHandler();
		publisherLock = new ReentrantLock(true);
	}

	@RequestMapping(method = RequestMethod.GET, value="/config")
	public @ResponseBody String getConfig() {
		logger.info("Got call from client for config");
		Config config = null;
		try {
			JAXBContext context = JAXBContext.newInstance(Config.class);
			Unmarshaller um = context.createUnmarshaller();
		    config = (Config) um.unmarshal(new FileReader("config.xml"));
		} catch(Exception ex) {
			
		}
		String response = "{}";
		if(config != null) {
			Gson gson = new Gson();
			response = gson.toJson(config);
		}
		return response;
	}
	
	@RequestMapping(value = "/publishPimData",
					method = RequestMethod.POST)
	public ResponseEntity<String> publishPimData(@RequestBody PimSetup message) {
		Channel channel = null;
		Connection connection = null;
		try {
			JAXBContext context = JAXBContext.newInstance(Config.class);
			Unmarshaller um = context.createUnmarshaller();
		    Config config = (Config) um.unmarshal(new FileReader("config.xml"));
		    Environment environment = config.getConfiguredEnvironment();
		    ConnectionFactory factory = new ConnectionFactory();
			factory.setUsername(environment.getQueueInfo().getUserName());
			factory.setPassword(environment.getQueueInfo().getPassword());
			factory.setPort(environment.getQueueInfo().getPort());
			factory.setHost(environment.getQueueInfo().getHostName());
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.queueDeclare(environment.getQueueInfo().getPimSetupQueue(),
					true, false, false, null);
			Map<String, Object> headers = new HashMap<String, Object>();
			headers.put("__TypeId__", "pim-setup");
			Gson gson = new Gson();
			String jsonMessage = gson.toJson(message);
			channel.basicPublish("", environment.getQueueInfo()
					.getSetupQueueName(), new AMQP.BasicProperties.Builder()
					.contentType("application/json").headers(headers)
					.build(), jsonMessage.getBytes());
		}catch(Exception ex) {
			logger.error("Problem publishing pim message. Reason " + ex.getMessage());
			return new ResponseEntity<String>("Problem publishing pim message. Reason " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			try {
				if(channel != null && connection != null) {
					channel.close();
					connection.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new ResponseEntity<String>("{\"status\":\"Pim Message published updated\"}", HttpStatus.OK);
	}
				
	
	@RequestMapping(value = "/update",
	        method = RequestMethod.POST)
	public ResponseEntity<String>  updateConfig(@RequestBody Config config) {
		try {
			JAXBContext context = JAXBContext.newInstance(Config.class);
			Marshaller um = context.createMarshaller();
		    um.marshal(config,new FileWriter(new File("config.xml")));
		    logger.info("Configuration updated");
		    
		}catch(Exception ex) {
			logger.error("Problem occured during update of configuration. Error : " + ex.getMessage());
		}
		PerfToolApplication.getControlServer().updateServerActivityLog("Configuration updated successfully at " + new Date().toString());
		return new ResponseEntity<String>("{\"status\":\"Configuration updated\"}", HttpStatus.OK);
	}
	
	/**
	 * Publish endpoint is called by the UX to submit promotion and customer data
	 * @return ActivityLog detailing what happened when publish was executed.
	 */
	@RequestMapping(method = RequestMethod.GET, value="/publish")
	public ResponseEntity<String> publishData() {
		DataSetupActivityLog activityLog = null;
		boolean locked = false;
		System.out.println("Publish got called at " + new Date().toString());
		
		try {
			JAXBContext context = JAXBContext
					.newInstance(Config.class);
			Unmarshaller um = context.createUnmarshaller();
			Config config = (Config) um.unmarshal(new FileReader(
					"config.xml"));
			
			if(publisherLock.tryLock(1, TimeUnit.SECONDS)) {
				handler.setConfig(config);
				locked = true;
				activityLog = handler.dataSetup();
			} else {
				activityLog = new DataSetupActivityLog();
				System.out.println("I am locked");
				activityLog.addException("Unable to service request at this time. Reason - Prior publish activity is still not complete.");
				return new ResponseEntity<String>(new Gson().toJson(activityLog),HttpStatus.SERVICE_UNAVAILABLE);
			}
		}catch(Exception ex) {
			if(activityLog == null) {
				activityLog = new DataSetupActivityLog();
			}
			logger.error("Problem occured during publishing data. Error : " +ex.getMessage());
			activityLog.addException("Problem occured during setting data. Error : " +ex.getMessage());	
			//return new ResponseEntity<String> (new Gson().toJson(activityLog), HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			if(locked) {
				publisherLock.unlock();
			}
		}
		if(activityLog.errorOccured()) {
			PerfToolApplication.getControlServer().updateServerActivityLog("Problem occured while publishing data to PMR and DMP [" + new Date().toString() +"]" );
			return new ResponseEntity<String>(new Gson().toJson(activityLog),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		activityLog.addActivityMessage("Data published successfully");
		PerfToolApplication.getControlServer().updateServerActivityLog("Data published successfully to PMR and DMP [" + new Date().toString() + "]");
		return new ResponseEntity<String>(new Gson().toJson(activityLog),HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/reset")
	public ResponseEntity<String> resetData() {
		DataSetupActivityLog activityLog = null;
		try {
			JAXBContext context = JAXBContext
					.newInstance(Config.class);
			Unmarshaller um = context.createUnmarshaller();
			Config config = (Config) um.unmarshal(new FileReader(
					"config.xml"));
			if(publisherLock.tryLock(1, TimeUnit.SECONDS)) {
				handler.setConfig(config);
				activityLog = handler.clearEventsFromProfile();	
			}
		}catch(Exception ex) {
			logger.error("Problem occured during resetting data. Error : " +ex.getMessage());
			if(activityLog == null) {
				activityLog = new DataSetupActivityLog();
			}
			activityLog.addException("Problem occured during resetting data. Error : " +ex.getMessage());	
			return new ResponseEntity<String> (new Gson().toJson(activityLog), HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			publisherLock.unlock();
		}
		if(activityLog.errorOccured()) {
			PerfToolApplication.getControlServer().updateServerActivityLog("Problem occured while resetting data ["+ new Date().toString()+ "]" );
			new ResponseEntity<String>(new Gson().toJson(activityLog),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		PerfToolApplication.getControlServer().updateServerActivityLog("Data reset was requested at [" + new Date().toString() +"]");
		return new ResponseEntity<String>(new Gson().toJson(activityLog),HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/status")
	public ResponseEntity<String> status() {
		StatusMessage statusMessage = new StatusMessage();
		Map<String, ClientCommunicationHandler> clientList = PerfToolApplication.getControlServer()
				.getClientCommunicationHandlerList();
		if(clientList.size() == 0) {
			logger.info("No clients connected");
			statusMessage.setStatus("No clients connected");
		} else {
			for (Map.Entry<String, ClientCommunicationHandler> entry : clientList
					.entrySet()) {
				ClientCommunicationHandler clientCommHandler = entry.getValue();
				WorkerInfo workerInfo = new WorkerInfo(clientCommHandler.getHostName(), clientCommHandler.getUserName(),
						clientCommHandler.getStatus().toString());
				statusMessage.addWorker(workerInfo);
				workerInfo.setApiExceptionList(clientCommHandler.getApiExceptionList());
				workerInfo.setApiResponseCounterList(clientCommHandler.getApiResponseCounterList());
				workerInfo.setMetricRegistryList(clientCommHandler.getMetricRegistryList());
			}
		}
		statusMessage.updateStatus(PerfToolApplication.getControlServer().getServerActivityLog());
		statusMessage.updateStatus(this.handler.getPublishStatus());
		//statusMessage.setTestGoingOn(ControlServer.isTestInProgress() );
		return new ResponseEntity<String>(new Gson().toJson(statusMessage),HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.POST, value="/checkStatus")
	public ResponseEntity<String> checkForNewClients() {
		
		return new ResponseEntity<String>(new Gson().toJson(""),HttpStatus.OK);
	} 

	@RequestMapping(method=RequestMethod.GET, value="/stop")
	public ResponseEntity<String> cancelTest() {
		HaltExecutionMsg msg = new HaltExecutionMsg();
		msg.setHalt(true);
		msg.setReason("Admin requested for shutdown of test");
		Map<String, ClientCommunicationHandler> clientList = PerfToolApplication.getControlServer()
				.getClientCommunicationHandlerList();
		if(clientList.size() > 0) {
			ClientCommunicationHandler client = null;
			for (Map.Entry<String, ClientCommunicationHandler> entry : clientList
					.entrySet()) {
				client = entry.getValue();
				client.setStatus(STATUS.TEST_EXECUTION_HALTED);
				client.writeMessage(msg);
			}
		} else {
			return new ResponseEntity<String>("{\"status\":\"No clients available \"}",HttpStatus.OK);
		}
		PerfToolApplication.getControlServer().updateServerActivityLog("Test execution requested to be stopped at " + new Date().toString());
		ControlServer.setTestInProgress(TESTSTATUS.TEST_ABORTED);
		ControlServer.cancelTestExecutionCheckTimer();
		return new ResponseEntity<String>("{\"status\":\"Abort test request sent to all workers in the pool.\"}",HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/start")
	public ResponseEntity<String> startTest() {
		TestActivityMessage testActivity = new TestActivityMessage();
		if(PerfToolApplication.getControlServer().isTestInProgress() ) {
			testActivity.setStatus(PerfToolApplication.getControlServer().getTestStatus());
		} else {
			try {
				JAXBContext context = JAXBContext
						.newInstance(Config.class);
				Unmarshaller um = context.createUnmarshaller();
				Config config = (Config) um.unmarshal(new FileReader(
						"config.xml"));
				PmrDataOrganizer pmrDataOrganizer = new PmrDataOrganizer(config,null);
				pmrDataOrganizer.initializePmrDataSetup();
				TestPlanDispatcherThread testPlanDispatcherThread = new TestPlanDispatcherThread(
						config, PerfToolApplication.getControlServer(), pmrDataOrganizer.getPmrSetupMessageList());
				new Thread(testPlanDispatcherThread).start();
				Map<String, ClientCommunicationHandler> clientList = PerfToolApplication.getControlServer()
						.getClientCommunicationHandlerList();
				if(clientList.size() > 0) {
					for (Map.Entry<String, ClientCommunicationHandler> entry : clientList
							.entrySet()) {
						testActivity.addWorker(new WorkerInfo(entry.getValue().getHostName(), entry.getValue().getUserName(), "Test requested"));
					}
					long simulationTimeInSeconds = config.getSimulationTimeInSeconds();
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.MILLISECOND,(int)(simulationTimeInSeconds * 1000));
					testActivity.setStatus("Test has been requested on " + new Date().toString() + " and will end around " +cal.getTime().toString());
					PerfToolApplication.getControlServer().setTestStatus("Test has been requested on " + new Date().toString() + " and will end around " +cal.getTime().toString());
					PerfToolApplication.getControlServer().updateServerActivityLog("Test has been requested on " + new Date().toString() + " and will end around " +cal.getTime().toString());

					ControlServer.setTestInProgress(TESTSTATUS.TEST_IN_PROGRESS);
					ControlServer.startTestExecutionCheckTimer((simulationTimeInSeconds*1000));
				}else {
					testActivity.setStatus("No clients are available for executing test plan");
				}
				
			}catch(Exception ex) {
				logger.error("Problem requesting test execution. Error : " + ex.getMessage());
				testActivity.setStatus("Problem requesting test execution. Error : "+ ex.getMessage());
				return new ResponseEntity<String>(new Gson().toJson(testActivity),HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<String>(new Gson().toJson(testActivity),HttpStatus.OK);
	}

}
