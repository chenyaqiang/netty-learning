package com.netty.http;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;


/**
 * Created by viruser on 2018/4/16.
 */
public class HttpServer {


    public void run() throws InterruptedException {
        int port = 6789;
        EventLoopGroup group = new NioEventLoopGroup();   // 通过nio方式来接收连接和处理连接
        ServerBootstrap b = new ServerBootstrap();
        try {
            b.group(group);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new HttpResponseEncoder());
                    socketChannel.pipeline().addLast(new HttpRequestDecoder());
                    socketChannel.pipeline().addLast(new HttpObjectAggregator(10 * 1024 * 1024));
                    socketChannel.pipeline().addLast(new HttpServerHandler());// 服务端业务逻辑
                }
            }); //设置过滤器
            // 服务器绑定端口监听
            ChannelFuture f = b.bind(port).sync();
            System.out.println("服务端启动成功,端口是:" + port);
            // 监听服务器关闭监听
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully(); //关闭EventLoopGroup，释放掉所有资源包括创建的线程
        }
    }

    /**
     * Netty创建全部都是实现自AbstractBootstrap。
     * 客户端的是Bootstrap，服务端的则是	ServerBootstrap。
     **/
    public static void main(String[] args) throws InterruptedException {
        new HttpServer().run();
    }

}
