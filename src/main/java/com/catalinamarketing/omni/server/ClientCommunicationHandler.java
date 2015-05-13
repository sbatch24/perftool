package com.catalinamarketing.omni.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.catalinamarketing.omni.protocol.message.HandShakeMsg;
import com.catalinamarketing.omni.protocol.message.Message;
import com.catalinamarketing.omni.protocol.message.StatusMsg;
import com.catalinamarketing.omni.util.MessageMarshaller;

public class ClientCommunicationHandler implements Runnable {

	final static Logger logger = LoggerFactory.getLogger(ClientCommunicationHandler.class);
	private PrintWriter writer = null;
	private final Object mutex;
	
	private final Socket socket;
	private final ControlServer controlServer;
	private final String clientIdentifier;
	private final String hostName;
	private String userName;

	public ClientCommunicationHandler(Socket socket, ControlServer controlServer) {
		this.socket = socket;
		this.controlServer = controlServer;
		this.clientIdentifier = UUID.randomUUID().toString();
		this.hostName  = socket.getInetAddress().getHostName();
		mutex = new Object();
	}
	
	/**
	 * Write the given message to the socket output stream. It also flushes the message.
	 * @param message
	 */
	public  void  writeMessage(Message message) {
		synchronized (mutex) {
			try{
				if(this.writer == null) {
					this.writer  = new PrintWriter(socket.getOutputStream(), true);
				}
				this.writer.println(MessageMarshaller.marshalMessage(message).toString());
				this.writer.flush();
			}catch(Exception ex) {
				logger.error("Problem writing to the client server socket. Error: "+ ex.getMessage()); 
			}
		}
	}
	
	/**
	 * Processes the message received from client. Marshals it back into the message instance
	 * @param message
	 * @return Message
	 */
	private Message marshalIncomingMessage(String data) {
		Message message = null;
		try {
			message = MessageMarshaller.unMarshalMessage(data);
		}catch(Exception ex) {
			logger.error("Problem unmarshalling message from client. Error: " + ex.getMessage());
		}
		return message;
	}
	
	/**
	 * Process message received from the client.
	 * @param message
	 */
	public void processMessage(Message message) {
		synchronized (mutex) {
			if(message instanceof HandShakeMsg) {
				setUserName(message.getUserName());
				logger.info(message.printMessage());
				HandShakeMsg msg = new HandShakeMsg();
				msg.setInitializationMessage("Test plan will be delivered by the server.");
				writeMessage(msg);
			} else if(message instanceof StatusMsg) {
				StatusMsg statusMessage = (StatusMsg) message;
				logger.info(statusMessage.printMessage());
			}
		}
	}
	
	@Override
	public void run() {
		try {
			BufferedReader input =
	                new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while(true) {
		        String incomingMessage = input.readLine();
		        if(incomingMessage != null) {
		        	Message message = marshalIncomingMessage(incomingMessage);
			        processMessage(message);	
		        }else {
		        	controlServer.removeClientCommunicationHandler(clientIdentifier);
		        	break;
		        }
			}
		}catch(Exception ex) {
			logger.error("Problem in client communication thread. Error: " + ex.getMessage());
			ex.printStackTrace();
		}finally {
			try {
				socket.close();
				controlServer.removeClientCommunicationHandler(clientIdentifier);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String getClientIdentifier() {
		return clientIdentifier;
	}

	public String getHostName() {
		return hostName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}