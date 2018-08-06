package com.xbw.douyu;

/**
 *
 * 功能描述：斗鱼弹幕服务API
 * 基于(斗鱼弹幕服务器第三方接入协议v1.6.2)开发
 *
 * @auther: xubowen
 * @date: 2018-08-06 10:28:48
 * 修改日志:
 *      
 */
public class DouYuApi {
    public static String LOGIN_REQ = "type@=loginreq/roomid@=%s/";
    public static String JOIN_GROUP = "type@=joingroup/rid@=%s/gid@=%s/";
    public static String KEEP_LIVE = "type@=mrkl/";
    public static String LOGOUT = "type@=logout/";
}
