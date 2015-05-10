package com.catalinamarketing.omni.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.catalinamarketing.omni.protocol.message.HandShakeMsg;
import com.catalinamarketing.omni.protocol.message.Message;
import com.catalinamarketing.omni.protocol.message.StandByPeriodExpired;
import com.catalinamarketing.omni.protocol.message.TestPlanMsg;
import com.catalinamarketing.omni.util.MessageMarshaller;
public class PerfToolClient {
	
	final static Logger logger = LoggerFactory.getLogger(PerfToolClient.class);

	
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
			/*StringWriter writer = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(HandShakeMsg.class, StandByPeriodExpired.class, TestPlanMsg.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(handShakeMsg, writer);*/
			out.println(MessageMarshaller.marshalMessage(handShakeMsg).toString());
			out.flush();
			try {
				while(true) {
					BufferedReader input =
			                new BufferedReader(new InputStreamReader(socket.getInputStream()));
			        String msg = input.readLine();
			        System.out.println("Client message received : "+ msg);
			        if(msg != null) {
			        	
			        	//Unmarshaller unMarshaller = context.createUnmarshaller();
						//Object message = unMarshaller.unmarshal(new StringReader(msg));
			        	Message message = MessageMarshaller.unMarshalMessage(msg);
						if(message instanceof StandByPeriodExpired) {
							StandByPeriodExpired standByPeriodExpired = (StandByPeriodExpired)message;
							logger.warn("Server rejected client request to join the pool for test. Will exit now");
							logger.info("Poll start time " +standByPeriodExpired.getStartPollDateTime() + " and end time " + standByPeriodExpired.getEndPollDateTime());
							socket.close();
						} else if(message instanceof TestPlanMsg) {
							TestPlanMsg testPlanMessage = (TestPlanMsg)message;
							logger.info(testPlanMessage.printMessage());
							logger.info("Received TestPlanMsg from Server. Will begin execution of the test plan");
							TestPlanExecutor testPlanExecutor = new TestPlanExecutor(testPlanMessage);
							new Thread(testPlanExecutor).start();
						}	
			        }else {
			        	System.out.println("Server " + msg);
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
