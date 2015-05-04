package com.catalinamarketing.omni.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class PerfToolClientInitializer extends
		ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) {
		try {
			
			System.out.println("InitChannel client " + ch.isOpen() +  " " + ch.isActive());
			ChannelPipeline pipeLine = ch.pipeline();
			pipeLine.addLast(new DelimiterBasedFrameDecoder(10000, Delimiters.lineDelimiter()));
			pipeLine.addLast("decoder", new StringDecoder());
			pipeLine.addLast("encoder", new StringEncoder());
			pipeLine.addLast("handler", new PerfToolClientHandler());	
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
