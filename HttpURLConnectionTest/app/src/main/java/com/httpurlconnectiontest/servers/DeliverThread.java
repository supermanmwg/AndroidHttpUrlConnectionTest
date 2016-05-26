package com.httpurlconnectiontest.servers;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by weiguangmeng on 16/5/26.
 */
public class DeliverThread extends Thread {
    private static final String TAG = "DeliverThread";
    Socket mClientSocket;

    //输入流
    BufferedReader mInputStream;
    //输出流
    PrintStream mOutputStream;
    //请求方法
    String httpMethod;
    //子路径
    String subPath;
    //分隔符
    String boundary;
    //请求参数
    Map<String, String> mParams = new HashMap<>();
    //是否已经解析完Header
    boolean isParseHeader = true;

    public DeliverThread(Socket socket) {
        mClientSocket = socket;
    }

    @Override
    public void run() {
        try{
            mInputStream = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));
            mOutputStream = new PrintStream(mClientSocket.getOutputStream());
            parseRequest();
        }catch (IOException e) {

        }finally {

        }
    }

    private void parseRequest(){
        String line;
        try {
            int lineNum = 0;
            while((line = mInputStream.readLine()) != null) {
                //第一行为请求行
                if(lineNum == 0) {
                    parseRequestLine(line);
                }

                //判断是否是数据的结束行
                if(isEnd(line)) {
                    break;
                }

                //解析header参数
                if(lineNum != 0 && !isParseHeader) {
                    parserHeaders(line);
                }

                //解析请求参数
                if(isParseHeader) {
                    parseRequestParams(line);
                }

                lineNum++;
            }
        }catch (IOException e) {

        }
    }

    // 是否是结束行
    private boolean isEnd(String line) {
        return line.equals("--" + boundary + "--");
    }

    private void parseRequestLine(String line) {
        String[] tempStrings = line.split(" ");
        httpMethod = tempStrings[0];
        subPath = tempStrings[1];
        System.out.print("请求方式:" + tempStrings[0]);
        System.out.print("子路径:" + tempStrings[1]);
        System.out.print("HTTP版本:" + tempStrings[2]);
    }

    private void parserHeaders(String headLine) {
        if(headLine.equals("")) {
            isParseHeader = true;
            System.out.print("-------header解析完成");
            return;
        } else if(headLine.contains("boundary")) {
            boundary = parseSecondField(headLine) ;
            Log.d(TAG, "分隔符:" + boundary);
        } else {
            parseHeaderParam(headLine);
        }
    }

    private String parseSecondField(String line) {
        String[] headerArray = line.split(";");
        parseHeaderParam(headerArray[0]);
        if(headerArray.length > 1) {
            return headerArray[1].split("=")[1];
        }

        return "";
    }

    private void parseHeaderParam(String headLine) {
        String[] keyValue = headLine.split(":");
    }

    private void parseRequestParams(String line){}

}
