package com.catalinamarketing.omni.util;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.catalinamarketing.omni.protocol.message.HaltExecutionMsg;
import com.catalinamarketing.omni.protocol.message.HandShakeMsg;
import com.catalinamarketing.omni.protocol.message.Message;
import com.catalinamarketing.omni.protocol.message.StatusMsg;
import com.catalinamarketing.omni.protocol.message.TestPlanMsg;

/**
 * Class unmarshalls/marshalls messages using jaxb marshallers.
 * @author achavan
 *
 */
public class MessageMarshaller {

	final static Logger logger = LoggerFactory.getLogger(MessageMarshaller.class);

	private  static JAXBContext context;
	private static MessageMarshaller instance = new MessageMarshaller();
	private MessageMarshaller() {
		try {
			context = JAXBContext.newInstance( HandShakeMsg.class, TestPlanMsg.class, StatusMsg.class, HaltExecutionMsg.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	public static MessageMarshaller instanceOf() {
		return instance;
	}
	
	/**
	 * Marshal message to string representation.
	 * @param message
	 * @return String
	 */
	public static String marshalMessage(Message message) {
		StringBuffer buffer = new StringBuffer();
		try {
			StringWriter writer = new StringWriter();
			Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(message, writer);
			buffer.append(writer.toString());
		}catch(Exception ex) {
			
		}
		return buffer.toString();
	}
	
	/**
	 * UnMarshal String data to Message instance.
	 * @param data
	 * @return Message
	 */
	public static Message unMarshalMessage(String data) {
		Message message = null;
		try {
			Unmarshaller unMarshaller = context.createUnmarshaller();
			StringReader reader = new StringReader(data);
			 message = (Message)unMarshaller.unmarshal(reader);	
		}catch(Exception ex) {
			logger.error("Problem unmarshalling from data " + data + " Error: " + ex.getMessage());
			ex.printStackTrace();
		}
		return message;
	}
}
