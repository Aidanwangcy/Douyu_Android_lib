package com.xbw.douyu.listener;


import android.util.Log;

import com.xbw.douyu.MessageListener;
import com.xbw.douyu.entity.BaseMsg;

/**
 *
 * 功能描述：默认的BaseMsg消息监听器
 *
 * @auther: xubowen
 * @date: 2018-08-06 10:28:48
 * 修改日志:
 *      
 */
public class DefaultBaseMsgListener extends MessageListener<BaseMsg> {
    @Override
    public void read(BaseMsg message) {
        Log.d("xbw12138",message.toString());
    }
}
