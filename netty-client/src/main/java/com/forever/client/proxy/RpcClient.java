package com.forever.client.proxy;

import com.forever.protocol.ProtocolEntity;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Description:
 * @Author: zhang
 * @Date: 2019/6/18
 */
public class RpcClient {

    public static <T> T create(Class<?> clazz){
        ClientProxy handler = new ClientProxy(clazz);
        Class<?>[] interfase = clazz.isInterface() ?
                            new Class<?>[]{clazz}:
                            clazz.getInterfaces();
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), interfase, handler);
    }

    private static class ClientProxy implements InvocationHandler {

        private Class<?> clazz;
        public ClientProxy(Class<?> clazz) {
            this.clazz = clazz;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if(!clazz.isInterface()){
                return method.invoke(this, args);
            }else{
                return invokeRpc(proxy, method, args);
            }
        }

        private Object invokeRpc(Object proxy, Method method, Object[] args) {
            ProtocolEntity request = new ProtocolEntity();
            request.setClassName(clazz.getName());
            request.setMethodName(method.getName());
            request.setParamsType(method.getParameterTypes());
            request.setPrarams(args);

            Bootstrap server = new Bootstrap();
            final ProxyHandler handler = new ProxyHandler();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            server.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            pipeline.addLast(new LengthFieldPrepender(4));

                            pipeline.addLast("encoder", new ObjectEncoder());
                            pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));

                            pipeline.addLast(handler);
                        }
                    });
            ChannelFuture future = server.connect("localhost", 8080);
            future.channel().writeAndFlush(request);
            try {
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return handler.getRequest();
        }
    }
}
