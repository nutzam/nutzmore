package org.nutz.mongo.mr;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;

public class ZMoMapReduceManager {

    private Map<String, ZMoMapReduce> map;

    private String home;

    public ZMoMapReduceManager(String home) {
        this.home = (home.endsWith("/") ? home.substring(0, home.length() - 1)
                                       : home).replaceAll("[.\\\\]", "/");
        this.map = new HashMap<String, ZMoMapReduce>();
    }

    public ZMoMapReduce get(String key) {
        ZMoMapReduce mr = map.get(key);
        if (null == mr) {
            mr = syncGet(key);
        }
        return mr;
    }

    private synchronized ZMoMapReduce syncGet(String key) {
        ZMoMapReduce mr;
        mr = map.get(key);
        if (null == mr) {
            File f = Files.findFile(home + "/" + key + ".js");
            if (null != f) {
                try {
                    StringBuilder sb = new StringBuilder();
                    mr = new ZMoMapReduce();
                    mr.setKey(key);
                    BufferedReader br = Streams.buffr(Streams.fileInr(f));
                    String line;
                    // 首先得到 init obj
                    while (null != (line = br.readLine())) {
                        // 如果是 function 开头退出
                        if (line.startsWith("function("))
                            break;
                        // 去掉注释符
                        if (line.startsWith("//"))
                            line = line.substring(2);
                        // 累加
                        sb.append(line).append('\n');
                    }
                    mr.setInit(Strings.trim(sb));
                    // 继续读取函数
                    sb = new StringBuilder(line);
                    while (null != (line = br.readLine())) {
                        sb.append('\n').append(line);
                    }
                    mr.setReduceFunc(sb.toString());
                    // 加入缓存
                    map.put(key, mr);
                }
                catch (IOException e) {
                    throw Lang.wrapThrow(e);
                }
            }
        }
        return mr;
    }
}
