package org.nutz.plugins.zdoc.markdown;

import org.nutz.lang.util.Callback;
import org.nutz.lang.util.Tag;
import org.nutz.plugins.zdoc.NutDoc;

/**
 * 封装所有的和 Markdown 相关的操作
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Markdown {

    /**
     * 将 Markdown 文本直接转换成 HTML
     * 
     * @param markdown
     *            Markdown 文本
     * @param tagWatcher
     *            每次输出一个节点前，可以对节点进行修改的观察者
     * @return 转换成的 HTML 代码
     */
    public static String toHtml(String markdown, Callback<Tag> tagWatcher) {
        NutDoc d = new NutDoc(null);
        d.setPrimerContent(markdown);
        parse(d);
        return d.getRootTag().toInnerHtml(true, tagWatcher);
    }

    /**
     * @see #toHtml(String, Callback)
     */
    public static String toHtml(String markdown) {
        return toHtml(markdown, null);
    }

    /**
     * 将 Markdown 文本填充到给定的文档对象
     * 
     * @param d
     *            设置了原始内容后的文档对象
     */
    public static void parse(NutDoc d) {
        MarkdownDocParser p = new MarkdownDocParser();
        p.parse(d);
    }

}
