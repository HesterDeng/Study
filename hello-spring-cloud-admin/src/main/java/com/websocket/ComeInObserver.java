package com.websocket;

import com.alibaba.fastjson.JSON;
import com.tunnelkey.tktim.api.util.RedisUtil;
import com.tunnelkey.tktim.business.system.ISystemSettingService;
import com.tunnelkey.tktim.model.PageModel;
import com.tunnelkey.tktim.model.common.UserCompanyModel;
import com.tunnelkey.tktim.model.common.UserInfoModel;
import com.tunnelkey.tktim.model.request.NotifyMsgRequest;
import com.tunnelkey.tktim.model.system.NotifyMsgModel;
import com.tunnelkey.tktim.model.websocket.UserRedisSession;
import com.tunnelkey.tktim.model.websocket.logonRequest;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.websocket.Session;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @version:（1.0.0.0）
 * @Description: 用户登录成功发送信息到ws服务器
 * @author: enoch
 * @date: 2019/1/7 10:14
 */
@Component
@Data
public class ComeInObserver implements IMonitorObserver {
    @Autowired
    private MongoTemplate template;
    @Autowired
    ISystemSettingService systemSettingService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private LogonLogOffEvent logonLogOffEvent;
    private logonSocket ls;

    public ComeInObserver(logonSocket ls) {
        this.setLs(ls);
        ls.RegisterObserver(this);
    }

    public void addUserSession(logonRequest request, Session session) {
        UserRedisSession userSession = new UserRedisSession();
        BeanUtils.copyProperties(request, userSession);
        UserCompanyModel userCompanyModel = template.findOne(Query.query(Criteria.where("UserId").is(request.userId)), UserCompanyModel.class);
        if (!ObjectUtils.isEmpty(userCompanyModel)) {
            userSession.setCompanyId(userCompanyModel.CompanyId);
        }
        UserInfoModel userInfoModel = template.findOne(Query.query(Criteria.where("_id").is(request.userId)), UserInfoModel.class);
        if (!ObjectUtils.isEmpty(userInfoModel)) {
            userSession.setRole(userInfoModel.Role);
            userSession.setIsMaster(userInfoModel.IsMaster);
        }
        userSession.setSessionId(session.getId());
        redisUtil.hset(RedisKeyFinal.key_usert2ession, request.getUserId().toString(), userSession);
        sendSystemMsg(userSession, session);
    }

    @Override
    public void Execute(int commonType, int msgType, Session session, String content) {
        if (commonType != 2 || msgType != 0)
            return;
        logonRequest logonRequest = JSON.parseObject(content, logonRequest.class);
        addUserSession(logonRequest, session);
        logonLogOffEvent.logon();
    }

    private void sendSystemMsg(UserRedisSession userSession, Session session) {
        NotifyMsgRequest notifyMsgRequest = new NotifyMsgRequest();
        notifyMsgRequest.endTime = LocalDateTime.now();
        PageModel<NotifyMsgModel> notifyList = systemSettingService.findNotifyList(notifyMsgRequest);
        if (notifyList.DataList.size() < 1 || null == userSession.userId || null == session) {
            return;
        }
        notifyList.DataList.forEach(notifyMsgModel -> {
            String msg = JSON.toJSONString(notifyMsgModel);
            if (notifyMsgModel.NotifyMsgType == 3) {
                if (session != null) {
                    try {
                        session.getBasicRemote().sendText(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (notifyMsgModel.NotifyMsgType == 2) {
                if (notifyMsgModel.ReceiveCompanyId.equals(userSession.companyId) || (userSession.role == 1 && userSession.isMaster == 1)) {
                    if (session != null) {
                        try {
                            session.getBasicRemote().sendText(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
            if (notifyMsgModel.NotifyMsgType == 1) {
                if (notifyMsgModel.ReceiveUserId.equals(userSession.userId) || (userSession.role == 1 && userSession.isMaster == 1)) {
                    if (session != null) {
                        try {
                            session.getBasicRemote().sendText(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });
    }
}
