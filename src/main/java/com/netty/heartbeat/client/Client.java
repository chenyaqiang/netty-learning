package com.netty.heartbeat.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by viruser on 2018/4/13.
 */
public class Client {

    public static String host = "127.0.0.1";  //ip地址
    public static int port = 8379;          //端口
    /// 通过nio方式来接收连接和处理连接
    private static EventLoopGroup group = new NioEventLoopGroup();
    private static  Bootstrap b = new Bootstrap();
    private static Channel ch;

    /**
     * Netty创建全部都是实现自AbstractBootstrap。
     * 客户端的是Bootstrap，服务端的则是    ServerBootstrap。
     **/
    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("客户端成功启动...");
        b.group(group);
        b.channel(NioSocketChannel.class);
        b.handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                //入参说明: 读超时时间、写超时时间、所有类型的超时时间、时间格式
                //因为服务端设置的超时时间是5秒，所以设置4秒
                socketChannel.pipeline().addLast( new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS));
                socketChannel.pipeline().addLast(new StringDecoder());
                socketChannel.pipeline().addLast(new StringEncoder());
                socketChannel.pipeline().addLast(new ClientHandler());
            }
        });
        // 连接服务端
        ch = b.connect(host, port).sync().channel();
        star();
    }

    public static void star() throws IOException{
        String str="Hello Netty";
        ch.writeAndFlush(str);
//      ch.writeAndFlush(str+ "\r\n");
        System.out.println("客户端发送数据:"+str);
    }

}
