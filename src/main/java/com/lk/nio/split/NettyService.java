package com.lk.nio.split;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @Description:
 * @Author: linkun
 * @date: 2021/1/27 16:58
 **/
public class NettyService {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                socketChannel.pipeline().addLast(new MessageDecoder());
                                socketChannel.pipeline().addLast(new ServerHandler());
                            }
                        });
        System.out.println("netty Service 启动");
        try {
            ChannelFuture channelFuture = bootstrap.bind(9000).sync();

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    static class ServerHandler extends SimpleChannelInboundHandler<Message>{
        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message o) throws Exception {
            System.out.println("service read");
            System.out.println(o.getMstr());
//            User user = (User)o;
//            System.out.println(user.getName());
//            System.out.println(user.getPassword());
        }
    }
}
