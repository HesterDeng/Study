const wsDomain = 'ws://192.168.1.120:8021';
export default class TMMSSocket {
    constructor(needReconnect = true){
        this.needReconnect = needReconnect;
    }

    init() {
        let me = this;
        axios.get('/uuid').then(function (response) {
            me.setSocket(response.data);
        }).catch(function () {
            setTimeout(function () {
                me.init();
            },10000);
        });
    }

    setSocket(uuid){
        let me = this;
        this.uid = uuid;
        this.url = "/ws/tmms/";
        me.ws = new WebSocket(wsDomain + me.url + me.uid);
        me.ws.onopen = function(){
            if (me.getInfoFunc){
                let info = me.getInfoFunc();
                me.sendTypeAndId('pointId',info.point ? info.point.pointId : null);
                me.sendTypeAndId('tunnelId',info.tunnel ? info.tunnel.id : null);
            }
        };

        me.ws.onmessage = function (message) {
            me.update(message);
        };
        me.ws.onclose = function () {
            if (me.reconnect || !me.needReconnect) return;//当正在重连或者不需要重连时关闭socket
            me.reconnect = true;
            setTimeout(function () {
                me.ws = null;
                me.init();
                me.reconnect = false;
            },2000);
        };
        me.ws.onerror = function () {
            me.ws.close();
        };
        window.addEventListener('beforeunload', function () {
            me.close();
        });
    }

    close(){
        this.needReconnect = false;
        if (this.ws){
            this.ws.close();
        }
    }

    update(message) {
        let me = this;
        let obj = message && message.data !== '' ? JSON.parse(message.data) : null;
        if (obj){
            let methods = [];
            //隧道层面的数据更新
            if (obj.type === 1){
                methods = me.tunnelUpdateMethods;
            }
            //测点层面的数据更新
            if (obj.type === 3){
                methods = me.pointUpdateMethods;
            }
            methods.forEach(method=>{
                method();
            })
        }
    }

    sendTypeAndId(type,id) {
        let me = this;
        if (!type){
            return;
        }
        if (me.ws.readyState === me.ws.CONNECTING) {
            setTimeout(function () {
                me.sendTypeAndId(type,id);
            }, 1000);
        } else if (me.ws.readyState === me.ws.CLOSED || me.ws.readyState === me.ws.CLOSING) {
            me.init();
            me.sendTypeAndId(type,id);
        } else {
            me.ws.send(`{"${type}":"${id}"}`);
        }

    }

    setInfoFunc(func){
        this.getInfoFunc = func;
    }

    setUpdateFunc(updateFunc){
        this.updateFunc = updateFunc;
    }

    addPointUpdateMethod(method){
        if (!this.pointUpdateMethods) {
            this.pointUpdateMethods = [];
        }
        this.pointUpdateMethods.push(method);
    }

    addTunnelUpdateMethod(method){
        if (!this.tunnelUpdateMethods) {
            this.tunnelUpdateMethods = [];
        }
        this.tunnelUpdateMethods.push(method);
    }
}
