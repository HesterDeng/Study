package com.websocket;

import javax.websocket.Session;

/**
 * @version:（1.0.0.0）
 * @Description: 观察者
 * @author: enoch
 * @date: 2019/1/4 13:28
 */
public interface IMonitorObserver {
    /// <summary>
    /// 观察到变化执行的操作
    /// </summary>
    /// <param name="commonType">命令类型1 进入 2会话 3 离开</param>
    /// <param name="session">客户端请求的session</param>
    /// <param name="content">客户端发送的消息</param>
    void Execute(int commonType, int msgType, Session session, String content);
}
