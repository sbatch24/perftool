package com.catalinamarketing.omni.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

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
							testPlanExecutor.executeTestPlan();
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
	
	public void launch(String hostName, int port) {

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootStrap = new Bootstrap();
            
            bootStrap.group(group)
             .channel(NioSocketChannel.class)
             .handler(new PerfToolClientInitializer());
            // Start the connection attempt.
            SocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("173.171.241.49"), port);
            SocketAddress localAddress = new InetSocketAddress(InetAddress.getByName("localhost"), 12001);
			Channel connectionChannel = bootStrap.connect(socketAddress).sync().channel();
			HandShakeMsg handShakeMsg = new HandShakeMsg();
			handShakeMsg.setUserName(System.getProperty("user.name"));
			try {
				StringWriter writer = new StringWriter();
				JAXBContext context = JAXBContext.newInstance(HandShakeMsg.class);
				Marshaller marshaller = context.createMarshaller();
				marshaller.marshal(handShakeMsg, writer);
				connectionChannel.writeAndFlush(writer.toString()+"\r\n");
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	            for (;;) {
	                String line = in.readLine();
	                if (line == null) {
	                    break;
	                }

	                // If user typed the 'bye' command, wait until the server closes
	                // the connection.
	                if ("bye".equals(line.toLowerCase())) {
	                    connectionChannel.closeFuture().sync();
	                    break;
	                }
	            }
			}catch(Exception e) {
				e.printStackTrace();
			}
        } catch(Exception e) {
        	e.printStackTrace();
        }
        finally {
            // The connection is closed automatically on shutdown.
            group.shutdownGracefully();
        }
	}
}
