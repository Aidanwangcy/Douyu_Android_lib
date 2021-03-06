package com.xbw.douyu;


import android.util.Log;

import com.xbw.douyu.exceptions.DouYuSDKException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * 功能描述：消息处理助手
 *
 * @auther: xubowen
 * @date: 2018-08-06 10:28:48
 * 修改日志:
 */
public class MessageHandler {
    private Socket socket;

    public MessageHandler(Socket socket) {
        this.socket = socket;
    }

    /**
     * 发送消息
     *
     * @param content
     */
    public void send(String content) {
        try {
            Message message = new Message(content);
            OutputStream out = socket.getOutputStream();
            out.write(message.getBytes());
            out.flush();
        } catch (IOException e) {
            throw new DouYuSDKException(e);
        }
    }

    /**
     * 读取消息
     *
     * @return
     */
    public byte[] read(){
        try {
            InputStream inputStream = socket.getInputStream();
            //下条信息的长度
            int contentLen = 0;

            //读取前4个字节，得到数据长度
            for (int i = 0; i < 4; i++) {
                int tmp = inputStream.read();
                contentLen += tmp * Math.pow(16, 2 * i);
            }

            int len = 0;
            int readLen = 0;
            byte[] bytes = new byte[contentLen];
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            while ((len = inputStream.read(bytes, 0, contentLen - readLen)) != -1) {
                byteArray.write(bytes, 0, len);
                readLen += len;
                if (readLen == contentLen) {
                    break;
                }
            }

            return byteArray.toByteArray();
        } catch (IOException e) {
            throw new DouYuSDKException(e);
        }
    }

    /**
     * 关闭socket通道
     *
     * @throws IOException
     */
    public void close(){
        try {
            socket.close();
        } catch (IOException e) {
            Log.d("xbw12138","socket通道关闭异常"+e);
        }
    }
}
