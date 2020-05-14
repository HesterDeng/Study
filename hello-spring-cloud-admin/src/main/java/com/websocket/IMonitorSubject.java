package com.websocket;

import javax.websocket.Session;

/**
 * @version:（1.0.0.0）
 * @Description: 被观察者
 * @author: enoch
 * @date: 2019/1/4 13:28
 */
public interface IMonitorSubject {

    public void RegisterObserver(IMonitorObserver o);//注册观察者

    public void RemoveObserver(IMonitorObserver o);//删除观察者

    public void NotifyObervers(int commonType, Session session, String content);//通知观察者
}
