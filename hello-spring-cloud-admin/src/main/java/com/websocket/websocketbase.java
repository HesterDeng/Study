package com.websocket;

import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;

/**
 * @version:（1.0.0.0）
 * @Description: ws交互的基类，封装常用的方法
 * @author: enoch
 * @date: 2019/1/4 11:06
 */
@Component
@Data
public abstract class websocketbase {
    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    public Session session;

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
        // this.session.getAsyncRemote().sendText(message);异步发送
    }
    public static ApplicationContext applicationContext;

    /**
     * 必须在springboot启动的时候加入main方法中调用
     * @param context
     */
    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

}
