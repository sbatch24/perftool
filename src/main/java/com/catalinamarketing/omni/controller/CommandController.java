package com.catalinamarketing.omni.controller;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Date;
import java.util.Map;

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
import com.catalinamarketing.omni.message.DataSetupActivityLog;
import com.catalinamarketing.omni.message.StatusMessage;
import com.catalinamarketing.omni.message.TestActivityMessage;
import com.catalinamarketing.omni.message.WorkerInfo;
import com.catalinamarketing.omni.pmr.setup.PmrDataOrganizer;
import com.catalinamarketing.omni.protocol.message.HaltExecutionMsg;
import com.catalinamarketing.omni.server.ClientCommunicationHandler;
import com.catalinamarketing.omni.server.ClientCommunicationHandler.STATUS;
import com.catalinamarketing.omni.server.DataSetupHandler;
import com.catalinamarketing.omni.server.TestPlanDispatcherThread;
import com.google.gson.Gson;

@RestController
@RequestMapping("/")
public class CommandController {
	final static Logger logger = LoggerFactory.getLogger(CommandController.class);

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
	
	@RequestMapping(value = "/update",
	        method = RequestMethod.POST)
	public ResponseEntity<String>  updateConfig(@RequestBody Config config) {
		try {
			JAXBContext context = JAXBContext.newInstance(Config.class);
			Marshaller um = context.createMarshaller();
		    um.marshal(config,new FileWriter(new File("configt.xml")));
		    logger.info("Configuration updated");
		}catch(Exception ex) {
			logger.error("Problem occured during update of configuration. Error : " + ex.getMessage());
		}
		return new ResponseEntity<String>("{\"status\":\"Configuration updated\"}", HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/publish")
	public ResponseEntity<String> publishData() {
		DataSetupActivityLog activityLog = new DataSetupActivityLog();
		try {
			JAXBContext context = JAXBContext
					.newInstance(Config.class);
			Unmarshaller um = context.createUnmarshaller();
			Config config = (Config) um.unmarshal(new FileReader(
					"config.xml"));
			DataSetupHandler handler = new DataSetupHandler(config, true);
			activityLog = handler.dataSetup();
		}catch(Exception ex) {
			logger.error("Problem occured during publishing data. Error : " +ex.getMessage());
			activityLog.addException("Problem occured during setting data. Error : " +ex.getMessage());	
			return new ResponseEntity<String> (new Gson().toJson(activityLog), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(activityLog.errorOccured()) {
			new ResponseEntity<String>(new Gson().toJson(activityLog),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		activityLog.addActivityMessage("Data published data successful");
		return new ResponseEntity<String>(new Gson().toJson(activityLog),HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/reset")
	public ResponseEntity<String> resetData() {
		DataSetupActivityLog activityLog = new DataSetupActivityLog();
		try {
			JAXBContext context = JAXBContext
					.newInstance(Config.class);
			Unmarshaller um = context.createUnmarshaller();
			Config config = (Config) um.unmarshal(new FileReader(
					"config.xml"));
			DataSetupHandler handler = new DataSetupHandler(config, false);
			activityLog = handler.clearEventsFromProfile();
		}catch(Exception ex) {
			logger.error("Problem occured during resetting data. Error : " +ex.getMessage());
			activityLog.addException("Problem occured during resetting data. Error : " +ex.getMessage());	
			return new ResponseEntity<String> (new Gson().toJson(activityLog), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(activityLog.errorOccured()) {
			new ResponseEntity<String>(new Gson().toJson(activityLog),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		activityLog.addActivityMessage("Resetting data successful");
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
				// Only update status for newly connected clients.
				if(clientCommHandler.getStatus() == ClientCommunicationHandler.STATUS.CONNECTED) {
					statusMessage.addWorker(new WorkerInfo(clientCommHandler.getHostName(), clientCommHandler.getUserName(),
							clientCommHandler.getStatus().toString()));
					clientCommHandler.setStatus(STATUS.INITIAL_STATUS_SENT);
				} else if(clientCommHandler.getStatus() == ClientCommunicationHandler.STATUS.BUSY_EXECUTING_TEST) {
					statusMessage.addWorker(new WorkerInfo(clientCommHandler.getHostName(), clientCommHandler.getUserName(),
							clientCommHandler.getStatus().toString()));
				}
			}
		}
		statusMessage.updateStatus(PerfToolApplication.getControlServer().getServerStatus());
		return new ResponseEntity<String>(new Gson().toJson(statusMessage),HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/checkStatus")
	public ResponseEntity<String> checkForNewClients() {
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
				// Only update status for newly connected clients.
				if(clientCommHandler.getStatus() == ClientCommunicationHandler.STATUS.CONNECTED) {
					statusMessage.addWorker(new WorkerInfo(clientCommHandler.getHostName(), clientCommHandler.getUserName(),
							clientCommHandler.getStatus().toString()));
					clientCommHandler.setStatus(STATUS.INITIAL_STATUS_SENT);
				} else if(clientCommHandler.getStatus() == ClientCommunicationHandler.STATUS.DISCONNECTED) {
					statusMessage.addWorker(new WorkerInfo(clientCommHandler.getHostName(), clientCommHandler.getUserName(),
							clientCommHandler.getStatus().toString()));
					clientCommHandler.setStatus(STATUS.DISCONNECTED_STATUS_SENT);
					PerfToolApplication.getControlServer().removeClientCommunicationHandler(clientCommHandler.getClientIdentifier());
				}
			}
		}
		statusMessage.updateStatus(PerfToolApplication.getControlServer().getServerStatus());
		return new ResponseEntity<String>(new Gson().toJson(statusMessage),HttpStatus.OK);
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
				client.writeMessage(msg);
			}
		} else {
			return new ResponseEntity<String>("{\"status\":\"No clients available \"}",HttpStatus.OK);
		}
		PerfToolApplication.getControlServer().updateStatus("Test execution requested to be stopped at " + new Date().toString());
		return new ResponseEntity<String>("{\"status\":\"Abort test request sent to all workers in the pool.\"}",HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/start")
	public ResponseEntity<String> startTest() {
		TestActivityMessage testActivity = new TestActivityMessage();
		try {
			JAXBContext context = JAXBContext
					.newInstance(Config.class);
			Unmarshaller um = context.createUnmarshaller();
			Config config = (Config) um.unmarshal(new FileReader(
					"config.xml"));
			PmrDataOrganizer pmrDataOrganizer = new PmrDataOrganizer(config);
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
				testActivity.setStatus("Test has been requested");	
			}else {
				testActivity.setStatus("No clients are available for executing test plan");
			}
			
		}catch(Exception ex) {
			logger.error("Problem requesting test execution. Error : " + ex.getMessage());
			testActivity.setStatus("Problem requesting test execution. Error : "+ ex.getMessage());
			return new ResponseEntity<String>(new Gson().toJson(testActivity),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		PerfToolApplication.getControlServer().updateStatus("Test execution requested at " + new Date().toString());
		return new ResponseEntity<String>(new Gson().toJson(testActivity),HttpStatus.OK);
	}

}
