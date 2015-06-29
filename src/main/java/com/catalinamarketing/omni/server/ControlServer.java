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
import org.apache.tomcat.util.digester.ArrayStack;
import org.mapdb.Queues.CircularQueue;
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
	private CircularFifoQueue<String> serverStatus;
	private static boolean testInProgress;
	private String testStatus;
	public ControlServer(Config configuration) {
		this.clientCommunicationHandlerList = new HashMap<String,ClientCommunicationHandler>();
		serverStatus = new CircularFifoQueue<String>(5);
		testInProgress = false;
		serverStatus.add("Server initialized at " + new Date().toString());
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
                	ClientCommunicationHandler handler = new ClientCommunicationHandler(socket);
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

	public List<String> getServerStatus() {
		return Arrays.asList((String[]) serverStatus.toArray(new String[serverStatus.size()]));
	}

	public void updateStatus(String serverStatus) {
		this.serverStatus.add(serverStatus);
	}

	public static boolean isTestInProgress() {
		return testInProgress;
	}

	public static void setTestInProgress(boolean val) {
		testInProgress = val;
	}

	public String getTestStatus() {
		return testStatus;
	}

	public void setTestStatus(String testStatus) {
		this.testStatus = testStatus;
	}
	
	/**
	 * This timer is started after a request to kick off test execution is requested. The timer will be set to expire
	 * when the tests is scheduled to end.
	 * @param delay
	 */
	public static void startTestExecutionCheckTimer(long delay) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				setTestInProgress(false);
			}
		}, delay);
	}
}