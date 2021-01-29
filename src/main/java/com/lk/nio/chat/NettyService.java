package com.lk.nio.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

/**
 * @Description: Netty
 * @Author: linkun
 * @date: 2021/1/19 22:22
 **/
public class NettyService {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new StringEncoder());
                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new ChatServerHandler());
                        }
                    });
            System.out.println("netty Service 启动");

            ChannelFuture cf = bootstrap.bind(9000).sync();

            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }


    static class ChatServerHandler extends SimpleChannelInboundHandler<String> {
        private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        /**
         * 客户端接入
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
//            super.channelActive(ctx);
            Channel channel = ctx.channel();
            Date now = new Date();
            channelGroup.writeAndFlush(sf.format(now)+" "+channel.remoteAddress().toString()+" 上线");
            channelGroup.add(channel);
            System.out.println(sf.format(now)+" "+channel.remoteAddress().toString()+" 上线");
        }

        /**
         * 客户端下线
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//            super.channelInactive(ctx);
            Channel channel = ctx.channel();
            channelGroup.remove(channel);
            Date now = new Date();
            channelGroup.writeAndFlush(sf.format(now)+" "+channel.remoteAddress().toString()+" 下线");
            System.out.println(sf.format(now)+" "+channel.remoteAddress().toString()+" 下线");
        }


        /**
         * 读取客户端数据
         * @param channelHandlerContext
         * @param s
         * @throws Exception
         */
        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
            System.out.println(s);
            channelGroup.forEach(channel->{
                if(channel != channelHandlerContext.channel()){
                    channel.writeAndFlush("["+channel.remoteAddress().toString()+"]:"+s);
                }else{
                    channel.writeAndFlush("[自己]:"+s);
                }
            });
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//            super.exceptionCaught(ctx, cause);
            ctx.close();
        }
    }
}

