package org.nutz.plugins.zdoc;

import org.nutz.lang.Lang;
import org.nutz.plugins.zdoc.html.Htmls;
import org.nutz.plugins.zdoc.markdown.LocalMarkdownDSetParser;

/**
 * 提供控制台命令。这个类也可以作为 zdoc 包的使用示例
 * 
 * <h4>转换 markdown 文档目录到一个 html 目录</h4>
 * 
 * <pre>
 * zdoc md2html /path/to/src /path/to/dest
 * </pre>
 * 
 * <h4>转换 markdown 文档目录到一个 word 文件</h4>
 * 
 * <pre>
 * zdoc md2word /path/to/src /path/to/dest.docx
 * </pre>
 * 
 * <h4>转换 markdown 文档目录到一个 PDF 文件</h4>
 * 
 * <pre>
 * zdoc md2pdf /path/to/src /path/to/dest.pdf
 * </pre>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class NutzDocMain {

    public static void main(String[] args) {
        // 分析参数
        String ctype = args[0];
        String phSrc = args[1];
        String phDst = args.length > 2 ? args[2] : null;
        String phCnf = args.length > 3 ? args[3] : null;

        // 转换 markdown 文档目录到一个 html 目录
        if ("md2html".equals(ctype)) {
            // 解析
            LocalMarkdownDSetParser dsp = new LocalMarkdownDSetParser();
            NutDSet dsHome = new NutDSet(null);
            dsp.parse(dsHome, phSrc, phCnf);

            // 渲染
            Htmls.renderDSet(dsHome, phDst);
        }
        // 转换 markdown 文档目录到一个 word 文件
        else if ("md2word".equals(ctype)) {
            throw Lang.noImplement();
        }
        // 转换 markdown 文档目录到一个 PDF 文件
        else if ("md2pdf".equals(ctype)) {
            throw Lang.noImplement();
        }
        // 靠，不支持
        else {
            throw Lang.makeThrow("e.zdoc.main.unknowCType : %s", ctype);
        }
    }

}
