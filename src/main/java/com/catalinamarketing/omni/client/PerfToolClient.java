package com.catalinamarketing.omni.client;

import java.io.BufferedReader;
import java.io.IOException;
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
	
	public void launchClient(String hostName, int port) {
		Socket socket = null;
		PrintWriter out = null;
		try {
			socket = new Socket(hostName, port);
			logger.info("Client connected to server at " + new Date().toString());
			Thread.sleep(60);
			out = new PrintWriter(socket.getOutputStream(), true);
			HandShakeMsg handShakeMsg = new HandShakeMsg();
			handShakeMsg.setInitializationMessage("Available for test plan execution");
			handShakeMsg.setUserName(System.getProperty("user.name"));
			out.println(MessageMarshaller.marshalMessage(handShakeMsg).toString());
			out.flush();
			try {
				while(true) {
					BufferedReader input =
			                new BufferedReader(new InputStreamReader(socket.getInputStream()));
			        String msg = input.readLine();
			        if(msg != null) {
			        	Message message = MessageMarshaller.unMarshalMessage(msg);
						if(message instanceof TestPlanMsg) {
							logger.info(message.printMessage());
							logger.info("Received TestPlanMsg from Server. Will begin execution of the test plan");
							this.testPlanExecutor = new TestPlanExecutor((TestPlanMsg)message);
							new Thread(testPlanExecutor).start();
						} else if(message instanceof StatusMsg) {
							if(this.testPlanExecutor != null ) {
								StatusMsg statusMsg = new StatusMsg();
								statusMsg.setTestPlanVersion(testPlanExecutor.getTestPlan().getTestPlanVersion());
								statusMsg.setExecutionStatus(testPlanExecutor.testPlanStatus());	
								out.println(MessageMarshaller.marshalMessage(statusMsg).toString());
								out.flush();
							}else {
								StatusMsg statusMsg = new StatusMsg();
								statusMsg.setTestPlanVersion("NA");
								statusMsg.setExecutionStatus("Test plan not received yet.");
								out.println(MessageMarshaller.marshalMessage(statusMsg).toString());
								out.flush();
							}
						} else if(message instanceof HaltExecutionMsg) {
							if(this.testPlanExecutor != null ) {
								testPlanExecutor.haltApiThread();
								logger.info(message.printMessage());
								StatusMsg statusMsg = new StatusMsg();
								statusMsg.setTestPlanVersion(testPlanExecutor.getTestPlan().getTestPlanVersion());
								statusMsg.setExecutionStatus("Api threads stoped");
								out.println(MessageMarshaller.marshalMessage(statusMsg).toString());
								out.flush();
							}
						}
			        }
				}
			}catch(Exception ex) {
				logger.error("Problem in client communication thread. Error: " + ex.getMessage());
				ex.printStackTrace();
			}finally {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			out.close();
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
