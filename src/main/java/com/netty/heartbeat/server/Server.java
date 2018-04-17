package com.netty.heartbeat.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Created by viruser on 2018/4/13.
 */
public class Server {

    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void run() {

        EventLoopGroup bossGroup = new NioEventLoopGroup(); //用于处理服务器端接收客户端连接
        EventLoopGroup workerGroup = new NioEventLoopGroup(); //进行网络通信（读写）

        try {
            ServerBootstrap bootstrap = new ServerBootstrap(); //辅助工具类，用于服务器通道的一系列配置
            bootstrap.group(bossGroup, workerGroup) //绑定两个线程组
                    .channel(NioServerSocketChannel.class) //指定NIO的模式
                    .childHandler(new ChannelInitializer<SocketChannel>() { //配置具体的数据处理方式
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new StringEncoder());
                            socketChannel.pipeline().addLast(new ServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128) //设置TCP缓冲区
                    .option(ChannelOption.SO_SNDBUF, 32 * 1024) //设置发送数据缓冲大小
                    .option(ChannelOption.SO_RCVBUF, 32 * 1024) //设置接受数据缓冲大小
                    .childOption(ChannelOption.SO_KEEPALIVE, true); //保持连接

            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new Server(8379).run();
    }
}
