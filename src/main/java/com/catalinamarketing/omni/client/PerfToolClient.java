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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.catalinamarketing.omni.protocol.message.HandShakeMsg;
public class PerfToolClient {
	
	public void launchClient(String hostName, int port) {
		Socket s = null;
		PrintWriter out = null;
		try {
			s = new Socket(hostName, port);
			Thread.sleep(100);
			out = new PrintWriter(s.getOutputStream(), true);
			HandShakeMsg handShakeMsg = new HandShakeMsg();
			handShakeMsg.setUserName(System.getProperty("user.name"));
			StringWriter writer = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(HandShakeMsg.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(handShakeMsg, writer);
			out.println(writer.toString());
			out.flush();
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			for (;;) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }

                // If user typed the 'bye' command, wait until the server closes
                // the connection.
                if ("bye".equals(line.toLowerCase())) {
                    break;
                }
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			out.close();
			try {
				s.close();
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
