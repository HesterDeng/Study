package com.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tunnelkey.tktim.api.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @version:（1.0.0.0）
 * @Description: 用户在线统计的WS 建立多个ServerEndpoint他们的浏览器的sessionid是不同的
 * @author: enoch
 * @date: 2019/1/4 9:41
 */
@ServerEndpoint(value = "/ws/logon")
@Component
public class logonSocket extends websocketbase implements IMonitorSubject {
    @Autowired
    private MongoTemplate template;
    // 静态变量,监控的资源对象
    public static int onlineCount = 0;
    // 存放在线用户的链接socket。
    public static CopyOnWriteArrayList<logonSocket> logonSocketSet = new CopyOnWriteArrayList<>();
    // 存放订阅者(观察者)
    public static List<IMonitorObserver> observers = new ArrayList<IMonitorObserver>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        logonSocketSet.add(this);
        System.out.println("logonSocket sid" + session.getId());
        this.NotifyObervers(1, session, "");

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        logonSocketSet.remove(this);
        RedisUtil redisUtil = applicationContext.getBean(RedisUtil.class);
        redisUtil.setRemove(RedisKeyFinal.key_listenonline, session.getId());
        //用户在线记录删除
        this.NotifyObervers(3, session, "");
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            Object messageObj = JSON.parse(message);
            this.NotifyObervers(2, session, message);
        } catch (Exception e) {
            System.out.println("ws:接收用户消息处理异常");
        }
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

    /**
     * 群发自定义消息
     */
    public static void sendInfo(Session session, String message) {//
        for (logonSocket item : logonSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                continue;
            }
        }

    }

    @Override
    public void RegisterObserver(IMonitorObserver o) {
        observers.add(o);
    }

    @Override
    public void RemoveObserver(IMonitorObserver o) {
        observers.remove(o);
    }

    @Override
    public void NotifyObervers(int commonType, Session session, String content) {
        int msgType = -1;
        if (observers.size() > 0) {
            if (commonType == 2) {
                msgType = getMsgType(content);
            }
            for (IMonitorObserver item : observers) {
                item.Execute(commonType, msgType, session, content);
            }
        }
    }

    private int getMsgType(String content) {
        Object parse = JSON.parse(content);
        if (parse instanceof JSONObject) {
            JSONObject parse1 = (JSONObject) parse;
            return Integer.parseInt(parse1.get("msgType").toString());
        }
        return -1;
    }
}
