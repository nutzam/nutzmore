package org.nutz.plugins.ngrok.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.SocketFactory;

import org.nutz.http.Http;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.ngrok.common.NgrokAgent;
import org.nutz.plugins.ngrok.common.NgrokMsg;
import org.nutz.plugins.ngrok.common.PipedStreamThread;
import org.nutz.plugins.ngrok.common.StatusProvider;

public class NgrokClient implements Runnable, StatusProvider<Integer> {

    protected static final Log log = Logs.get();
    /** ClientId, 根据AuthResp响应从服务器获取 */
    public String id;
    /**
     * 服务器域名
     */
    public String srv_host = "wendal.cn";
    /**
     * 服务器端口号
     */
    public int srv_port = 4443;
    /**
     * 本地ip,默认127.0.0.1
     */
    public String to_host = "127.0.0.1";
    /**
     * 本地端口,默认8080
     */
    public int to_port = 8080;
    /**
     * 授权码,必填
     */
    public String auth_token;
    /**
     * 缓存区大小,默认64kb
     */
    public int bufSize = 64 * 1024;

    /**
     * 隧道协议, 默认是http, 如需设置多个,用加号连起来, 例如 http+https+tcp
     */
    public String protocol = "http";

    /**
     * tcp隧道的外网端口号. http/https时无效
     */
    public int remote_port;

    /**
     * Http/Https协议时的简单鉴权,通常不需要
     */
    public String http_auth = "";

    /**
     * Http/Https协议时的CNAME,通常不需要
     */
    public String hostname = "";

    /**
     * 指定子域名, 通过不支持
     */
    public String subdomain = "";
    
    /**
     * 是否启用gzip支持, 只对NutzNgrok服务器有效果
     */
    public boolean gzip;
    /**
     * ssl链接的工厂
     */
    public transient SocketFactory socketFactory;
    /**
     * 控制输出流
     */
    protected transient OutputStream ctlOut;
    /**
     * 控制输入流
     */
    protected transient InputStream ctlIn;
    /**
     * 0代表ready,1代表运行中,2代表异常退出,3代表已关闭
     */
    public int status = 0;

    /**
     * 最后出现的信息
     */
    public String error;
    /**
     * 请求id与映射地址的响应
     */
    public Map<String, NgrokMsg> reqIdMap = new HashMap<String, NgrokMsg>();
    /**
     * 线程池
     */
    protected transient ExecutorService executorService;

    protected transient Socket ctlSocket;

    protected ConcurrentHashMap<String, ProxyConn> pmap = new ConcurrentHashMap<String, NgrokClient.ProxyConn>();

    public void start() {
        if (auth_token == null) {
            log.error("must set auth_token!!!");
            return;
        }
        // 检查一下线程池是否设置了,没有的话就建个默认的
        if (executorService == null)
            executorService = Executors.newCachedThreadPool();
        // 如果没设置SSLSocketFactory,那就取系统默认的
        if (socketFactory == null)
            try {
                socketFactory = Http.nopSSLSocketFactory();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        // 把自身提交到线程池去执行呗
        executorService.submit(this);
    }

    public void run() {
        status = 1;
        while (status == 1) {
            _run();
            if (status != 1)
                break;
            Lang.quiteSleep(30000);
        }
    }
    
    public void _run() {
        try {
            reqIdMap.clear();
            // 建一个通往服务器的控制Socket
            ctlSocket = newSocket2Server();
            ctlIn = ctlSocket.getInputStream();
            ctlOut = ctlSocket.getOutputStream();
            // 发送登录信息
            NgrokMsg.auth(auth_token, "", "windows", "386", "", gzip).write(ctlOut);
            // 接受登录信息
            NgrokMsg authResp = NgrokAgent.readMsg(ctlIn);
            String error = authResp.getString("Error");
            if (!Strings.isBlank(error)) { // 发现错误, 只能退出了
                log.error("auth fail : " + error);
                this.error = "auth fail : " + error;
                return;
            }
            id = authResp.getString("ClientId");
            // 发送通道请求
            String reqId = R.UU32();
            for (String prot : protocol.split("[\\+]")) {
                if (prot.startsWith("http"))
                    NgrokMsg.reqTunnel(reqId,
                                                           hostname,
                                                           prot,
                                                           subdomain,
                                                           http_auth,
                                                           0).write(ctlOut);
                else if (prot.startsWith("tcp"))
                    NgrokMsg.reqTunnel(reqId, "", prot, "", "", remote_port).write(ctlOut);
                else
                    log.warn("unkown protocol=" + prot);
            }

            // 启动心跳线程
            executorService.submit(new PingThread());
            handle();
        }
        catch (Exception e) {
            log.debug("something happen", e);
        }
        finally {
            Streams.safeClose(ctlSocket);
        }
    }

    protected void handle() {
        while (status == 1) {
            try {
                // 看看服务器想干啥
                NgrokMsg msg = NgrokAgent.readMsg(ctlIn);
                String type = msg.getType();
                // 服务器要求我们发送新的代理链接
                if ("ReqProxy".equals(type)) {
                    ProxyConn pc = new ProxyConn();
                    pmap.put(pc.pcid, pc);
                    executorService.submit(pc);
                }
                // 服务器发心跳了!!!, 需要回应pong
                else if ("Ping".equals(type)) {
                    NgrokMsg.pong().write(ctlOut);
                }
                // 服务器响应ReqTunnel,但有可能已经注册过,那么只能直接退出了
                else if ("NewTunnel".equals(type)) {
                    if (Strings.isBlank(msg.getString("Error"))) {
                        reqIdMap.put(msg.getString("ReqId"), msg);
                        log.debugf("ReqId=%s URL=%s", msg.getString("ReqId"), msg.getString("Url"));
                    } else {
                        log.error(msg.getString("Error"));
                        this.error = msg.getString("Error");
                        break;
                    }
                }
                // 服务器对心跳线程ping的回应,可以忽略
                else if ("Pong".equals(type)) {
                    // nop
                } else {
                    log.info("unknown type=" + msg.getString("Type"));
                }
            }
            catch (IOException e) {
                if (status == 1)
                    log.debug("io error, main contrl connection break", e);
                else
                    log.debug("main contrl connection close.");
                break;
            }
        }
    }

    /**
     * 创建一个通往服务器的Socket
     */
    protected Socket newSocket2Server() throws UnknownHostException, IOException {
        return socketFactory.createSocket(srv_host, srv_port);
    }

    /**
     * 创建一个通往本地端口的Socket
     */
    public Socket newSocket2Local() throws UnknownHostException, IOException {
        return new Socket(to_host, to_port);
    }

    /**
     * 关闭自身
     */
    public void stop() {
        status = 3;
        reqIdMap.clear();
        Streams.safeClose(ctlSocket); // 强制关闭控制链接,这样就触发其他链接全部被服务器中断,然后所有线程得以退出
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
            executorService = null;
        }
        List<ProxyConn> list = new ArrayList<ProxyConn>(pmap.values());
        pmap.clear();
        for (ProxyConn pc : list) {
            Streams.safeClose(pc.toLoc);
            Streams.safeClose(pc.toSrv);
        }
    }

    /**
     * 获取自身id,debug用的
     * 
     * @return ClientId
     */
    public String getId() {
        return id;
    }

    /**
     * 开启一个代理请求
     */
    protected class ProxyConn implements Callable<Object> {
        public Socket toSrv = null;
        public Socket toLoc = null;
        public String pcid = R.UU32();

        @Override
        public Object call() throws Exception {
            try {
                // 首先,建立一条通道
                toSrv = newSocket2Server();
                // 需要等服务器响应,可能会很久
                //toSrv.setSoTimeout(60 * 1000); // 一小时
                // 取出该通道的输入输出流备用
                OutputStream srvOut = toSrv.getOutputStream();
                InputStream srvIn = toSrv.getInputStream();
                // 发起注册通道的请求
                NgrokMsg.regProxy(getId()).write(srvOut);
                // 等待服务器响应StartProxy
                NgrokMsg msg = NgrokAgent.readMsg(srvIn);
                // 如果真的响应了StartProxy,开始桥接Socket
                if ("StartProxy".equals(msg.getType())) {
                    try {
                        if (log.isDebugEnabled())
                            log.debug("start socket pipe ...");
                        toLoc = newSocket2Local();
                        try {
                            // 服务器-->本地
                            PipedStreamThread srv2loc = new PipedStreamThread("srv2loc",
                                                                              srvIn,
                                                                              toLoc.getOutputStream(),
                                                                              bufSize);
                            // 本地-->服务器
                            PipedStreamThread loc2srv = new PipedStreamThread("loc2srv",
                                                                              toLoc.getInputStream(),
                                                                              srvOut,
                                                                              bufSize);
                            // 等待其中任意一个管道的关闭
                            String exitFirst = executorService.invokeAny(Arrays.asList(srv2loc,
                                                                                       loc2srv));
                            if (log.isDebugEnabled())
                                log.debug("proxy conn exit first at " + exitFirst);
                        }
                        catch (Exception e) {
                            if (status == 1)
                                log.debug("something happen", e);
                            else
                                log.debug("proxy conn exit ...");
                        }
                        finally {
                            Streams.safeClose(toLoc);
                        }
                    }
                    catch (IOException e) {
                        log.debug("bab bad!! can't acccess local port", e);
                        srvOut.write("can't acccess local port".getBytes());
                        srvOut.flush();
                        toSrv.close();
                    }
                } else {
                    log.debugf("unkown type %s from proxy conn", msg.getString("Type"));
                }
            }
            catch (IOException e) {
                if (status == 1)
                    log.debug("something happen. proxy conn is lose", e);
                else
                    log.debug("proxy conn is closed");
            }
            finally {
                Streams.safeClose(toSrv);
                pmap.remove(pcid);
            }
            return null;
        }
    }

    protected class PingThread implements Callable<Object> {

        @Override
        public Object call() throws Exception {
            while (status == 1) {
                try {
                    // 每隔15秒发个心跳包,不然服务器会断开连接
                    Thread.sleep(15000);
                    NgrokMsg.ping().write(ctlOut);
                }
                catch (InterruptedException e) {
                    break;
                }
                catch (IOException e) {
                    if (status == 1)
                        log.debug("heartbeat exit. Contrl Conntion close?", e);
                    else
                        log.debug("heartbeat exit.");
                    break;
                }
            }
            return null;
        }
    }

    public static NgrokClient make(PropertiesProxy conf, String prefix) {
        NgrokClient client = new NgrokClient();
        Mirror<NgrokClient> mirror = Mirror.me(NgrokClient.class);
        for (String key : conf.keys()) {
            if (!key.startsWith(prefix) || key.equals(prefix + "auto_start")) {
                continue;
            }
            String value = conf.get(key);
            if (Strings.isBlank(key))
                continue;
            try {
                mirror.setValue(client, key.substring(prefix.length()), value);
            }
            catch (Exception e) {
                log.warnf("bad ngrok.client configure k=%s v=%s", key, value, e);
            }
        }
        return client;
    }

    public static void main(String[] args) {
        NgrokClient client = new NgrokClient();
        if (!NgrokAgent.fixFromArgs(client, args)) {
            log.debug("usage : -srv_host=wendal.cn -srv_port=4443 -to_host=127.0.0.1 -to_port=8080 -auth_token=ABC -conf_file=xxx.properties");
            return;
         }
        client.start();
    }
    
    @Override
    public Integer getStatus() {
        return status;
    }
}
