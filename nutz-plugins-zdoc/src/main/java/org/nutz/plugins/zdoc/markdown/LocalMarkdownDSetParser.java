package org.nutz.plugins.zdoc.markdown;

import java.io.BufferedInputStream;
import java.io.File;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.zdoc.NutD;
import org.nutz.plugins.zdoc.NutDSet;
import org.nutz.plugins.zdoc.NutDoc;

/**
 * 从本地目录加载，有两种加载方式:
 * 
 * <h3>默认加载</h3>
 * <p>
 * 没啥好说的，就是一层层递归目录呗
 * 
 * <h3>配置文件指定加载</h3>
 * <p>
 * 默认会读取 nutzdoc.conf 文件，文件的格式类似：
 * 
 * <pre>
 * #------------------------------------------
 * # 集合的标题
 * title=这是本集合的整体标题
 * #------------------------------------------
 * # 集合的作者，多个作者半角逗号隔开
 * authors=xiaobai,xiaohei
 * #------------------------------------------
 * # 关键是下面的路径，如果没声明，则全查
 * # 每行格式为  路径 [: 标题]
 * paths:
 * a/b/c   : 一个集合
 * bbb.md  : 一个文档
 * # 结束
 * #------------------------------------------
 * ... 其他自定义属性
 * </pre>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class LocalMarkdownDSetParser extends AbstractMarkdownDSetParser {

    private static final Log log = Logs.get();

    @Override
    protected void checkPath(NutDSet home, String path) {
        // 得到主目录
        File dHome = Files.findFile(path);
        if (null == dHome)
            throw Lang.makeThrow("e.zdoc.noexist", path);

        if (!dHome.isDirectory())
            throw Lang.makeThrow("e.zdoc.shouldBeDir", path);

        // 设置源对象
        home.setPrimerObj(dHome);
    }

    @Override
    protected NutMap loadConfig(NutDSet home, String path, String configPath) {
        // 得到主目录
        File dHome = (File) home.getPrimerObj();

        // 试图得到配置信息
        NutMap conf = new NutMap();
        File fConf = Files.getFile(dHome, Strings.sBlank(configPath, "zdoc.conf"));
        if (fConf.exists()) {
            BufferedInputStream ins = Streams.buff(Streams.fileIn(fConf));
            PropertiesProxy pp = new PropertiesProxy(ins);
            conf.putAll(pp);
        }

        // 返回配置信息
        return conf;
    }

    @Override
    protected void loadTreeByRecur(NutDSet home) {
        __load_recur(home);
    }

    private int __load_recur(NutDSet ds) {
        File dir = (File) ds.getPrimerObj();

        // 遍历子目录/文件
        File[] fs = dir.listFiles();

        // 对自己所有的子孙叶子节点进行计数
        int c_doc_nb = 0;

        // 计算一共增加了几个项目
        for (File f : fs) {
            // 隐藏文件无视
            if (f.isHidden())
                continue;

            // 下划线开头的也无视
            String fnm = f.getName();
            if (fnm.startsWith("_"))
                continue;

            // 目录的话递归
            if (f.isDirectory()) {
                NutDSet dsub = ds.createSet(fnm);
                dsub.setPrimerObj(f);
                c_doc_nb += __load_recur(dsub);
            }
            // 文件的话，看看是不是 markdown
            else if (fnm.endsWith(".md") || fnm.endsWith(".markdown")) {
                NutDoc doc = ds.createDoc(fnm);
                doc.setPrimerObj(f);
                doc.setPrimerContent(Files.read(f));
                c_doc_nb++;
            }
        }

        // 如果为空，那么不是主目录就移除
        if (c_doc_nb == 0 && !ds.isRoot()) {
            ds.remove();
        }

        // 返回叶子节点点计数
        return c_doc_nb;
    }

    protected void loadTreeByPaths(NutDSet home, String paths) {
        File dHome = (File) home.getPrimerObj();
        String[] lines = Strings.splitIgnoreBlank(paths, "\r?\n");

        if (null != lines) {
            for (String line : lines) {
                String[] ss = Strings.splitIgnoreBlank(line, ":");
                String ph = ss[0];
                NutD d;

                // 看看这个文件是路径还是目录
                File f = Files.getFile(dHome, ph);

                // 不存在的话，警告一下
                if (!f.exists()) {
                    log.warnf("NutD noexists '%s'", ph);
                    continue;
                }

                // 如果是文件读取文件原始内容
                if (f.isFile()) {
                    NutDoc doc = home.createDocByPath(ph, true);
                    doc.setPrimerContent(Files.read(f));
                    doc.setDateTime(f.lastModified());
                    d = doc;
                }
                // 目录
                else if (f.isDirectory()) {
                    d = home.createSetByPath(ph, true);
                }
                // 不可能
                else {
                    throw Lang.impossible();
                }

                // 记录原始内容
                d.setPrimerObj(f);

                // 最后，看看是否需要设置标题
                if (ss.length > 1)
                    d.setTitle(ss[1]);
            }
        }
    }

}
