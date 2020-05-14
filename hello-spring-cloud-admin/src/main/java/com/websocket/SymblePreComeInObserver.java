package com.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tunnelkey.tktim.api.util.RedisUtil;
import com.tunnelkey.tktim.model.QrLogin.LoginNotifyMessage;
import com.tunnelkey.tktim.model.QrLogin.QrLoginWSRequest;
import com.tunnelkey.tktim.model.websocket.wsResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * @version:（1.0.0.0）
 * @Description: 表示用户待登录系统, 已给服务器发起会话
 * @author: yh
 * @date: 2019/2/15 16:55
 */
@Component
@Data
public class SymblePreComeInObserver implements IMonitorObserver {
    @Autowired
    private RedisUtil redisUtil;
    private logunSocket ls;

    public SymblePreComeInObserver(logunSocket ls) {
        this.setLs(ls);
        ls.RegisterObserver(this);
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
    public void NotifyMsg(LoginNotifyMessage notify) {
        //将要通知的用户内容
        String content = "";
        content = JSON.toJSONString(notify);
        wsResponse wsr = new wsResponse();
        wsr.code = 0;//通知
        wsr.content = content;
        String strmsg = JSON.toJSONString(wsr);
        //扫码登录的用户
        Map<Object, Object> hmget = redisUtil.hmget(RedisKeyFinal.key_loginsocket);
        if (notify != null) {
            String receiveList = notify.uuid;
            hmget.forEach((key, value) -> {
                String sessionid = key.toString();
                Session session = this.getSession(sessionid);
                if (session != null) {
                    if (session.isOpen()) {
                        if (!receiveList.isEmpty() && receiveList.equals(value)) {
                            try {
                                session.getBasicRemote().sendText(strmsg);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    this.removeSession(session);
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
        if (logunSocket.logunSocketSet.size() > 0) {
            Optional<logunSocket> first = logunSocket.logunSocketSet.stream().filter(it -> it.getSession().getId().equals(sid)).findFirst();
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
