package org.nutz.plugins.ngrok.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import org.nutz.lang.Mirror;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class NgrokClient implements Runnable {

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
     * 缓存区大小,默认16kb
     */
    public int bufSize = 16*1024;
    
    /**
     * 隧道协议, 默认是http, 如需设置多个,用加号连起来, 例如 http+https+tcp
     */
    public String protocol = "http";
    
    /**
     * tcp隧道的外网端口号. http/https时无效
     */
    public int remotePort;
    
    /**
     * Http/Https协议时的简单鉴权,通常不需要
     */
    public String httpAuth = "";
    
    /**
     * Http/Https协议时的CNAME,通常不需要
     */
    public String hostName = "";
    
    /**
     * 指定子域名, 通过不支持
     */
    public String subdomain = "";
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
    protected int status = 0;
    /**
     * 请求id与映射地址的响应
     */
    protected Map<String, NgrokMsg> reqIdMap = new HashMap<String, NgrokMsg>();
    /**
     * 线程池
     */
    protected transient ExecutorService executorService;

    protected transient Socket ctlSocket;

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
            socketFactory = SSLSocketFactory.getDefault();
        // 把自身提交到线程池去执行呗
        executorService.submit(this);
    }

    public void run() {
        try {
            status = 1;
            // 建一个通往服务器的控制Socket
            ctlSocket = newSocket2Server();
            ctlIn = ctlSocket.getInputStream();
            ctlOut = ctlSocket.getOutputStream();
            // 发送登录信息
            NgrokMsg auth = NgrokMsg.auth(auth_token, "", "windows", "386", "");
            NgrokAgent.writeMsg(ctlOut, auth);
            // 接受登录信息
            NgrokMsg authResp = NgrokAgent.readMsg(ctlIn);
            String error = authResp.getString("Error");
            if (!Strings.isBlank(error)) { // 发现错误, 只能退出了
                log.error("auth fail : " + error);
                return;
            }
            id = authResp.getString("ClientId");
            // 发送通道请求
            String reqId = R.UU32();
            for (String prot : protocol.split("[\\+]")) {
                if (prot.startsWith("http"))
                    NgrokAgent.writeMsg(ctlOut, NgrokMsg.reqTunnel(reqId, hostName, prot, subdomain, httpAuth, 0));
                else if (prot.startsWith("tcp")) 
                    NgrokAgent.writeMsg(ctlOut, NgrokMsg.reqTunnel(reqId, "", prot, "", "", remotePort));
                else
                    log.warn("unkown protocol=" + prot);
            }
            
            // 启动心跳线程
            executorService.submit(new Runnable() {
                public void run() {
                    while (status == 1) {
                        try {
                            // 每隔15秒发个心跳包,不然服务器会断开连接
                            Thread.sleep(15000);
                            NgrokAgent.writeMsg(ctlOut, NgrokMsg.ping());
                        }
                        catch (InterruptedException e) {
                            break;
                        }
                        catch (IOException e) {
                            log.debug("Contrl Conntion close?", e);
                            break;
                        }
                    }
                }
            });
            handle();
        }
        catch (Exception e) {
            log.debug("something happen", e);
        } finally {
            Streams.safeClose(ctlSocket);
            status = 2;
        }
    }

    protected void handle() {
        while (status == 1) {
            try {
                // 看看服务器想干啥
                NgrokMsg msg = NgrokAgent.readMsg(ctlIn);
                String type = msg.getString("Type");
                // 服务器要求我们发送新的代理链接
                if ("ReqProxy".equals(type)) {
                    executorService.submit(new Runnable(){
                        public void run() {
                            proxy();
                        }
                    });
                }
                // 服务器发心跳了!!!, 需要回应pong
                else if ("Ping".equals(type)) {
                    NgrokAgent.writeMsg(ctlOut, NgrokMsg.pong());
                }
                // 服务器响应ReqTunnel,但有可能已经注册过,那么只能直接退出了
                else if ("NewTunnel".equals(type)) {
                    if (Strings.isBlank(msg.getString("Error"))) {
                        reqIdMap.put(msg.getString("ReqId"), msg);
                        log.debugf("ReqId=%s URL=%s", msg.getString("ReqId"), msg.getString("Url"));
                    } else {
                        log.error("ReqTunnel Failed!!! Exit!!" + msg.getString("Error"));
                        status = 2;
                    }
                }
                // 服务器对心跳线程ping的回应,可以忽略
                else if ("Pong".equals(type)) {
                }
                else {
                    log.info("unknown type=" + msg.getString("Type"));
                }
            }
            catch (IOException e) {
                log.debug("bad io", e);
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
        if (executorService != null) {
            executorService.shutdownNow();
            executorService = null;
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
    protected void proxy() {
        Socket toSrv = null;
        try {
            // 首先,建立一条通道
            toSrv = newSocket2Server();
            // 需要等服务器响应,可能会很久
            toSrv.setSoTimeout(3600 * 1000); // 一小时
            // 取出该通道的输入输出流备用
            OutputStream srvOut = toSrv.getOutputStream();
            InputStream srvIn = toSrv.getInputStream();
            // 发起注册通道的请求
            NgrokAgent.writeMsg(srvOut, NgrokMsg.regProxy(getId()));
            // 等待服务器响应StartProxy
            NgrokMsg msg = NgrokAgent.readMsg(srvIn);
            // 如果真的响应了StartProxy,开始桥接Socket
            if ("StartProxy".equals(msg.getString("Type"))) {
                try {
                    if (log.isDebugEnabled())
                        log.debug("start socket pipe ...");
                    Socket locSocket = newSocket2Local();
                    try {
                        // 服务器-->本地
                        PipedStreamThread srv2loc = new PipedStreamThread("srv2loc",
                                                                          srvIn,
                                                                          locSocket.getOutputStream(),
                                                                          bufSize);
                        // 本地-->服务器
                        PipedStreamThread loc2srv = new PipedStreamThread("loc2srv",
                                                                          locSocket.getInputStream(),
                                                                          srvOut,
                                                                          bufSize);
                        // 等待其中任意一个管道的关闭
                        executorService.invokeAny(Arrays.asList(srv2loc, loc2srv));
                    }
                    catch (Exception e) {
                        log.debug("something happen", e);
                    }
                    finally {
                        Streams.safeClose(locSocket);
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
            log.debug("something happen", e);
        }
        finally {
            Streams.safeClose(toSrv);
        }
    }

    public static void main(String[] args) {
        NgrokClient client = new NgrokClient();
        Mirror<NgrokClient> mirror = Mirror.me(NgrokClient.class);
        for (String arg : args) {
            if (!arg.startsWith("-") || !arg.contains("=")) {
                log.debug("bad arg = " + arg);
                log.debug("usage : -srv_host=wendal.cn -srv_port=4443 -to_host=127.0.0.1 -to_port=8080 -auth_token=ABC");
                return;
            }
            arg = arg.substring(1);
            String[] tmp = arg.split("=", 2);
            log.debugf("config key=%s value=%s", tmp[0], tmp[1]);
            mirror.setValue(client, tmp[0], tmp[1]);
        }
        client.start();
    }
}
