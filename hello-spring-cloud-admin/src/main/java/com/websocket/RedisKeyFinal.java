package com.websocket;

/**
 * @version:（1.0.0.0）
 * @Description: 保存在redis中的key的常量
 * @author: enoch
 * @date: 2019/1/7 9:49
 */
public class RedisKeyFinal {
    /**
     * 在线用户数量
     */
    public static final String key_onlineusers = "tim_onlinecount";
    /**
     * 存放用户的客服端信息
     */
    public static final String key_session = "tim_sessions";
    /**
     * 存放用户与session的对应信息
     */
    public static final String key_usert2ession = "tim_user2session";
    /**
     * 记录那些浏览器订阅了监听用户数量信息
     */
    public static final String key_listenonline = "tim_listenonline";

    /**
     * 用户打开登录界面时建立socket链接
     */
    public static final String key_loginsocket = "tim_loginsocket";
}
