package com.forever.registry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @Description:
 * @Author: zhang
 * @Date: 2019/6/18
 */
public class Registry {

    private int port;

    public Registry(int port) {
        this.port = port;
    }

    public void start(){
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap server = new ServerBootstrap();
        server.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        //自定义编解码器
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(
                                Integer.MAX_VALUE, 0, 4, 0, 4));
                        //自定义编码器
                        pipeline.addLast(new LengthFieldPrepender(4));

                        //对象参数类型编码器
                        pipeline.addLast("encoder", new ObjectEncoder());

                        //对象参数类型解码器
                        pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));

                        //自定义处理逻辑
                        pipeline.addLast(new RegistryHandler());
                    }
                })
            .option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.SO_KEEPALIVE, true);
        try {
            ChannelFuture future = server.bind(this.port).sync();
            System.out.println("GP RPC Registry start listen at " + port );
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new Registry(8080).start();
    }
}
