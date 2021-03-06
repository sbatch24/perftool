package com.catalinamarketing.omni.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.catalinamarketing.omni.protocol.message.ApiException;
import com.catalinamarketing.omni.protocol.message.ApiHttpResponseCounter;
import com.catalinamarketing.omni.protocol.message.ApiMetricRegistry;
import com.catalinamarketing.omni.protocol.message.HandShakeMsg;
import com.catalinamarketing.omni.protocol.message.Message;
import com.catalinamarketing.omni.protocol.message.StatusMsg;
import com.catalinamarketing.omni.protocol.message.TestExecutionResultMsg;
import com.catalinamarketing.omni.util.MessageMarshaller;

/**
 * Class is responsible for Client communication
 * @author achavan
 *
 */
public class ClientCommunicationHandler implements Runnable {

	final static Logger logger = LoggerFactory.getLogger(ClientCommunicationHandler.class);
	private PrintWriter writer = null;
	private final Object mutex;
	
	private final Socket socket;
	private final String clientIdentifier;
	private final String hostName;
	private String userName;
	private STATUS status;
	private List<ApiHttpResponseCounter> apiResponseCounterList;
	private List<ApiException> apiExceptionList;
	private List<ApiMetricRegistry> metricRegistryList;
	private ControlServer controlServer;
	
	public enum STATUS {
		CONNECTED(0),
		INITIAL_STATUS_SENT(1),
		BUSY_EXECUTING_TEST(2),
		TEST_EXECUTION_HALTED(3),
		FINISHED_EXECUTING_TEST(4),
		EXECUTION_STATUS_SENT(5),
		DISCONNECTED(6),
		DISCONNECTED_STATUS_SENT(7); 
		
		private int status;
		
		STATUS(int s) {
			this.setStatus(s);
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}
		
		@Override
		public String toString() {
			 String message = null;
			switch(this) {
				case CONNECTED :
					message = "Worker joined the pool on " + new Date().toString();
					break;
				case BUSY_EXECUTING_TEST:
					message = "Worker is busy executing test plan";
					break;
				case TEST_EXECUTION_HALTED:
					message = "Test execution was halted on " + new Date().toString();
					break;
				case DISCONNECTED : 
					message = "Worker left the pool on " + new Date().toString() + ". One less resource available";
					break;
				case FINISHED_EXECUTING_TEST :
					message = "Test execution finished on " + new Date().toString();
					break;
			}
			return message;
		}
	}

	public ClientCommunicationHandler(Socket socket, ControlServer cs) {
		this.socket = socket;
		this.clientIdentifier = UUID.randomUUID().toString();
		this.hostName  = socket.getInetAddress().getHostName();
		this.status = STATUS.CONNECTED;
		mutex = new Object();
		this.controlServer = cs;
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
			} else if(message instanceof TestExecutionResultMsg) {
				TestExecutionResultMsg executionResult = (TestExecutionResultMsg) message;
				setStatus(STATUS.FINISHED_EXECUTING_TEST);
				setApiExceptionList(executionResult.getApiExceptionList());
				setApiResponseCounterList(executionResult.getApiResponseCounterList());
				setMetricRegistryList(executionResult.getMetricRegistryList());
				logger.info(executionResult.printMessage());
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
		        	setStatus(STATUS.DISCONNECTED);
		        	break;
		        }
			}
		}catch(Exception ex) {
			logger.error("Problem in client communication thread. Error: " + ex.getMessage());
        	setStatus(STATUS.DISCONNECTED);
		}finally {
			try {
				socket.close();
	        	setStatus(STATUS.DISCONNECTED);
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

	public STATUS getStatus() {
		return status;
	}

	public void setStatus(STATUS status) {
		this.status = status;
	}

	public List<ApiHttpResponseCounter> getApiResponseCounterList() {
		return apiResponseCounterList;
	}

	public void setApiResponseCounterList(List<ApiHttpResponseCounter> apiResponseCounterList) {
		this.apiResponseCounterList = apiResponseCounterList;
	}

	public List<ApiException> getApiExceptionList() {
		return apiExceptionList;
	}

	public void setApiExceptionList(List<ApiException> apiExceptionList) {
		this.apiExceptionList = apiExceptionList;
	}

	public List<ApiMetricRegistry> getMetricRegistryList() {
		return metricRegistryList;
	}

	public void setMetricRegistryList(List<ApiMetricRegistry> metricRegistryList) {
		this.metricRegistryList = metricRegistryList;
	}
}