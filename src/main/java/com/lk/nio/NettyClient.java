package com.lk.nio;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.util.Scanner;

/**
 * @Description: NettyClient
 * @Author: linkun
 * @date: 2021/1/19 22:43
 **/
public class NettyClient {
    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new NettyClientHandler());
                        }
                    });
            System.out.println("netty client 启动");
            ChannelFuture cf = bootstrap.connect("127.0.0.1",9000).sync();

            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
     static class NettyClientHandler extends ChannelInboundHandlerAdapter {
         /**
          * 当客户端连接服务器完成会触发
          * @param ctx
          * @throws Exception
          */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
//            ByteBuf buf = Unpooled.copiedBuffer("HelloServer", CharsetUtil.UTF_8);
//            ctx.writeAndFlush(buf);
            //我上线了
            ByteBuf buf = Unpooled.copiedBuffer(ctx.channel().id()+"上线",CharsetUtil.UTF_8);
            ctx.writeAndFlush(buf);
            while(true){
                Scanner scanner = new Scanner(System.in);
                if(scanner.hasNext()){
                    String str = scanner.next();
                    buf = Unpooled.copiedBuffer(str,CharsetUtil.UTF_8);
                    ctx.writeAndFlush(buf);
                }
            }
        }

         /**
          * 服务端发送数据会触发
          * @param ctx
          * @param msg
          * @throws Exception
          */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//            ByteBuf buf = (ByteBuf)msg;
//            System.out.println("收到服务端消息："+buf.toString(CharsetUtil.UTF_8));
//            System.out.println("服务端地址："+ctx.channel().remoteAddress());
            ByteBuf buf = (ByteBuf)msg;
            System.out.println(buf.toString(CharsetUtil.UTF_8));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

}


