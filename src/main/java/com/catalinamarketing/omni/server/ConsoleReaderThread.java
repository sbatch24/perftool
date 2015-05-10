package com.catalinamarketing.omni.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.catalinamarketing.omni.config.Config;

/**
 * Class reads commands from the console and executes them.
 * @author achavan
 *
 */
public class ConsoleReaderThread implements Runnable {
	
	final static Logger logger = LoggerFactory.getLogger(ConsoleReaderThread.class);

	private Config config;
	private final ControlServer controlServer;
	
	public ConsoleReaderThread(ControlServer server) {
		this.controlServer = server;
	}

	@Override
	public void run() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		for (;;) {
			String line;
			try {
				System.out.println("\nEnter commands :");
				line = in.readLine();
				if(line.equalsIgnoreCase("exit")) {
					// TODO exit server.
				}else if(line.equalsIgnoreCase("reload")) {
					try {
						JAXBContext context = JAXBContext.newInstance(Config.class);
						Unmarshaller um = context.createUnmarshaller();
					    config = (Config) um.unmarshal(new FileReader("config.xml"));
					    logger.info("Configuration reloaded. You can kick off test anytime now.");	
					}catch(Exception ex) {
						logger.error("Problem reloading configuration");
					}
				}else if(line.equalsIgnoreCase("start")) {
					DataSetupHandler handler = new DataSetupHandler(config);
					handler.dataSetup();
					TestPlanDispatcherThread testPlanDispatcherThread = new TestPlanDispatcherThread(config, controlServer); 
					new Thread(testPlanDispatcherThread).start();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
