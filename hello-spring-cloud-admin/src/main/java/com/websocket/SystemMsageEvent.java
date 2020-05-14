package com.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tunnelkey.tktim.api.util.RedisUtil;
import com.tunnelkey.tktim.model.QrLogin.QrLoginWSRequest;
import com.tunnelkey.tktim.model.system.NotifyMsgModel;
import com.tunnelkey.tktim.model.websocket.UserRedisSession;
import com.tunnelkey.tktim.model.websocket.onlineResponse;
import com.tunnelkey.tktim.model.websocket.wsMessageType;
import com.tunnelkey.tktim.model.websocket.wsResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

/**
 * @author:zz
 * @description:发送系统通知
 * @date:2019/7/17
 */
@Component
@Data
public class SystemMsageEvent implements IMonitorObserver {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private logonSocket ls;

    public SystemMsageEvent(logonSocket ls) {

        this.setLs(ls);
    }

    @Override
    public void Execute(int commonType, int msgType, Session session, String content) {
        if (commonType != 2)
            return;
        //解析会话内容
        QrLoginWSRequest cmdObj = JSONObject.parseObject(content, QrLoginWSRequest.class);
        int cmdtype = cmdObj.msgType;
        if (cmdtype < 0)
            return;
        if (cmdtype != 0) {
            return;
        }
        //加入缓存
        AddRedis(cmdObj.uuId.toString(), session.getId());
    }

    /// <summary>
    /// 会话加入缓存
    /// </summary>
    /// <param name="content"></param>
    /// <param name="sessionId"></param>
    private void AddRedis(String content, String sessionId) {
        //写入redis
        if (content.isEmpty())
            return;
        String newstr = String.join("|", content, sessionId);
        redisUtil.hset(RedisKeyFinal.key_loginsocket, sessionId, content);
    }

    /// <summary>
    /// 系统通知
    /// </summary>
    public void NotifyMsg(NotifyMsgModel notify) {
        //将要通知的用户内容
        String content = "";
        content = JSON.toJSONString(notify);
        wsResponse wsr = new wsResponse();
        wsr.code = 0;//通知
        wsr.content = content;
        String strmsg = JSON.toJSONString(wsr);
        //获取所有在线用户
        onlineResponse res = new onlineResponse();
        res.code = wsMessageType.totaolOnline.ordinal();
        res.users = new ArrayList<>();
        Map<Object, Object> hmget = redisUtil.hmget(RedisKeyFinal.key_usert2ession);
        if (notify.NotifyMsgType == 1) {
            hmget.forEach((key, value) -> {
                if (value instanceof UserRedisSession) {
                    UserRedisSession userRedisSession = (UserRedisSession) value;
                    if (notify.ReceiveUserId.equals(userRedisSession.userId)||(userRedisSession.role == 1 && userRedisSession.isMaster == 1)) {
                        Session session = this.getSession(userRedisSession.sessionId);
                        if(session!=null){
                            try {
                                session.getBasicRemote().sendText(strmsg);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            });
        }
        if (notify.NotifyMsgType == 2) {
            hmget.forEach((key, value) -> {
                if (value instanceof UserRedisSession) {
                    UserRedisSession userRedisSession = (UserRedisSession) value;
                    if (notify.ReceiveCompanyId.equals(userRedisSession.companyId)||(userRedisSession.role == 1 && userRedisSession.isMaster == 1)) {
                        Session session = this.getSession(userRedisSession.sessionId);
                        if(session!=null){
                            try {
                                session.getBasicRemote().sendText(strmsg);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
        if (notify.NotifyMsgType == 3) {
            hmget.forEach((key, value) -> {
                if (value instanceof UserRedisSession) {
                    UserRedisSession userRedisSession = (UserRedisSession) value;
                    Session session = this.getSession(userRedisSession.sessionId);
                    if(session!=null){
                        try {
                            session.getBasicRemote().sendText(strmsg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    /**
     * 获取用户session
     *
     * @param sid
     * @return
     */
    private Session getSession(String sid) {
        Session mySession = null;
        if (logonSocket.logonSocketSet.size() > 0) {
            Optional<logonSocket> first = logonSocket.logonSocketSet.stream().filter(it -> it.getSession().getId().equals(sid)).findFirst();
            if (first.isPresent()) {
                mySession = first.get().getSession();
            }
        }
        return mySession;
    }

    /**
     * 移除session
     *
     * @param session
     */
    private void removeSession(Session session) {
        Map<Object, Object> hmget = redisUtil.hmget(RedisKeyFinal.key_loginsocket);
        hmget.forEach((key, value) -> {
            if (key.equals(session.getId())) {
                redisUtil.hdel(RedisKeyFinal.key_loginsocket, key);
            }
        });
    }

}
