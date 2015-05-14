package com.catalinamarketing.omni.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.catalinamarketing.omni.protocol.message.HaltExecutionMsg;
import com.catalinamarketing.omni.protocol.message.HandShakeMsg;
import com.catalinamarketing.omni.protocol.message.Message;
import com.catalinamarketing.omni.protocol.message.StatusMsg;
import com.catalinamarketing.omni.protocol.message.TestPlanMsg;
import com.catalinamarketing.omni.util.MessageMarshaller;

public class PerfToolClient {
	
	final static Logger logger = LoggerFactory.getLogger(PerfToolClient.class);
	private TestPlanExecutor testPlanExecutor;
	private Socket socket = null;
	private PrintWriter out = null;
	private BufferedReader input = null;
	
	public boolean connect(String hostName, int port) throws InterruptedException {
		boolean connected = false;
		try {
			socket = new Socket(hostName, port);	
			connected = socket.isConnected();
		}catch(Exception ex) {
			logger.error("Problems connecting to the server application. Error : " + ex.getMessage());
			Thread.sleep(10000);
		}
		return connected;
	}
	
	
	private void initializeClient() throws Exception {
		out = new PrintWriter(socket.getOutputStream(), true);
		HandShakeMsg handShakeMsg = new HandShakeMsg();
		handShakeMsg.setInitializationMessage("Available for test plan execution");
		handShakeMsg.setUserName(System.getProperty("user.name"));
		out.println(MessageMarshaller.marshalMessage(handShakeMsg).toString());
		out.flush();
		while(true) {
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        String msg = input.readLine();
	        if(msg != null) {
	        	Message message = MessageMarshaller.unMarshalMessage(msg);
				if(message instanceof TestPlanMsg) {
					if(this.testPlanExecutor != null && this.testPlanExecutor.isTestPlanExecutionComplete())
					logger.info(message.printMessage());
					logger.info("Received TestPlanMsg from Server. Will begin execution of the test plan");
					this.testPlanExecutor = new TestPlanExecutor((TestPlanMsg)message,out);
					new Thread(testPlanExecutor).start();
				} else if(message instanceof StatusMsg) {
					if(testPlanExecutor != null && testPlanExecutor.isTestPlanExecutionComplete()) {
						StatusMsg statusMsg = new StatusMsg();
						statusMsg.setTestPlanVersion(testPlanExecutor.getTestPlan().getTestPlanVersion());
						statusMsg.setExecutionStatus(testPlanExecutor.testPlanStatus());	
						out.println(MessageMarshaller.marshalMessage(statusMsg));
						out.flush();
					}else {
						StatusMsg statusMsg = new StatusMsg();
						statusMsg.setTestPlanVersion("NA");
						statusMsg.setExecutionStatus("Test plan not received yet.");
						out.println(MessageMarshaller.marshalMessage(statusMsg));
						out.flush();
					}
				} else if(message instanceof HaltExecutionMsg) {
					if(this.testPlanExecutor != null ) {
						testPlanExecutor.haltApiThread();
						logger.info(message.printMessage());
						StatusMsg statusMsg = new StatusMsg();
						statusMsg.setTestPlanVersion(testPlanExecutor.getTestPlan().getTestPlanVersion());
						statusMsg.setExecutionStatus("Api threads stoped");
						out.println(MessageMarshaller.marshalMessage(statusMsg));
						out.flush();
					}
				}
	        }else {
	        	logger.warn("Server disconnected client");
	        	break;
	        }
		}
	}
	
	public void launchClient(String hostName, int port) {
		int retryCount = 0;
		while (true) {
			try {
				if(connect(hostName, port)) {
					logger.info("Client connected to server at " + new Date().toString());
					Thread.sleep(60);
					retryCount = 0;
					initializeClient();	
				} else {
					retryCount++;
					Thread.sleep(30000);
					logger.info("Client will wait for 30 seconds and attempt to connect.");
					if(retryCount >= 100) {
						logger.info("Client will stop. Attempted to connect 100 times. Check server remote server hostname and port.");
						break;
					}
				}
			} catch (Exception e) {
				logger.error("Problem occured in client connection to the server. Error : " + e.getMessage());
			} finally{
				try {
					if(out != null) {
						out.close();
					}
					if(input != null) {
						input.close();
					}
					if(socket != null) {
						socket.close();
					}
					Thread.sleep(30000);
				}catch(Exception ex) {
					
				}
				
			}
		}
		
	}
}
