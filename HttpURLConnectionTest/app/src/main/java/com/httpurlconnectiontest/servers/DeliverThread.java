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
    // 请求headers
    Map<String, String> mHeaders = new HashMap<>();
    //是否已经解析完Header
    boolean isParseHeader = true;

    public DeliverThread(Socket socket) {
        mClientSocket = socket;
        Log.d(TAG, "accept socket");
    }

    @Override
    public void run() {
        try{
            Log.d(TAG, "parse request start1");
            mInputStream = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));
            Log.d(TAG, "parse request start2");
            mOutputStream = new PrintStream(mClientSocket.getOutputStream());
            Log.d(TAG, "parse request start3");
            parseRequest();
            Log.d(TAG, "parse request start4");
        }catch (IOException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }finally {
            Log.d(TAG, "parse request completed");
        }
    }

    private void parseRequest(){
        String line;
        try {
            int lineNum = 0;
            Log.d(TAG, "receive");
            while((line = mInputStream.readLine()) != null) {
                Log.d(TAG, "receive" + line);
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
            Log.d(TAG, "get Message ");
            Log.d(TAG, "get Message " + e.getMessage());
            e.printStackTrace();
        }finally {
            Log.d(TAG, "completely parse packet");
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
        Log.d(TAG, "请求方式:" + tempStrings[0]);
        Log.d(TAG, "子路径:" + tempStrings[1]);
        Log.d(TAG, "HTTP版本:" + tempStrings[2]);
    }

    private void parserHeaders(String headLine) {
        if(headLine.equals("")) {
            isParseHeader = true;
            Log.d(TAG, "-------header解析完成");
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
        mHeaders.put(keyValue[0].trim(), keyValue[1].trim());
        Log.d(TAG, "header参数名:" + keyValue[0].trim() + ", 参数值:" + keyValue[1].trim());
    }

    private void parseRequestParams(String paramLine) throws IOException {
        if(paramLine.equals("--" + boundary)) {
            //读取Content-Disposition行
            String contentDisposition = mInputStream.readLine();
            //解析参数名
            String paramName = parseSecondField(contentDisposition);
            //读取参数header与参数值之间的空行
            mInputStream.readLine();
            //读取参数值
            String paramValue = mInputStream.readLine();
            mParams.put(paramName, paramValue);
            Log.d(TAG, "参数名:" + paramName + ", 参数值:" + paramValue);
        }
    }

}
