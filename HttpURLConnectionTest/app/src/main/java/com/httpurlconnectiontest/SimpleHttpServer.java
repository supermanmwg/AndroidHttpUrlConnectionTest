package com.httpurlconnectiontest;

import com.httpurlconnectiontest.servers.DeliverThread;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by weiguangmeng on 16/5/26.
 */
public class SimpleHttpServer extends Thread {
    public static final int HTTP_PORT = 8000;

    ServerSocket mSocket = null;

    public SimpleHttpServer() {
        try {
            mSocket = new ServerSocket(HTTP_PORT);
        } catch (IOException e) {   //网络请求都是处理IOException
            e.printStackTrace();
        }

        if(mSocket ==null) {
            throw new RuntimeException("服务器初始化失败!");
        }
    }

    @Override
    public void run() {
        try{
            while(true) {
                System.out.println("等待连接中...");
                new DeliverThread(mSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
