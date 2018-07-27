package org.nutz.plugins.zdoc.markdown;

import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.zdoc.NutD;
import org.nutz.plugins.zdoc.NutDSet;
import org.nutz.plugins.zdoc.NutDSetParser;
import org.nutz.plugins.zdoc.NutDoc;

public abstract class AbstractMarkdownDSetParser implements NutDSetParser {

    private static final Log log = Logs.get();

    /**
     * 子类来实现的，用来分析指定目录的文档集的目录信息。
     * <p>
     * 子类可以从文件磁盘，数据库等任何地方根据给定的路径读取信息
     * <p>
     * <b>!!注意</b>本类非线程安全
     * 
     * @param home
     *            顶级文档集节点
     * @param path
     *            资源路径
     * @param configPath
     *            配置文件路径
     * 
     * @return 文档集的配置项
     * 
     * @see org.nutz.plugins.zdoc.NutDSetInfo
     */
    protected abstract NutMap loadConfig(NutDSet home, String path, String configPath);

    protected abstract void checkPath(NutDSet home, String path);

    protected abstract void loadTreeByPaths(NutDSet home, String paths);

    protected abstract void loadTreeByRecur(NutDSet home);
    
    protected void loadTreeByPath(NutDSet home, String path) {
        
    }

    @Override
    public void parse(NutDSet home, String path, String configPath) {
        // 检查源对象
        log.infof("checkPath: %s", path);
        this.checkPath(home, path);

        // 初始化目录结构
        log.infof("loadConfig: %s", configPath);
        NutMap conf = this.loadConfig(home, path, configPath);

        // 检查一下，是否配置项指定了路径
        String paths = conf.getString("paths");

        // 指定了路径，则用指定路径加载
        if (!Strings.isBlank(paths)) {
            log.info("loadTreeByPaths:");
            this.loadTreeByPaths(home, paths);
        }
        // 否则采用递归加载
        else {
            log.info("loadTreeByRecur:");
            this.loadTreeByRecur(home);
        }

        // 将所有配置项目非 _ 开头的，全部加入文档集元数据
        conf.remove("paths");
        for (String key : conf.keySet()) {
            if (null != key && !key.startsWith("_")) {
                home.getMeta().put(key, conf.get(key));
            }
        }
        log.info(Json.toJson(home.getMeta()));

        // 依次处理
        for (NutD d : home.getChildren()) {
            __parse_it(d);
        }
    }

    private void __parse_it(NutD d) {
        // 如果是 Markdown 文件，则解析
        if (d instanceof NutDoc) {
            MarkdownDocParser dp = new MarkdownDocParser();
            NutDoc doc = (NutDoc) d;
            log.info(" - parse : " + doc.getPath());
            dp.parse(doc);
        }
        // 如果是目录，则递归
        else if (d instanceof NutDSet) {
            for (NutD d2 : ((NutDSet) d).getChildren()) {
                __parse_it(d2);
            }
        }
        // 不可能
        else {
            throw Lang.impossible();
        }
    }

}
