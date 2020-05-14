package com.websocket;

import com.tunnelkey.tktim.api.util.RedisUtil;
import com.tunnelkey.tktim.model.websocket.UserRedisSession;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.util.Map;

/**
 * @version:（1.0.0.0）
 * @Description: 用户登录成功发送信息到ws服务器
 * @author: enoch
 * @date: 2019/1/7 10:14
 */
@Component
@Data
public class ComeOutObserver implements IMonitorObserver {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private LogonLogOffEvent logonLogOffEvent;
    private logonSocket ls;

    public ComeOutObserver(logonSocket ls) {
        this.setLs(ls);
        ls.RegisterObserver(this);
    }
    public void removeUserSession(Session session) {
        Map<Object, Object> hmget = redisUtil.hmget(RedisKeyFinal.key_usert2ession);
        hmget.forEach((key, value) -> {
            if (value instanceof UserRedisSession) {
                UserRedisSession userRedisSession = (UserRedisSession) value;
                if(userRedisSession.getSessionId().equals(session.getId())){
                    redisUtil.hdel(RedisKeyFinal.key_usert2ession,userRedisSession.getUserId().toString());
                }
            }
        });
    }
    @Override
    public void Execute(int commonType, int msgType, Session session, String content) {
        if (commonType != 3 )
            return;
        removeUserSession(session);
        logonLogOffEvent.logon();
    }
}
