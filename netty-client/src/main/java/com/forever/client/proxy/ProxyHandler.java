package com.forever.client.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @Description:
 * @Author: zhang
 * @Date: 2019/6/18
 */
public class ProxyHandler extends ChannelInboundHandlerAdapter {

    private Object request;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.request = msg;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("client is exception!");
    }

    public Object getRequest() {
        return this.request;
    }
}
