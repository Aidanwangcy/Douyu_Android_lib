package com.xbw.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.xbw.douyu.DouYuClient;
import com.xbw.douyu.MessageListener;
import com.xbw.douyu.constants.DouYuConstants;
import com.xbw.douyu.entity.ChatMsg;

public class MainActivity extends AppCompatActivity {

    private DouYuClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new Runnable(){
            @Override
            public void run() {
                getDouyuDanmu("750240");//斗鱼房间号
            }
        }).start();
    }
    public void getDouyuDanmu(String roomid) {
        client = new DouYuClient(DouYuConstants.SOCKET_HOST, DouYuConstants.SOCKET_PORT, roomid);
        client.registerMessageListener(new MessageListener<ChatMsg>() {
            @Override
            public void read(ChatMsg message) {
                Log.d("xbw12138", message.toChatStr());
            }
        });
        client.login();
        client.sync();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Thread(new Runnable(){
            @Override
            public void run() {
                client.exit();
            }
        }).start();
    }

}
