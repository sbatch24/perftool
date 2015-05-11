package com.catalinamarketing.omni.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.catalinamarketing.omni.api.CustomerMediaEvent;
import com.catalinamarketing.omni.api.DirectDeposit;
import com.catalinamarketing.omni.api.DirectDepositStatus;
import com.catalinamarketing.omni.api.MediaEvents;
import com.catalinamarketing.omni.api.TargetedMediaResponse;
import com.catalinamarketing.omni.config.Config;
import com.google.gson.Gson;

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
	
	public ControlServer(Config configuration) {
		this.clientCommunicationHandlerList = new HashMap<String,ClientCommunicationHandler>();
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
			ConsoleReaderThread consoleReader = new ConsoleReaderThread(this);
			new Thread(consoleReader).start();
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
}