package com.lk.nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Description: BIO
 * @Author: linkun
 * @date: 2021/1/18 21:27
 **/
public class BIOService {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9000);
        while(true){
            System.out.println("服务器等待接收连接");
            Socket clientSocket = serverSocket.accept();
            System.out.println("客户端连接成功");
            handler(clientSocket);
        }
    }

    private static void handler(Socket clientSocket){
        try{
            while(true){
                byte[] bytes = new byte[1024];
                InputStream inputStream = clientSocket.getInputStream();
                int len = inputStream.read(bytes);
                if(len > 0 ){
                    System.out.println("客户端发过来消息："+new String(bytes,0,len));
                }else if(len == -1){
                    break;
                }
            }
        }catch (Exception ex){

        }finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
