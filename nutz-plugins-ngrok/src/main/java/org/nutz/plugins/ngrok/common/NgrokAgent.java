package org.nutz.plugins.ngrok.common;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.nutz.http.Http;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Encoding;
import org.nutz.lang.Mirror;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class NgrokAgent {

    private static final Log log = Logs.get();

    public static void writeMsg(OutputStream out, NgrokMsg msg) throws IOException {
        synchronized (out) {
            String type = (String) msg.remove("Type");
            NutMap map = new NutMap("Type", type).setv("Payload", msg);
            String cnt = Json.toJson(map, JsonFormat.tidy().setQuoteName(true));
            if (log.isDebugEnabled() && !"Ping".equals(type) && !"Pong".equals(type))
                log.debug("write msg = " + cnt);
            byte[] buf = cnt.getBytes(Encoding.CHARSET_UTF8);
            int len = buf.length;
            out.write(longToBytes(len, 0));
            out.write(buf);
            out.flush();
        }
    }

    @SuppressWarnings("unchecked")
    public static NgrokMsg readMsg(InputStream ins) throws IOException {
        DataInputStream dis = new DataInputStream(ins);
        byte[] lenBuf = new byte[8];
        dis.readFully(lenBuf);
        int len = (int) bytes2long(leTobe(lenBuf, 8), 0);
        byte[] buf = new byte[len];
        dis.readFully(buf);
        String cnt = new String(buf, Encoding.CHARSET_UTF8);
        NutMap map = Json.fromJson(NutMap.class, cnt);
        NgrokMsg msg = new NgrokMsg();
        msg.setv("Type", map.getString("Type"));
        Map<String, Object> payload = map.getAs("Payload", Map.class);
        if (payload == null)
            payload = new HashMap<String, Object>();
        if (log.isDebugEnabled() && !"Pong".equals(msg.get("Type")) && !"Ping".equals(msg.get("Type")))
            log.debug("read msg = " + cnt);
        msg.putAll(payload);
        return msg;
    }
    
    public static int readLen(InputStream ins) throws IOException {
        DataInputStream dis = new DataInputStream(ins);
        byte[] lenBuf = new byte[8];
        dis.readFully(lenBuf);
        return (int) bytes2long(leTobe(lenBuf, 8), 0);
    }
    
    @SuppressWarnings("unchecked")
    public static NgrokMsg readMsg(String cnt) throws IOException {
        NutMap map = Json.fromJson(NutMap.class, cnt);
        NgrokMsg msg = new NgrokMsg();
        msg.setv("Type", map.getString("Type"));
        Map<String, Object> payload = map.getAs("Payload", Map.class);
        if (payload == null)
            payload = new HashMap<String, Object>();
        if (log.isDebugEnabled() && !"Pong".equals(msg.get("Type")) && !"Ping".equals(msg.get("Type")))
            log.debug("read msg = " + cnt);
        msg.putAll(payload);
        return msg;
    }

    // 下面几个方法来自 https://github.com/dosgo/ngrok-java

    // 转大端
    protected static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(0, x);
        return buffer.array();
    }

    // 转小端
    protected static byte[] longToBytes(long x, int pos) {

        byte[] bytes = longToBytes(x);
        byte[] back = new byte[8];
        // 山寨方法
        for (int i = 0; i < 8; i++) {
            back[i] = bytes[(7 - i)];
        }
        return back;
    }

    protected static byte[] leTobe(byte[] src, int len) {
        byte[] back = new byte[len];
        // 山寨方法
        for (int i = 0; i < len; i++) {
            back[i] = src[(len - 1 - i)];
        }
        return back;
    }

    protected static long bytes2long(byte[] array, int offset) {
        if (array.length < 8) {
            return 0;
        }

        return ((((long) array[offset + 0] & 0xff) << 56)
                | (((long) array[offset + 1] & 0xff) << 48)
                | (((long) array[offset + 2] & 0xff) << 40)
                | (((long) array[offset + 3] & 0xff) << 32)
                | (((long) array[offset + 4] & 0xff) << 24)
                | (((long) array[offset + 5] & 0xff) << 16)
                | (((long) array[offset + 6] & 0xff) << 8)
                | (((long) array[offset + 7] & 0xff) << 0));
    }

    public static void pipe2way(ExecutorService executorService,
                         InputStream fromA,
                         OutputStream toA,
                         InputStream fromB,
                         OutputStream toB,
                         int bufSize) throws Exception {
        PipedStreamThread srv2loc = new PipedStreamThread("srv2loc", fromA, toB, bufSize);
        // 本地-->服务器
        PipedStreamThread loc2srv = new PipedStreamThread("loc2srv", fromB, toA, bufSize);
        // 等待其中任意一个管道的关闭
        String exitFirst = executorService.invokeAny(Arrays.asList(srv2loc, loc2srv));
        if (log.isDebugEnabled())
            log.debug("proxy conn exit first at " + exitFirst);
    }
    
    public static boolean fixFromArgs(Object obj, String[] args) {
        Mirror<?> mirror = Mirror.me(obj);
        for (String arg : args) {
            if (!arg.startsWith("-") || !arg.contains("=")) {
                log.debug("bad arg = " + arg);
                return false;
            }
            arg = arg.substring(1);
            String[] tmp = arg.split("=", 2);
            if ("conf_file".equals(tmp[0])) {
                PropertiesProxy cpp = new PropertiesProxy(tmp[1]);
                for (String key : cpp.keySet()) {
                    log.debugf("config key=%s value=%s", key, cpp.get(key));
                    mirror.setValue(obj, key, cpp.get(key));
                }
            } else {
                log.debugf("config key=%s value=%s", tmp[0], tmp[1]);
                mirror.setValue(obj, tmp[0], tmp[1]);
            }
        }
        return true;
    }
    
    public static OutputStream gzip_out(boolean enable, OutputStream out) throws IOException {
        if (enable)
            return new GZIPOutputStream(out);
        return out;
    }
    
    public static InputStream gzip_in(boolean enable, InputStream ins) throws IOException {
        if (enable)
            return new GZIPInputStream(ins);
        return ins;
    }
    
    public static void httpResp(OutputStream out , int code, String cnt) {
        try {
            byte[] buf = cnt.getBytes();
            String respLine = String.format("HTTP/1.0 %d %s\r\n", code, Http.getStatusText(code, ""));
            String content_len = "Content-Length: " + buf.length + "\r\n";
            out.write(respLine.getBytes());
            out.write(content_len.getBytes());
            out.write("\r\n".getBytes());
            out.write(buf);
            out.flush();
            out.close();
        }
        catch (IOException e) {
        }
    }
}
