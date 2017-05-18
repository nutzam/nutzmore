package org.nutz.plugins.zdoc.markdown;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.plugins.zdoc.NutD;
import org.nutz.plugins.zdoc.NutDSet;
import org.nutz.plugins.zdoc.NutDSetConfig;
import org.nutz.plugins.zdoc.NutDSetParser;
import org.nutz.plugins.zdoc.NutDoc;

public abstract class MarkdownDSetParser implements NutDSetParser {

    protected abstract NutDSetConfig getConfig(NutDSet home, String path);

    @Override
    public void parse(NutDSet home, String path) {
        // 确保设置名称
        home.setName(Files.getMajorName(path));

        // 得到目录配置信息
        NutDSetConfig conf = this.getConfig(home, path);

        // 设置标题
        home.getMeta().put("title", conf.getTitle());

        // 依次处理
        for (NutD d : conf.getList()) {
            __parse_it(d);
        }
    }

    public void __parse_it(NutD d) {
        // 如果是 Markdown 文件，则解析
        if (d instanceof NutDoc) {
            MarkdownDocParser dp = new MarkdownDocParser();
            dp.parse((NutDoc) d);
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
