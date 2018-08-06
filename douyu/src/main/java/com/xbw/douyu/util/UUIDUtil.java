package com.xbw.douyu.util;

import java.util.UUID;

/**
 *
 * 功能描述：UUID工具类
 * @auther: xubowen
 * @date: 2018-08-06 10:28:48
 * 修改日志:
 *      
 */
public class UUIDUtil {
    /**
     * @return 随机UUID
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线
     *
     * @return 简化的UUID，去掉了横线
     * @since 3.2.2
     */
    public static String simpleUUID() {
        return randomUUID().replace("-", "");
    }
}
