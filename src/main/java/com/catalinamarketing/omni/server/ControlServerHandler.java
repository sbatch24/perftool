package com.catalinamarketing.omni.server;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.catalinamarketing.omni.protocol.message.HandShakeMsg;
import com.catalinamarketing.omni.protocol.message.ShutdownMsg;
import com.catalinamarketing.omni.protocol.message.TestPlanMsg;

@Sharable
public class ControlServerHandler extends SimpleChannelInboundHandler<String> {

	static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Handler Added " + ctx.channel().remoteAddress());
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg)
			throws Exception {
		JAXBContext context = JAXBContext.newInstance(TestPlanMsg.class, ShutdownMsg.class, HandShakeMsg.class);
		Unmarshaller um = context.createUnmarshaller();
		Object message = um.unmarshal(new StringReader(msg));
		if(message instanceof HandShakeMsg) {
			HandShakeMsg handShakeMsg = (HandShakeMsg)message;
			System.out.println("HandShake Message :" + handShakeMsg.getUserName());
		}
		ctx.channel().writeAndFlush("How are you ?" + "\r\n");
	}
}
