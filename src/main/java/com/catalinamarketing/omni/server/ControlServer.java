package com.catalinamarketing.omni.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public ControlServer(Config configuration) {
		this.clientCommunicationHandlerList = new HashMap<String,ClientCommunicationHandler>();
		serverStatus = new CircularFifoQueue<String>(5);
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
                	ClientCommunicationHandler handler = new ClientCommunicationHandler(socket,this);
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
}