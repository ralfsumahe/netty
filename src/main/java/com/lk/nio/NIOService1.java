package com.lk.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Description: NIO
 * @Author: linkun
 * @date: 2021/1/18 22:40
 **/
public class NIOService1 {
    static List<SocketChannel> channelList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(9000));
        System.out.println("服务器启动成功");

        serverSocketChannel.configureBlocking(false);
        while(true){
            SocketChannel socketChannel = serverSocketChannel.accept();
            if(socketChannel != null){
                socketChannel.configureBlocking(false);
                System.out.println("客户端连接成功");
                channelList.add(socketChannel);
            }
            Iterator<SocketChannel> it = channelList.iterator();
            while(it.hasNext()){
                SocketChannel channel = it.next();
                ByteBuffer byteBuffer = ByteBuffer.allocate(128);
                int len = channel.read(byteBuffer);
                if(len > 0){
                    System.out.println("客户端发来消息:"+new String(byteBuffer.array()));
                }else if(len == -1){
                    it.remove();
                    System.out.println("客户端断开连接");
                }
            }
        }


    }
}
