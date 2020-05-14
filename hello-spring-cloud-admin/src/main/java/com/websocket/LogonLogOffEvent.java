package com.websocket;

import com.alibaba.fastjson.JSON;
import com.tunnelkey.tktim.api.util.RedisUtil;
import com.tunnelkey.tktim.model.websocket.UserRedisSession;
import com.tunnelkey.tktim.model.websocket.logonRequest;
import com.tunnelkey.tktim.model.websocket.onlineResponse;
import com.tunnelkey.tktim.model.websocket.wsMessageType;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.*;

/**
 * @version:（1.0.0.0）
 * @Description: （对类进行功能描述）
 * @author: enoch
 * @date: 2019/1/7 13:15
 */
@Component
@Data
public class LogonLogOffEvent implements IMonitorObserver {
    @Autowired
    private RedisUtil redisUtil;

    private logonSocket ls;

    public LogonLogOffEvent(logonSocket ls) {
        this.setLs(ls);
       // ls.RegisterObserver(this);
    }

    @Override
    public void Execute(int commonType, int msgType, Session session, String content) {
//        if (commonType == 1)
//            logon();
        if (commonType == 3)
            logoff();
    }
    /**
     * 输出在线用户信息
     */
    public void logon() {
        Set<Object> objects = redisUtil.sGet(RedisKeyFinal.key_listenonline);
        if (objects != null && objects.size() > 0) {
            onlineResponse  res=new onlineResponse();
            res.code= wsMessageType.totaolOnline.ordinal();
            res.users=new ArrayList<>();
            Map<Object, Object> hmget = redisUtil.hmget(RedisKeyFinal.key_usert2ession);
            hmget.forEach((key, value) -> {
                if (value instanceof UserRedisSession) {
                    UserRedisSession userRedisSession = (UserRedisSession) value;
                    logonRequest user=new logonRequest();
                    BeanUtils.copyProperties(userRedisSession,user);
                    res.users.add(user);
                }
            });
            if(res.users.size()>0) {
                String join = JSON.toJSONString(res);
                objects.forEach(it -> {
                    Session session = this.getSession(it.toString());
                    if(session!=null) {
                        try {
                            session.getBasicRemote().sendText(join);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }
    private Session getSession(String sid) {
        Session mySession = null;
        if(logonSocket.logonSocketSet.size()>0){
            Optional<logonSocket> first = logonSocket.logonSocketSet.stream().filter(it -> it.getSession().getId().equals(sid)).findFirst();
            if(first.isPresent()){
                mySession=first.get().getSession();
            }
        }
        return mySession;
    }
    /**
     * 输出离线用户信息
     */
    public void logoff() {
        logon();
    }
}
