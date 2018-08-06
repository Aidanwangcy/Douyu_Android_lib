package com.xbw.douyu.listener;


import android.util.Log;

import com.xbw.douyu.MessageListener;
import com.xbw.douyu.entity.ChatMsg;

import java.util.logging.Logger;

/**
 * 功能描述：默认ChatMsg消息监听处理器
 *
 * @auther: xubowen
 * @date: 2018-08-06 10:28:48
 * 修改日志:
 */
public class DefaultChatMsgListener extends MessageListener<ChatMsg> {

    @Override
    public void read(ChatMsg message) {
        Log.d("xbw12138",message.toChatStr());
    }
}
