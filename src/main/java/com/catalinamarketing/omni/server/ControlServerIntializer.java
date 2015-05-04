package com.catalinamarketing.omni.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControlServerIntializer extends ChannelInitializer<SocketChannel> {

	final static Logger logger = LoggerFactory.getLogger(ControlServerIntializer.class);
	
	@Override
	public void exceptionCaught(ChannelHandlerContext arg0, Throwable arg1)
			throws Exception {
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channelActive " + ctx.channel().remoteAddress());
	}
	
	@Override
	protected void initChannel(SocketChannel socketChannel) throws Exception {
		ChannelPipeline pipeLine = socketChannel.pipeline();
        pipeLine.addLast(new DelimiterBasedFrameDecoder(10000, Delimiters.lineDelimiter()));
        pipeLine.addLast(new StringDecoder());
        pipeLine.addLast(new StringEncoder());
		pipeLine.addLast(new ControlServerHandler());
	}
}
