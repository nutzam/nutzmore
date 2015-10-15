package org.nutz.auth.secken;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.nutz.http.Request;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 官方文档: https://www.yangcong.com/api
 * @author wendal(wendal1985@gmail.com)
 * @see https://nutz.cn
 */
public class Secken {
    
    protected static final Log log = Logs.get();
    
    public static boolean DEBUG;
    
    protected String apibase = "https://api.yangcong.com/v2";
    protected String authbase = "https://auth.yangcong.com/v2";
    
    protected String appId;
    protected String appKey;
    protected String authId;
    protected int timeout = 15*1000;
    
    public Secken() {}

    public Secken(String appId, String appKey, String authId) {
        super();
        this.appId = appId;
        this.appKey = appKey;
        this.authId = authId;
    }

    // -------------------------------------------------
    // ------------登陆相关-----------------------------
    // -------------------------------------------------
    
    /**
     * 获取绑定账号二维码图片的URL地址。用户通过扫描绑定二维码把自己的账号和洋葱客户端完成绑定。
     * 所有洋葱的服务都只针对绑定过洋葱客户端的账号生效。
     * 二维码图片的地址是动态的，你需要每60秒重新获取一次地址。
     * @param auth_type 1(点击确定按钮，默认)、2(使用手势密码)、3(人脸验证)、4(声音验证)
     * @param callback 回调URL,可以为null
     * @return 服务器响应,通过qrcode_url获取二维码地址,或qrcode_data获取裸数据
     */
    public SeckenResp getBind(int auth_type, String callback) {
        NutMap params = new NutMap();
        if (auth_type != 1)
            params.put("auth_type", auth_type);
        if (!Strings.isBlank(callback))
            params.setv("callback", callback);
        params.put("app_id", appId);
        return _GET(apibase+"/qrcode_for_binding", params);
    }
    
    /**
     * 获取账号认证二维码图片的URL地址。
     * 客户通过使用洋葱安全扫一扫扫描认证二维码完成登录认证、支付认证或授权认证等授权操作。
     * 二维码图片的地址是动态的，你需要每60秒重新获取一次地址。
     * @param auth_type 1(点击确定按钮，默认)、2(使用手势密码)、3(人脸验证)、4(声音验证)
     * @param callback 回调URL,可以为null
     * @return 服务器响应,通过qrcode_url()获取二维码地址,或qrcode_data()获取裸数据
     */
    public SeckenResp getAuth(int auth_type, String callback) {
        NutMap params = new NutMap();
        if (auth_type != 1)
            params.put("auth_type", auth_type);
        if (!Strings.isBlank(callback))
            params.setv("callback", callback);
        params.put("app_id", appId);
        return _GET(apibase+"/qrcode_for_auth", params);
    }
    
    /**
     * 当用户使用扫码登录并且扫码手机无法访问互联网时，
     * 你需要调用此API将用户引导到一个洋葱特定的认证页面，用户在该页面输入绑定洋葱的手机以及洋葱动态验证码完成验证。
     * 验证成功后，洋葱会调用你传递的一个回调URL，在你收到洋葱回调后，必须首先对URL进行转码，并对参数签名进行验证，避免被恶意调用。
     * @param callback 必须填写的回调URL
     * @return 本地生成响应, 通过url()获取生成好的URL
     */
    public SeckenResp authPage(String callback) {
        NutMap params = new NutMap();
        params.put("auth_id", authId);
        params.put("callback", callback);
        params.put("timestamp", System.currentTimeMillis()/1000);

        doSign(params);
        Request req = Request.create(authbase+"/auth_page", METHOD.GET, params);
        SeckenResp sr = new SeckenResp();
        sr.setv("status", 200);
        sr.put("url", req.getUrl());
        return sr;
    }


    // -------------------------------------------------
    // ------------认证相关-----------------------------
    // -------------------------------------------------
    
    /**
     * 向洋葱手机App推送一条实时验证，你可以要求用户使用以下几种方式进行验证：
     *人脸验证；
     * 声音验证；
     * 手势验证；
     * 点击确认按钮验证；
     * @param action_type 操作类型，显示在推送的认证消息上的行为。可选操作类型码：1(登录验证)、2(请求验证)、3(交易验证)、4(其他验证)
     * @param auth_type 1(点击确定按钮，默认)、2(使用手势密码)、3(人脸验证)、4(声音验证)
     * @param uid 用户 id，最长 64 字节字符串，用户在洋葱对应该应用生成的唯一且固定的 ID 账号。
     * @param user_ip 用户ip,可以为null
     * @param username 用户名,可以为null
     * @param callback 回调URL,可以为null
     * @return 通过event_id()获取一个 20 字节的字符串用来唯一标识一个特定的账号事件。
     *          你可以将 event_id 传递给 /v2/event_result 来获得验证结果。如果你传递了 callback 参数，则返回的 event_id 不能用于获取验证结果。
     */
    public SeckenResp realtimeAuth(int action_type, int auth_type, String uid, String user_ip, String username, String callback) {
        NutMap params = new NutMap();
        params.put("action_type", action_type > 0 ? action_type : 1);
        params.put("auth_type", auth_type > 0 ? auth_type : 1);
        params.put("uid", uid);
        params.put("app_id", appId);
        if (!Strings.isBlank(user_ip))
            params.put("user_ip", user_ip);
        if (!Strings.isBlank(username))
            params.put("username", username);

        return _POST(apibase+"/realtime_authorization", params);
    }
    
    /**
     * 当绑定洋葱账号之后，在继续使用传统登录、交易、关键信息修改等需要第二次验证身份的地方如果洋葱APP无网络的时，可调用该方法，进行传统离线动态码授权验证。
     * 该授权码为客户端顶部展示的6位动态码。
     * @param dynamic_code 6 位动态码，用户输入的洋葱动态验证码
     * @param uid 用户 id，最长64字节字符串，用户在洋葱对应该应用生成的唯一且固定的ID账号。
     * @return 通过status()或ok()判断结果
     */
    public SeckenResp offlineAuth(String dynamic_code, String uid) {
        NutMap params = new NutMap();
        params.put("dynamic_code", dynamic_code);
        params.put("uid", uid);
        params.put("app_id", appId);
        return _POST(apibase+"/offline_authorization", params);
    }


    // -------------------------------------------------
    // ------------结果查询-----------------------------
    // -------------------------------------------------
    
    /**
     * 查看event_id所对应的事件响应结果，该 event_id 可以通过 /v2/realtime_authorization、/v2/qrcode_for_auth、/v2/qrcode_for_binding 获得,
     * 此操作为异步获取等待用户扫码产生响应或直至接口返回超时不可重试,返回结果之后，必须进行签名验证，
     * 避免传输过程被恶意修改。event_id有效时间为 300s。
     * @param event_id 事件 id，一个20字节的字符创，用来标识某个洋葱认证事件。
     * @return 通过user_id()获取用户在洋葱的 ID，最长64字节字符串，用户在洋葱对应该应用生成的唯一且固定的ID账号。
     */
    public SeckenResp getResult(String event_id) {
        NutMap params = new NutMap();
        params.put("event_id", event_id);
        params.put("app_id", appId);
        return _GET(apibase+"/event_result", params);
    }
    
    protected void doSign(NutMap params) {
        if (!Strings.isBlank(params.getString("callback"))) {
            try {
                params.put("callback", URLEncoder.encode(params.getString("callback"), "UTF-8"));
            }
            catch (UnsupportedEncodingException e) {
            }
        }
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            sb.append(key).append('=').append(params.get(key));
        }
        sb.append(appKey);
        String signature = Lang.md5(sb.toString());
        if (DEBUG)
            log.debugf("params=[%s] sign=[%s]", sb, signature);
        params.put("signature", signature);
    }
    
    public void checkSign(SeckenResp sr) {
        List<String> keys = new ArrayList<String>(sr.keySet());
        keys.remove("signature");
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            sb.append(key).append('=').append(sr.get(key));
        }
        sb.append(appKey);
        if (DEBUG)
            log.debug("before sign: " + sb);
        String signature = Lang.md5(sb.toString());
        if (!signature.equals(sr.getString("signature")))
            throw new RuntimeException("bad resp signature");
    }
    
    protected SeckenResp _GET(String uri, NutMap params) {
        return _SEND(uri, params, METHOD.GET);
    }
    
    protected SeckenResp _POST(String uri, NutMap params) {
        return _SEND(uri, params, METHOD.POST);
    }
    
    protected SeckenResp _SEND(String url, NutMap params, METHOD method) {
        doSign(params);
        Request req = Request.create(url, method, params);
        req.getHeader().set("Sdk", "nutz-secken-" + version());
        if (method == METHOD.POST)
            req.getHeader().set("Content-Type", "application/x-www-form-urlencoded");
        Response resp = Sender.create(req, timeout).send();
        if (!resp.isOK()) {
            throw new RuntimeException("Secken Server return=" + resp.getStatus());
        }
        SeckenResp sr = Json.fromJson(SeckenResp.class, resp.getReader());
        if (sr.ok())
            checkSign(sr);
        return sr;
    }
    
    public static String version() {
        return "1.0";
    }
}
