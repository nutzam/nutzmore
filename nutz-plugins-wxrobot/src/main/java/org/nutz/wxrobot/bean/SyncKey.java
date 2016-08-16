package org.nutz.wxrobot.bean;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;

public class SyncKey {
    public int Count;
    public List<Map<String, Long>> List;

    /**
     * 返回符合微信指定格式 1_124125|2_452346345|3_65476547|1000_5643635
     */
    public String toString() {
        StringBuilder synckey = new StringBuilder();
        for (Map<String, Long> item : List) {
            synckey.append("|" + item.get("Key") + "_" + item.get("Val"));
        }
        return synckey.substring(1);
    }

    public String toEncodeString() {
        String sk = toString();
        try {
            return URLEncoder.encode(sk, Encoding.CHARSET_UTF8.name());
        }
        catch (UnsupportedEncodingException e) {
            throw Lang.wrapThrow(e);
        }
    }
}
