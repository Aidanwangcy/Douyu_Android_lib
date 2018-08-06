package com.xbw.douyu;


import android.util.Log;

import com.xbw.douyu.constants.DouYuConstants;
import com.xbw.douyu.constants.MsgType;
import com.xbw.douyu.entity.BaseMsg;
import com.xbw.douyu.entity.ChatMsg;
import com.xbw.douyu.entity.DgbMsg;
import com.xbw.douyu.entity.ErrorMsg;
import com.xbw.douyu.entity.GgbbMsg;
import com.xbw.douyu.entity.MsgEntity;
import com.xbw.douyu.entity.SpbcMsg;
import com.xbw.douyu.entity.SsdMsg;
import com.xbw.douyu.entity.UenterMsg;
import com.xbw.douyu.exceptions.DouYuSDKException;
import com.xbw.douyu.util.DataUtil;
import com.xbw.douyu.util.STTUtil;
import com.xbw.douyu.util.UUIDUtil;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * 功能描述：斗鱼SDK
 *
 * @auther: xubowen
 * @date: 2018-08-06 10:28:48
 * 修改日志:
 */
public class DouYuClient {
    private String host;    //API Host
    private int port;       //API 端口
    private String roomId;  //房间ID(房间号)
    private String groupId = DouYuConstants.MASSIVE_GID;    //分组ID

    private Socket socket;                  //通讯socket对象
    private Thread dataSyncThread;          //异步读取消息线程
    private Boolean isExitMark = false;     //退出标记
    private MessageHandler messageHandler;
    private List<MessageListener> messageListenerList = new ArrayList<>();


    public DouYuClient(String host, int port, String roomId) {
        this.host = host;
        this.port = port;
        this.roomId = roomId;
        //初始化
        this.init();
    }

    /**
     * 初始化Client
     */
    private void init() {
        Log.d("xbw12138","初始化斗鱼SDK_Client...");
        this.connect();
    }

    /**
     * 打开socket连接
     */
    private void connect() {
        try {
            Log.d("xbw12138","从服务器("+host+":"+port+")获取弹幕服务器数据");
            socket = new Socket(host, port);
            messageHandler = new MessageHandler(socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 登入斗鱼弹幕服务器
     */
    public DouYuClient login() {
        Log.d("xbw12138","登录房间 "+roomId);
        String content = String.format(DouYuApi.LOGIN_REQ, roomId);
        messageHandler.send(content);
        return this;
    }

    /**
     * 加入房间分组并接收房间消息
     *
     * @return
     */
    private DouYuClient joinGroup(String groupId) {
        if (DouYuConstants.MASSIVE_GID.equals(groupId)) {
            Log.d("xbw12138","开启海量弹幕接收模式");
        } else {
            Log.d("xbw12138","关闭海量弹幕接收模式");
        }
        Log.d("xbw12138","加入分组("+groupId+")并开始接收消息");
        String content = String.format(DouYuApi.JOIN_GROUP, roomId, groupId);
        messageHandler.send(content);
        return this;
    }

    /**
     * 注册消息监听器
     *
     * @param messageListener
     * @return
     */
    public DouYuClient registerMessageListener(MessageListener messageListener) {
        Log.d("xbw12138","注册消息监听器:"+messageListener.getClass());
        this.messageListenerList.add(messageListener);
        return this;
    }

    /**
     * 开始同步并接收房间消息
     */
    public DouYuClient sync() {
        //加入海量弹幕分组，接收所有消息
        this.joinGroup(groupId);

        //开启异步线程接收房间消息
        dataSyncThread = new Thread(new Runnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                while (true) {
                    //判断是否退出线程
                    if (isExitMark == true) {
                        break;
                    }

                    //读取弹幕消息
                    byte[] bytes = messageHandler.read();
                    String msg = new String(Arrays.copyOfRange(bytes, 8, bytes.length));
                    //获取消息类型
                    String msgType = DataUtil.getMsgType(msg);
                    if (msgType == null) {
                        Log.d("xbw12138","获取消息类型失败，消息:"+msg);
                        continue;
                    }

                    //封装基础消息对象
                    BaseMsg msgBase = new BaseMsg();
                    msgBase.setUuid(UUIDUtil.simpleUUID());
                    msgBase.setType(msgType);
                    msgBase.setMessage(msg);

                    //根据不同的消息类型 序列化不同的 实体对象
                    MsgEntity entity = null;
                    if (MsgType.CHAT_MSG.equals(msgType)) {
                        entity = STTUtil.toBean(msg, ChatMsg.class);
                    } else if (MsgType.DGB.equals(msgType)) {
                        entity = STTUtil.toBean(msg, DgbMsg.class);
                    } else if (MsgType.GGBB.equals(msgType)) {
                        entity = STTUtil.toBean(msg, GgbbMsg.class);
                    } else if (MsgType.SPBC.equals(msgType)) {
                        entity = STTUtil.toBean(msg, SpbcMsg.class);
                    } else if (MsgType.SSD.equals(msgType)) {
                        entity = STTUtil.toBean(msg, SsdMsg.class);
                    } else if (MsgType.UENTER.equals(msgType)) {
                        entity = STTUtil.toBean(msg, UenterMsg.class);
                    } else if (MsgType.ERROR.equals(msgType)) {
                        entity = STTUtil.toBean(msg, ErrorMsg.class);
                    }
                    if (entity != null) {
                        entity.setMessage(msg);
                        entity.setUuid(msgBase.getUuid());
                    }
                    //消息监听器处理
                    for (MessageListener messageListener : messageListenerList) {
                        try {
                            //基础消息监听器处理
                            if (messageListener.getMsgClazz() == BaseMsg.class) {
                                messageListener.read(msgBase);
                            }
                            //指定类型消息监听器处理
                            else if (entity != null && messageListener.getMsgClazz() == entity.getClass()) {
                                messageListener.read(entity);
                            }
                            //String消息监听器处理
                            else if (messageListener.getMsgClazz() == String.class) {
                                messageListener.read(msg);
                            }
                        } catch (Exception e) {
                            Log.d("xbw12138","消息处理出现异常:"+e);
                        }
                    }

                    //发送心跳消息保持通道
                    long end = System.currentTimeMillis();
                    if (end - start > 30000) {
                        doKeepLive();
                        start = System.currentTimeMillis();
                    }
                    //休眠1毫秒
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new DouYuSDKException(e);
                    }
                }

                //客户端关闭，断开socket通道
                messageHandler.close();
                Log.d("xbw12138","斗鱼弹幕SDK客户端已成功退出");
            }
        });
        dataSyncThread.start();
        return this;
    }

    /**
     * 发送心跳消息，保持通道
     */
    public void doKeepLive() {
        String content = String.format(DouYuApi.KEEP_LIVE);
        Log.d("xbw12138","发送心跳信息，保持通道中...");
        messageHandler.send(content);
    }

    /**
     * 发送登出消息，用于退出
     */
    public void logout(){
        String content = String.format(DouYuApi.LOGOUT);
        Log.d("xbw12138","发送登出消息中...");
        messageHandler.send(content);
    }

    public void exit() {
        logout();
        isExitMark = true;
        Log.d("xbw12138","斗鱼弹幕SDK客户端正在退出中...");
    }
}
