package com.catalinamarketing.omni.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.catalinamarketing.omni.config.Config;
import com.catalinamarketing.omni.pmr.setup.PmrDataOrganizer;
import com.catalinamarketing.omni.protocol.message.HaltExecutionMsg;
import com.catalinamarketing.omni.protocol.message.StatusMsg;

/**
 * Class reads commands from the console and executes them.
 * 
 * @author achavan
 *
 */
public class ConsoleReaderThread implements Runnable {

	final static Logger logger = LoggerFactory
			.getLogger(ConsoleReaderThread.class);

	private Config config;
	private final ControlServer controlServer;

	private PmrDataOrganizer pmrDataOrganizer;

	public ConsoleReaderThread(ControlServer server) {
		this.controlServer = server;
		pmrDataOrganizer = null;
	}

	@Override
	public void run() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		for (;;) {
			String line;
			try {
				System.out.println("\nEnter commands :");
				line = in.readLine();
				if (line.equalsIgnoreCase("exit")) {
					controlServer.shutdown();
					break;
				} else if (line.equalsIgnoreCase("load")) {
					try {
						JAXBContext context = JAXBContext
								.newInstance(Config.class);
						Unmarshaller um = context.createUnmarshaller();
						config = (Config) um.unmarshal(new FileReader(
								"config.xml"));
						logger.info("Configuration reloaded. You can kick off test anytime now.");
						DataSetupHandler handler = new DataSetupHandler(config, false);
						handler.dataSetup();
						pmrDataOrganizer = handler.getPmrDataOrganizer();
					} catch (Exception ex) {
						logger.error("Problem reloading configuration");
					}
				} else if(line.equalsIgnoreCase("publish")) {
					DataSetupHandler handler = new DataSetupHandler(config, true);
					handler.dataSetup();
					pmrDataOrganizer = handler.getPmrDataOrganizer();
				} else if (line.equalsIgnoreCase("start")) {
					if(pmrDataOrganizer != null) {
						TestPlanDispatcherThread testPlanDispatcherThread = new TestPlanDispatcherThread(
								config, controlServer, pmrDataOrganizer);
						new Thread(testPlanDispatcherThread).start();	
					} 
					
				} else if (line.equalsIgnoreCase("status")) {
					Map<String, ClientCommunicationHandler> clientList = controlServer
							.getClientCommunicationHandlerList();
					if(clientList.size() == 0) {
						logger.info("No clients connected");
					} else {
						StatusMsg statusMessage = new StatusMsg();
						for (Map.Entry<String, ClientCommunicationHandler> entry : clientList
								.entrySet()) {
							logger.info("Requestion status for client hostname : "
									+ entry.getValue().getHostName() + " client Id : " + entry.getValue().getClientIdentifier()
									+ " userName : " + entry.getValue().getUserName());
							entry.getValue().writeMessage(statusMessage);
						}
					}
				} else if(line.contains("halt")) {
					String[] tokens = line.split("\\s+");
					HaltExecutionMsg msg = new HaltExecutionMsg();
					msg.setHalt(true);
					if(tokens.length >=1 ) {
						StringBuffer bf = new StringBuffer();
						for(int i=1; i < tokens.length; i++) {
							bf.append(tokens[i] +" ");
						}
						msg.setReason(bf.toString());	
					}
					Map<String, ClientCommunicationHandler> clientList = controlServer
							.getClientCommunicationHandlerList();
					for (Map.Entry<String, ClientCommunicationHandler> entry : clientList
							.entrySet()) {
						entry.getValue().writeMessage(msg);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
