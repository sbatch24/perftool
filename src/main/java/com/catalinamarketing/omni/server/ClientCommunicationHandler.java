package com.catalinamarketing.omni.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.catalinamarketing.omni.config.Config;
import com.catalinamarketing.omni.protocol.message.HandShakeMsg;
import com.catalinamarketing.omni.protocol.message.Message;
import com.catalinamarketing.omni.protocol.message.ShutdownMsg;
import com.catalinamarketing.omni.protocol.message.StandByPeriodExpired;
import com.catalinamarketing.omni.protocol.message.TestPlanMsg;
import com.catalinamarketing.omni.util.MessageMarshaller;

public class ClientCommunicationHandler implements Runnable {

	final static Logger logger = LoggerFactory.getLogger(ClientCommunicationHandler.class);
	private PrintWriter writer = null;
	private final Object mutex;
	
	private final Socket socket;
	private final ControlServer controlServer;
	private final Config config;
	public ClientCommunicationHandler(Socket socket, ControlServer cs, Config config) {
		this.socket = socket;
		this.controlServer = cs;
		this.config = config;
		mutex = new Object();
	}
	
	/**
	 * Write the given message to the socket output stream. It also flushes the message.
	 * @param message
	 */
	public  void  writeMessage(Message message) {
		synchronized (mutex) {
			try{
				/*StringWriter writer = new StringWriter();
				JAXBContext context = JAXBContext.newInstance(TestPlanMsg.class, HandShakeMsg.class,
										StandByPeriodExpired.class, ShutdownMsg.class);
				Marshaller marshaller = context.createMarshaller();
				marshaller.marshal(message, writer);
				if(this.writer == null) {
					this.writer  = new PrintWriter(socket.getOutputStream(), true);
				}
				System.out.println("Message to be sent " + writer.toString());*/
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
	public Message marshalIncomingMessage(String data) {
		Message message = null;
		try {
			/*JAXBContext context = JAXBContext.newInstance(StandByPeriodExpired.class, HandShakeMsg.class, 
					ShutdownMsg.class, TestPlanMsg.class);
			Unmarshaller unMarshaller = context.createUnmarshaller();
			StringReader reader = new StringReader(data);
			message = (Message)unMarshaller.unmarshal(reader);	*/
			message = MessageMarshaller.unMarshalMessage(data);
		}catch(Exception ex) {
			logger.error("Problem unmarshalling message from client. Error: " + ex.getMessage());
			ex.printStackTrace();
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
				Calendar calendar = Calendar.getInstance();
				Date et = new Date(controlServer.standbyStartTime());
				calendar.setTime(et);
				calendar.add(Calendar.SECOND, config.getServer().getStandby());
				HandShakeMsg msg = new HandShakeMsg();
				msg.setInitializationMessage("Server waiting for clients to join poll. Will distribute test plan at " + calendar.getTime().toString());
				writeMessage(msg);
			} else if(message instanceof ShutdownMsg) {
				// TODO
			}
		}
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				BufferedReader input =
		                new BufferedReader(new InputStreamReader(socket.getInputStream()));
		        String incomingMessage = input.readLine();
		        if(incomingMessage != null) {
		        	Message message = marshalIncomingMessage(incomingMessage);
			        processMessage(message);	
		        }else {
		        	System.out.println("Server " + incomingMessage);
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
	}
}