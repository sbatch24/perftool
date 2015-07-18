package com.catalinamarketing.omni.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.catalinamarketing.omni.config.Config;

/**
 * Control server is a class that manages setup for the performance tool and
 * assigns work to different nodes in the network.
 * 
 * <ul>
 * <li>Configuration for functioning of the control server is read from a
 * configuration file</li>
 * </ul>
 * 
 * @author achavan
 * @version 1.0
 */
public class ControlServer {

	final static Logger logger = LoggerFactory.getLogger(ControlServer.class);
	private Map<String,ClientCommunicationHandler> clientCommunicationHandlerList;
	private ServerSocket serverSocket;
	private CircularFifoQueue<String> activityLog;
	private static TESTSTATUS testInProgress;
	private String testStatus;
	private static Timer timer;
	
	public enum TESTSTATUS {
		NOT_STARTED(0),
		TEST_IN_PROGRESS(1),
		TEST_ABORTED(2),
		TEST_FINISHED(3);
		
		private int status;
		
		TESTSTATUS(int s) {
			this.setStatus(s);
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}
	}
	
	
	public ControlServer(Config configuration) {
		this.clientCommunicationHandlerList = new HashMap<String,ClientCommunicationHandler>();
		activityLog = new CircularFifoQueue<String>(50);
		testInProgress = TESTSTATUS.NOT_STARTED;
		activityLog.add("Server initialized at " + new Date().toString());
	}
	
	public Map<String, ClientCommunicationHandler> getClientCommunicationHandlerList() {
		return clientCommunicationHandlerList;
	}
	
	/**
	 * Remove ClientCommunicationHandler from the list.
	 * @param identifier
	 */
	public void removeClientCommunicationHandler(String identifier) {
		logger.warn("Client " + identifier + " disconnected" );
		clientCommunicationHandlerList.remove(identifier);
	}
	
	/**
	 * Returns the number of available machines to execute the test plan for this server.
	 * @return int
	 */
	public int availableExecutorCount() {
		return this.clientCommunicationHandlerList.size();
	}
	
	/**
	 * Shutdown socket and exit
	 */
	public void shutdown() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void runServer(int portNumber) {
		try {
			serverSocket = new ServerSocket(portNumber);
			logger.info("Listening on port "+ portNumber);
	        try {
	            while (true) {
	                Socket socket = serverSocket.accept();
	              	logger.info("Got connection request from " + socket.getRemoteSocketAddress());
                	ClientCommunicationHandler handler = new ClientCommunicationHandler(socket, this);
		            clientCommunicationHandlerList.put(handler.getClientIdentifier(),handler);
		            new Thread(handler).start();	
	            }
	        }
	        finally {
	            shutdown();
	        }
		}catch(Exception ex) {
			logger.error("Problem occured in server. Error: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public List<String> getServerActivityLog() {
		return Arrays.asList((String[]) activityLog.toArray(new String[activityLog.size()]));
	}

	public void updateServerActivityLog(String serverStatus) {
		this.activityLog.add(serverStatus);
	}

	public static boolean isTestInProgress() {
		if(testInProgress == TESTSTATUS.NOT_STARTED || testInProgress == TESTSTATUS.TEST_FINISHED ||
				testInProgress == TESTSTATUS.TEST_ABORTED) {
			return false;
		}  
		return true;
	}

	public static void setTestInProgress(TESTSTATUS val) {
		testInProgress = val;
	}

	public String getTestStatus() {
		return testStatus;
	}

	public void setTestStatus(String testStatus) {
		this.testStatus = testStatus;
	}
	
	
	public static void cancelTestExecutionCheckTimer() {
		timer.cancel();
	}
	/**
	 * This timer is started after a request to kick off test execution is requested. The timer will be set to expire
	 * when the tests is scheduled to end.
	 * @param delay
	 */
	public static void startTestExecutionCheckTimer(long delay) {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				setTestInProgress(TESTSTATUS.TEST_FINISHED);
			}
		}, delay);
	}
}