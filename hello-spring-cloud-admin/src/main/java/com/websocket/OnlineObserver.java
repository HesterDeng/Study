package com.websocket;

import com.tunnelkey.tktim.api.util.RedisUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.websocket.Session;

/**
 * @version:（1.0.0.0）
 * @Description: 记录谁(浏览器)订阅了监听在线用户数量的接口
 * @author: enoch
 * @date: 2019/1/4 13:28
 */
@Component
@DependsOn("logonSocket")
@Data
public class OnlineObserver implements IMonitorObserver {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private LogonLogOffEvent logonLogOffEvent;

    private logonSocket ls;

    public OnlineObserver(logonSocket ls) {
        this.setLs(ls);
        ls.RegisterObserver(this);
    }

    public void addSession(Session session) {
        //set集合
        String sid=session.getId();
        //redis cmd:smembers tim_listenonline
        redisUtil.sSet(RedisKeyFinal.key_listenonline, sid);
    }
    /**
     * @param commonType
     * @param msgType    必须等于1 ：表示浏览器订阅统计人数消息
     * @param session
     * @param content
     */
    @Override
    public void Execute(int commonType, int msgType, Session session, String content) {
        if (commonType != 2 || msgType != 1)
            return;
        addSession(session);
        //马上发送统计信息
        logonLogOffEvent.logon();
    }
}
