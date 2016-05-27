package com.httpurlconnectiontest;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Button startServerBt;
    private Button startClientBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startServerBt = (Button) findViewById(R.id.start_server);
        startClientBt = (Button) findViewById(R.id.start_client);

        startServerBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SimpleHttpServer().start();
            }
        });

        startClientBt.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {

                                                 new Thread(new Runnable() {
                                                     @Override
                                                     public void run() {
                                                         try {
                                                             sendPost();
                                                         } catch (Exception e) {
                                                             Log.d(TAG, e.getMessage());
                                                             e.printStackTrace();
                                                         }
                                                     }
                                                 }
                                                 ).start();
                                             }
                                         }
        );
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void sendPost() throws IOException {
        String urlParameters = "a=3&b=1&c=2";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = "http://127.0.0.1:8000";
        URL url = new URL(request);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        conn.setUseCaches(false);
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.write(postData);
        Log.d(TAG, "write");
        wr.close();
    }
}
