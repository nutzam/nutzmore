package org.nutz.wxrobot;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.nutz.http.Cookie;
import org.nutz.http.Header;
import org.nutz.http.Http;
import org.nutz.http.HttpException;
import org.nutz.http.Request;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.MapKeyConvertor;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mapl.Mapl;
import org.nutz.runner.NutLock;
import org.nutz.runner.NutRunner;
import org.nutz.wxrobot.bean.SyncKey;
import org.nutz.wxrobot.bean.WxInMsg;

import sun.misc.BASE64Decoder;

/**
 * Weixin Robot Client
 * 
 * @author pw
 *
 */
public class Client {

    private Log log = Logs.get();

    // 几个特殊的url
    private String login_url = "https://login.weixin.qq.com";
    private String weixin_url = "https://wx.qq.com";
    private String webpush_url = "https://webpush.weixin.qq.com";
    private String redirect_url;

    // 用户头像
    private String userAvatar;
    private File userAvatarFile;
    private boolean showAvatar;

    // timeout
    private int wait_timeout = 10;
    private String wait_url;

    // listener
    private NutRunner listener;
    private NutLock listenerLock;

    // 微信相关
    private String appid = "wx782c26e4c19acffb"; // 网页版里自带的
    private String uuid;
    // private String deviceId = "e" + System.currentTimeMillis(); //
    private String deviceId = "e1471230987"; // 官方规定的格式，直接copy一个过来的
    private String skey;
    private String wxuin;
    private String wxsid;
    private String pass_ticket;
    private Cookie cookie;
    private SyncKey syncKey;
    private Object user;
    private String userName;
    private String nickName;
    private List<Object> contacts = new ArrayList<Object>();
    private Map<String, Object> members = new HashMap<String, Object>();
    private List<String> pushServers = Arrays.asList("webpush.weixin.qq.com",
                                                     "webpush2.weixin.qq.com",
                                                     "webpush.wechat.com",
                                                     "webpush1.wechat.com",
                                                     "webpush2.wechat.com",
                                                     "webpush1.wechatapp.com");
    // 微信特殊账号 该列表copy自
    // https://github.com/biezhi/wechat-robot/blob/f7728a4050dddc5edd9fbe3c988689784f53cb3c/src/main/java/me/biezhi/weixin/App.java
    private List<String> specialUsers = Arrays.asList("newsapp",
                                                      "fmessage",
                                                      "filehelper",
                                                      "weibo",
                                                      "qqmail",
                                                      "fmessage",
                                                      "tmessage",
                                                      "qmessage",
                                                      "qqsync",
                                                      "floatbottle",
                                                      "lbsapp",
                                                      "shakeapp",
                                                      "medianote",
                                                      "qqfriend",
                                                      "readerapp",
                                                      "blogapp",
                                                      "facebookapp",
                                                      "masssendapp",
                                                      "meishiapp",
                                                      "feedsapp",
                                                      "voip",
                                                      "blogappweixin",
                                                      "weixin",
                                                      "brandsessionholder",
                                                      "weixinreminder",
                                                      "wxid_novlwrv3lqwv11",
                                                      "gh_22b87fa7cb3c",
                                                      "officialaccounts",
                                                      "notification_messages",
                                                      "wxid_novlwrv3lqwv11",
                                                      "gh_22b87fa7cb3c",
                                                      "wxitil",
                                                      "userexperience_alarm",
                                                      "notification_messages");

    private static Map<Integer, String> msgTypeLabel = new HashMap<Integer, String>();

    static {
        // Avoid Error：
        // javax.net.ssl.SSLProtocolException:
        // handshake alert: unrecognized_name
        System.setProperty("jsse.enableSNIExtension", "false");

        msgTypeLabel.put(1, "文字");
        msgTypeLabel.put(3, "图片");
        msgTypeLabel.put(34, "语音");
        msgTypeLabel.put(42, "名片");
    }

    // ------------------------- 几个util方法

    private String matchFind(String regex, String content) {
        Matcher matcher = Pattern.compile(regex).matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private Matcher match(String regex, String content) {
        return Pattern.compile(regex).matcher(content);
    }

    private File base64Image(String imageDataBytes) {
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] imgBytes;
        File imgOutFile;
        try {
            imgBytes = decoder.decodeBuffer(imageDataBytes);
            BufferedImage bufImg = ImageIO.read(new ByteArrayInputStream(imgBytes));
            imgOutFile = File.createTempFile("wxrobot_avatar_", ".jpg");
            ImageIO.write(bufImg, "jpg", imgOutFile);
            return imgOutFile;
        }
        catch (IOException e) {
            log.error(e);
        }
        return null;
    }

    private boolean openImage(File f) {
        if (f != null && f.exists()) {
            try {
                Desktop dt = Desktop.getDesktop();
                dt.open(f);
                return true;
            }
            catch (IOException e) {
                log.error(e);
            }
        }
        return false;
    }

    private WxInMsg convertWxInMsg(Object msg) {
        Map<String, Object> map = (Map<String, Object>) msg;
        Lang.convertMapKey(map, new MapKeyConvertor() {
            @Override
            public String convertKey(String key) {
                return Strings.lowerFirst(key);
            }
        }, true);
        return Lang.map2Object(map, WxInMsg.class);
    }

    private String getUserName(String nmkey) {
        if (members.containsKey(nmkey)) {
            return (String) Mapl.cell(members.get(nmkey), "NickName");
        }
        return nmkey;
    }

    // ------------------------ 微信相关

    /**
     * get UUID
     * 
     * @return
     */
    private String QR_UUID() {
        String url = String.format("%s/jslogin", login_url);
        Request req = Request.create(url, METHOD.GET, NutMap.NEW()
                                                            .addv("appid", appid)
                                                            .addv("fun", "new")
                                                            .addv("lang", "zh_CN")
                                                            .addv("_", System.currentTimeMillis()));
        log.infof("QR_UUID Url: %s", req.getUrl());
        Response resp = Sender.create(req).send();
        String respBody = resp.getContent();
        if (!Strings.isBlank(respBody)) {
            // 获取code与uuid
            Matcher m = match("window.QRLogin.code = (\\d+); window.QRLogin.uuid = \"(.*)\"",
                              respBody);
            if (m.find()) {
                String code = m.group(1);
                if ("200".equals(code)) {
                    this.uuid = m.group(2);
                    log.infof("QR_UUID Get: %s", this.uuid);
                    return this.uuid;
                } else {
                    log.errorf("QR_UUID ErrCode: %s", code);
                }
            }
        }
        return null;
    }

    /**
     * get QRCode picture
     * 
     * @return
     */
    private File QR_PIC() {
        File output;
        try {
            output = File.createTempFile("wxrobot_qrcode_", ".jpg");
        }
        catch (IOException e) {
            log.error(e);
            return null;
        }
        // download qrcode
        Response resp = Http.get(String.format("%s/qrcode/%s", login_url, uuid));
        if (resp.isOK()) {
            try {
                Streams.writeAndClose(new FileOutputStream(output), resp.getStream());
                log.infof("QR_CODE Path: %s", output.getAbsolutePath());
            }
            catch (FileNotFoundException e) {
                log.error(e);
                return null;
            }
        }
        return output;
    }

    /**
     * use system software open qrcode.jpg
     * 
     * @return
     */
    private boolean SHOW_QR_PIC() {
        File qrcode = QR_PIC();
        if (qrcode != null) {
            return openImage(qrcode);
        }
        return false;
    }

    /**
     * check status, scan & confirm login
     * 
     * @return
     */
    private boolean WAIT_LOGIN() {
        String url = String.format("%s/cgi-bin/mmwebwx-bin/login", login_url);
        Request req = Request.create(url,
                                     METHOD.GET,
                                     NutMap.NEW()
                                           .addv("loginicon", true)
                                           .addv("tip", 1)
                                           .addv("uuid", this.uuid)
                                           .addv("_", System.currentTimeMillis()))
                             .offEncode(true); // uuid中包含==字符，不能转义
        if (Strings.isBlank(wait_url)) {
            wait_url = req.getUrl().toString();
            log.infof("WAIT_LOGIN Url: %s", wait_url);
        }
        Response resp;
        try {
            resp = Sender.create(req, 1000 * wait_timeout).send();
        }
        catch (HttpException e) {
            log.warnf("WAIT_LOGIN Err: wait %ds timeout", wait_timeout);
            return false;
        }
        String respBody = resp.getContent();
        if (Strings.isBlank(respBody)) {
            log.warnf("WAIT_LOGIN Err: %s", "body is empty");
        } else {
            // check code
            String code = matchFind("window.code=(\\d+);", respBody);
            if (null == code) {
                log.warnf("WAIT_LOGIN Err: %s", "code not found");
            } else {
                // 200, login ok
                if (code.equals("200")) {
                    log.infof("WAIT_LOGIN %s: confirm login", code);
                    redirect_url = matchFind("window.redirect_uri=\"(\\S+?)\";", respBody);
                    weixin_url = redirect_url.substring(0, this.redirect_url.lastIndexOf("/"));
                    log.infof("WAIT_LOGIN Re_Url: %s", redirect_url);
                    log.infof("WAIT_LOGIN Wx_Url: %s", weixin_url);
                    // FIXME
                    // String redirectHost = "wx.qq.com";
                    // try {
                    // URL pmURL = new URL(redirect_url);
                    // redirectHost = pmURL.getHost();
                    // }
                    // catch (MalformedURLException e) {
                    // e.printStackTrace();
                    // }
                    // String pushServer =
                    // PushServerUtil.getPushServer(redirectHost);
                    // webpush_url = "https://" + pushServer +
                    // "/cgi-bin/mmwebwx-bin";
                    return true;
                }
                // 201
                else if (code.equals("201")) {
                    log.infof("WAIT_LOGIN %s: scanned QRCode", code);
                    userAvatar = matchFind("window.userAvatar = '(.*)';", respBody);
                    if (!Strings.isBlank(userAvatar)) {
                        // 显示头像
                        if (!showAvatar) {
                            showAvatar = true;
                            String imageDataBytes = userAvatar.substring(userAvatar.indexOf(",")
                                                                         + 1);
                            userAvatarFile = base64Image(imageDataBytes);
                            openImage(userAvatarFile);
                            log.infof("USR_AVATAR Path: %s", userAvatarFile.getAbsolutePath());
                        }
                    }
                }
                // 408
                else if (code.equals("408")) {
                    log.infof("WAIT_LOGIN %s: login timeout", code);
                }
                // others
                else {
                    log.infof("WAIT_LOGIN %s: unknown code", code);
                }
            }
        }
        return false;
    }

    /**
     * get uin & sid & pass_ticket
     * 
     * @return
     */
    private boolean DO_LOGIN() {
        Request req = Request.create(redirect_url + "&fun=new", METHOD.GET); // 必须添加
                                                                             // fun=new
                                                                             // 否者是返回一个页面
        log.infof("DO_LOGIN Url: %s", req.getUrl());
        Response resp = Sender.create(req).send();
        String respBody = resp.getContent();
        if (!Strings.isBlank(respBody)) {
            // 设置基本信息
            skey = matchFind("<skey>(\\S+)</skey>", respBody);
            wxsid = matchFind("<wxsid>(\\S+)</wxsid>", respBody);
            wxuin = matchFind("<wxuin>(\\S+)</wxuin>", respBody);
            pass_ticket = matchFind("<pass_ticket>(\\S+)</pass_ticket>", respBody);
            log.infof("DO_LOGIN skey: %s", skey);
            log.infof("DO_LOGIN wxsid: %s", wxsid);
            log.infof("DO_LOGIN wxuin: %s", wxuin);
            log.infof("DO_LOGIN pass_ticket: %s", pass_ticket);
            // 设置cookie
            this.cookie = resp.getCookie();
            log.infof("DO_LOGIN cookie: %s", cookie.toString());
            return true;
        }
        return false;
    }

    private NutMap BaseRequestJson() {
        NutMap baseRequest = NutMap.NEW();
        baseRequest.addv("DeviceID", deviceId);
        baseRequest.addv("Skey", skey);
        baseRequest.addv("Sid", wxsid);
        baseRequest.addv("Uin", wxuin);
        return NutMap.NEW().addv("BaseRequest", baseRequest);
    }

    /**
     * webwx init
     * 
     * @return
     */
    private boolean WX_INIT() {
        String url = String.format("%s/webwxinit?r=%d&pass_ticket=%s",
                                   weixin_url,
                                   System.currentTimeMillis(),
                                   pass_ticket);
        Request req = Request.create(url, METHOD.POST)
                             .setHeader(Header.create().set("Content-Type",
                                                            "application/json;charset=utf-8"))
                             .setCookie(cookie)
                             .setData(Json.toJson(BaseRequestJson()));
        log.infof("WX_INIT Url: %s", req.getUrl());
        Response resp = Sender.create(req).send();
        String respBody = resp.getContent();
        if (!Strings.isBlank(respBody)) {
            Object respJson = Json.fromJson(respBody);
            // 获取对象中特定的属性
            // SyncKey
            Object skObject = Mapl.cell(respJson, "SyncKey");
            syncKey = Json.fromJson(SyncKey.class, Json.toJson(skObject));
            log.infof("WX_INIT SyncKey: %s", syncKey.toString());
            // User
            user = Mapl.cell(respJson, "User");
            userName = (String) Mapl.cell(respJson, "User.UserName");
            nickName = (String) Mapl.cell(respJson, "User.NickName");
            members.put(userName, user);
            log.infof("WX_INIT UserUN: %s", userName);
            log.infof("WX_INIT UserNN: %s", nickName);
            log.infof("WX_INIT UserJson: \n%s", Json.toJson(user));
            return true;
        }
        return false;
    }

    /**
     * check status
     * 
     * @return
     */
    private boolean WX_STATUS_NOTIFY() {
        String url = String.format("%s/webwxstatusnotify?lang=zh_CN&pass_ticket=%s",
                                   weixin_url,
                                   pass_ticket);
        Request req = Request.create(url, METHOD.POST)
                             .setHeader(Header.create().set("Content-Type",
                                                            "application/json;charset=utf-8"))
                             .setCookie(cookie)
                             .setData(Json.toJson(BaseRequestJson().addv("Code", 3)
                                                                   .addv("ClientMsgId",
                                                                         System.currentTimeMillis())
                                                                   .addv("ToUserName", userName)
                                                                   .addv("FromUserName",
                                                                         userName)));
        log.infof("WX_STATUS_NOTIFY Url: %s", req.getUrl());
        Response resp = Sender.create(req).send();
        String respBody = resp.getContent();
        if (!Strings.isBlank(respBody)) {
            Object respJson = Json.fromJson(respBody);
            int ret = (Integer) Mapl.cell(respJson, "BaseResponse.Ret");
            String errMsg = (String) Mapl.cell(respJson, "BaseResponse.ErrMsg");
            log.infof("WX_STATUS_NOTIFY Ret: %d", ret);
            if (ret == 0) {
                // 状态正常
                return true;
            } else {
                log.errorf("WX_STATUS_NOTIFY Err: %s", errMsg);
            }
        }
        return false;
    }

    /**
     * check sync
     */
    private int[] SYNC_CHECK() {
        int[] arr = new int[2];
        String url = String.format("%s/cgi-bin/mmwebwx-bin/synccheck", webpush_url);
        NutMap params = NutMap.NEW()
                              .addv("_", System.currentTimeMillis())
                              .addv("skey", skey)
                              .addv("uin", wxuin)
                              .addv("sid", wxsid)
                              .addv("deviceid", deviceId)
                              .addv("synckey", syncKey.toString());
        Request req = Request.create(url, METHOD.GET, params).setCookie(cookie);
        log.infof("SYNC_CHECK Url: %s", req.getUrl());
        Response resp = Sender.create(req).send();
        String respBody = resp.getContent();
        if (!Strings.isBlank(respBody)) {
            String retcode = matchFind("retcode:\"(\\d+)\",", respBody);
            String selector = matchFind("selector:\"(\\d+)\"}", respBody);
            if (null != retcode && null != selector) {
                // retcode: 0 正常 1100 失败/登出微信
                // 正常返回结果
                // window.synccheck={retcode:"0",selector:"0"}
                // 有消息返回结果
                // window.synccheck={retcode:"0",selector:"6"}
                // 发送消息返回结果
                // window.synccheck={retcode:"0",selector:"2"}
                // 朋友圈有动态
                // window.synccheck={retcode:"0",selector:"4"}
                arr = new int[]{Integer.parseInt(retcode), Integer.parseInt(selector)};
                log.infof("SYNC_CHECK Result: %s", Json.toJson(arr, JsonFormat.compact()));
            }
        } else {
            log.infof("SYNC_CHECK Result: %s", "empty resp");
        }
        return arr;
    }

    /**
     * get lastest info
     */
    @SuppressWarnings("unchecked")
    private List<Object> WX_SYNC() {
        String url = String.format("%s/webwxsync?lang=zh_CN&pass_ticket=%s&sid=%s&skey=%s",
                                   weixin_url,
                                   pass_ticket,
                                   wxsid,
                                   skey);
        Request req = Request.create(url, METHOD.POST)
                             .setHeader(Header.create().set("Content-Type",
                                                            "application/json;charset=utf-8"))
                             .setCookie(cookie)
                             .setData(Json.toJson(BaseRequestJson().addv("SyncKey", syncKey)
                                                                   .addv("rr",
                                                                         System.currentTimeMillis())));
        log.infof("WX_SYNC Url: %s", req.getUrl());
        Response resp = Sender.create(req).send();
        String respBody = resp.getContent();

        if (!Strings.isBlank(respBody)) {
            Object respJson;
            try {
                respJson = Json.fromJson(respBody);
            }
            catch (Exception e) {
                log.error("Json Parse Error");
                return null;
            }
            // 获取对象中特定的属性
            // SyncKey
            Object skObject = Mapl.cell(respJson, "SyncKey");
            syncKey = Json.fromJson(SyncKey.class, Json.toJson(skObject));
            log.infof("WX_SYNC SyncKey: %s", syncKey.toString());
            // AddMsgCount
            int addMsgCount = (Integer) Mapl.cell(respJson, "AddMsgCount");
            // AddMsgList
            List<Object> addMsgList = (List<Object>) Mapl.cell(respJson, "AddMsgList");
            if (addMsgCount > 0) {
                return addMsgList;
            }
        }
        return null;
    }

    private void WX_SEND_MSG(String content, String toUserName) {
        // 组装消息
        NutMap outMsg = NutMap.NEW();
        outMsg.put("Type", 1);
        outMsg.put("Content", content);
        outMsg.put("FromUserName", userName);
        outMsg.put("ToUserName", toUserName);
        outMsg.put("LocalID", System.currentTimeMillis() + R.random(1000, 9999));
        outMsg.put("ClientMsgId", System.currentTimeMillis() + R.random(1000, 9999));
        // 发送
        String url = String.format("%s/webwxsendmsg?lang=zh_CN&pass_ticket=%s",
                                   weixin_url,
                                   pass_ticket);
        Request req = Request.create(url, METHOD.POST)
                             .setHeader(Header.create().set("Content-Type",
                                                            "application/json;charset=utf-8"))
                             .setCookie(cookie)
                             .setData(Json.toJson(BaseRequestJson().addv("Msg", outMsg)
                                                                   .addv("rr",
                                                                         System.currentTimeMillis())));
        log.infof("WX_SEND_MSG Url: %s", req.getUrl());
        Response resp = Sender.create(req).send();
        String respBody = resp.getContent();
        if (!Strings.isBlank(respBody)) {
            Object respJson = Json.fromJson(respBody);
            int ret = (Integer) Mapl.cell(respJson, "BaseResponse.Ret");
            String errMsg = (String) Mapl.cell(respJson, "BaseResponse.ErrMsg");
            if (ret == 0) {
                log.infof("WX_SEND_MSG : \n%s", Json.toJson(outMsg));
            } else {
                log.errorf("WX_SEND_MSG Ret: %d", ret);
                log.errorf("WX_SEND_MSG Err: %s", errMsg);
            }
        }
    }

    /**
     * get all contact
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    private boolean GET_CONTACT() {
        String url = String.format("%s/webwxgetcontact?lang=zh_CN&pass_ticket=%s&r=%d&seq=0&skey=%s",
                                   weixin_url,
                                   pass_ticket,
                                   System.currentTimeMillis(),
                                   skey);
        Request req = Request.create(url, METHOD.POST)
                             .setHeader(Header.create().set("Content-Type",
                                                            "application/json;charset=utf-8"))
                             .setCookie(cookie)
                             .setData(Json.toJson(BaseRequestJson()));
        log.infof("GET_CONTACT Url: %s", req.getUrl());
        Response resp = Sender.create(req).send();
        String respBody = resp.getContent();
        if (!Strings.isBlank(respBody)) {
            Object respJson = Json.fromJson(respBody);
            int ret = (Integer) Mapl.cell(respJson, "BaseResponse.Ret");
            String errMsg = (String) Mapl.cell(respJson, "BaseResponse.ErrMsg");
            if (ret == 0) {
                // 分析获取memberList
                List<Object> mlist = (List<Object>) Mapl.cell(respJson, "MemberList");
                for (Object m : mlist) {
                    int vflag = (Integer) Mapl.cell(m, "VerifyFlag");
                    String unm = (String) Mapl.cell(m, "UserName");
                    String nnm = (String) Mapl.cell(m, "NickName");
                    members.put(unm, m);
                    log.infof("GET_CONTACT NM: v_%d %s ", vflag, nnm);
                    // 公众号24 服务号29
                    if (vflag != 0) {
                        continue;
                    }
                    // 特殊联系人
                    if (specialUsers.contains(unm)) {
                        continue;
                    }
                    // 群聊
                    if (unm.indexOf("@@") != -1) {
                        continue;
                    }
                    // 自己
                    if (unm.equals(userName)) {
                        continue;
                    }
                    contacts.add(m);
                }
                log.infof("GET_CONTACT MSize: %d", members.size());
                log.infof("GET_CONTACT CSize: %d", contacts.size());
                return true;
            } else {
                log.errorf("GET_CONTACT Ret: %d", ret);
                log.errorf("GET_CONTACT Err: %s", errMsg);
            }
        }
        return false;
    }

    public void run() {
        // 获取UUID
        if (null == QR_UUID()) {
            return;
        }
        // 显示登陆用二维码
        if (!SHOW_QR_PIC()) {
            return;
        }
        // 查询 扫码&登陆状态
        while (!WAIT_LOGIN()) {
            Lang.quiteSleep(2000);
        }
        // 授权登录获取基本信息
        if (!DO_LOGIN()) {
            return;
        }
        // 客户端初始化
        if (!WX_INIT()) {
            return;
        }
        // 监听状态通知
        if (!WX_STATUS_NOTIFY()) {
            return;
        }
        // 获取所有联系人
        if (!GET_CONTACT()) {
            return;
        }
        // 监听消息 & 响应
        START_LISTEN();
    }

    int pi = 0;
    int px = pushServers.size();

    /**
     * 开始监听
     */
    private void START_LISTEN() {
        log.info("START_LISTEN Status: start");
        listener = new NutRunner("") {
            @Override
            public long exec() throws Exception {
                int[] arr = SYNC_CHECK();
                while (arr[0] == 1100) {
                    if (pi >= px) {
                        // pi = 0;
                        // 尝试了所有的都不行的话，退出吧
                        break;
                    }
                    webpush_url = pushServers.get(pi);
                    pi++;
                    Lang.quiteSleep(1000);
                    arr = SYNC_CHECK();
                }
                if (arr[0] == 0) {
                    // 发送信息
                    if (arr[1] == 2 || arr[1] == 6) {
                        List<Object> data = WX_SYNC();
                        if (data != null) {
                            log.infof("一次接收[%d]条消息\n", data.size());
                            for (Object msg : data) {
                                WxInMsg inMsg = convertWxInMsg(msg);
                                log.infof("消息ID：%s", inMsg.getMsgID());
                                log.infof("来自用户：%s", getUserName(inMsg.getFromUserName()));
                                log.infof("消息类型：%s",
                                          msgTypeLabel.containsKey(inMsg.getMsgType()) ? msgTypeLabel.get(inMsg.getMsgType())
                                                                                       : ("["
                                                                                          + inMsg.getMsgType()
                                                                                          + "]"));
                                // 不是自己发的，响应一下
                                if (!userName.equals(inMsg.getFromUserName())) {
                                    // 文字类型
                                    if (inMsg.getMsgType() == 1) {
                                        log.infof("消息内容：%s", inMsg.getContent());
                                        WX_SEND_MSG("收到消息："
                                                    + inMsg.getContent(),
                                                    inMsg.getFromUserName());
                                    } else {
                                        // 图片
                                        if (inMsg.getMsgType() == 3) {}
                                        // 语音
                                        if (inMsg.getMsgType() == 34) {}
                                        // 名片
                                        if (inMsg.getMsgType() == 42) {}
                                        WX_SEND_MSG("暂时还不支持该消息类型", inMsg.getFromUserName());
                                    }
                                }
                            }
                        }
                    }
                    // 其他类型
                    else if (arr[1] == 7) {
                        WX_SYNC();
                    }
                    // 其他类型
                    else {
                        System.out.println("不处理类型：" + arr[1]);
                    }
                    return 100;
                } else {
                    log.errorf("START_LISTEN Ret: %d", arr[0]);
                    log.infof("START_LISTEN Status: stop");
                    listenerLock.setStop(true);
                    return 10;
                }
            }
        }.setSleepAfterError(1);
        listenerLock = listener.getLock();
        new Thread(listener).start();
    }

}
