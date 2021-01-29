package com.lk.nio.split;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * @Description:
 * @Author: linkun
 * @date: 2021/1/27 17:09
 **/
public class NettyClient {
    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new MessageEncoder());
//                        socketChannel.pipeline().addLast(new ObjectEncoder());
                        socketChannel.pipeline().addLast(new ClientHandler());
                    }
                });
        try {
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9000).sync();

//            channel.writeAndFlush(User.builder().name("张三").password("123456").build());
//            System.out.println("写出user对象");

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    static class ClientHandler extends SimpleChannelInboundHandler<Message> {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("client write");
            for(int i = 0;i < 1000;i++){
                Message message = new Message();
                message.setMstr("机读卡附件是打发发发，的打法好看电视剧里很多事很多事，试试");
                message.setLength(message.getMstr().getBytes().length);
                ctx.channel().writeAndFlush(message);
            }
            System.out.println("写出字符串");
        }

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {

        }
    }
}
