package org.nutz.plugins.zdoc.markdown;

import java.io.File;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.plugins.zdoc.NutDSet;
import org.nutz.plugins.zdoc.NutDSetConfig;

/**
 * 从本地目录加载，有两种加载方式:
 * 
 * <h3>默认加载
 * <h3>没啥好说的，就是一层层递归目录呗
 * 
 * <h3>配置文件指定加载</h3> 会读取 zdoc.conf 文件，文件的格式类似：
 * 
 * <pre>
 * #------------------------------------------
 * # 井号开头的是注释行
 * title=这是本目录的标题
 * #------------------------------------------
 * # 关键是下面的路径，如果没声明，则全查
 * # 每行格式为  路径 [: 标题]
 * paths:
 * a/b/c   : 一个集合
 * bbb.md  : 一个文档
 * # 结束
 * #------------------------------------------
 * </pre>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class LocalMarkdownDSetParser extends MarkdownDSetParser {

    @Override
    protected NutDSetConfig getConfig(NutDSet home, String path) {
        // 得到主目录
        File dHome = Files.findFile(path);
        if (null == dHome)
            throw Lang.makeThrow("e.zdoc.noexist", path);

        // 准备返回值
        NutDSetConfig dc = new NutDSetConfig();

        // 试图得到配置信息
        File fConf = Files.getFile(dHome, "zdoc.conf");

        // 如果有，就用配置信息来
        if (fConf.exists()) {

        }
        // 否则递归来
        else {

        }

        // 返回
        return dc;
    }

}
