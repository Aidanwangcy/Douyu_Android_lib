package com.xbw.douyu;

import java.lang.reflect.ParameterizedType;

/**
 * 功能描述：消息监听器
 *
 * @auther: xubowen
 * @date: 2018-08-06 10:28:48
 * 修改日志:
 */
public abstract class MessageListener<T> {

    private Class<T> msgClazz;

    public MessageListener() {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.msgClazz = (Class<T>) type.getActualTypeArguments()[0];
    }

    /**
     * 消息读取
     */
    public abstract void read(T message);

    public Class<T> getMsgClazz() {
        return msgClazz;
    }
}
