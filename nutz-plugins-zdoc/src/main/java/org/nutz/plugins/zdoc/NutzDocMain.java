package org.nutz.plugins.zdoc;

import org.nutz.lang.Lang;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.Times;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.zdoc.html.Htmls;
import org.nutz.plugins.zdoc.markdown.LocalMarkdownDSetParser;
import org.nutz.plugins.zdoc.msword.MsWords;

/**
 * 提供控制台命令。这个类也可以作为 zdoc 包的使用示例
 * 
 * <h4>转换 markdown 文档目录到一个 html 目录</h4>
 * 
 * <pre>
 * zdoc md2html /path/to/src /path/to/dest
 * </pre>
 * 
 * <h4>转换 markdown 文档目录到一个 word2007 文件</h4>
 * 
 * <pre>
 * zdoc md2docx /path/to/src /path/to/dest.docx
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

    private static final Log log = Logs.get();

    public static void main(String[] args) {
        if (args == null || args.length < 2) {
            usage();
            return;
        }
        // 分析参数
        String ctype = args[0];
        String phSrc = args[1];
        String phDst = args.length > 2 ? args[2] : null;
        String phCnf = args.length > 3 ? args[3] : null;

        // 开始计时
        Stopwatch sw = Stopwatch.begin();

        // 转换 markdown 文档目录到一个 html 目录
        if ("md2html".equals(ctype)) {
            // 解析
            LocalMarkdownDSetParser dsp = new LocalMarkdownDSetParser();
            NutDSet dsHome = new NutDSet(null);
            dsp.parse(dsHome, phSrc, phCnf);

            // 渲染
            Htmls.renderDSet(dsHome, phDst);
        }
        // 转换 markdown 文档目录到一个 word2007 文件
        else if ("md2docx".equals(ctype)) {
            // 解析
            LocalMarkdownDSetParser dsp = new LocalMarkdownDSetParser();
            NutDSet dsHome = new NutDSet(null);
            dsp.parse(dsHome, phSrc, phCnf);

            // 渲染
            MsWords.renderDSet(dsHome, phDst);
        }
        // 转换 markdown 文档目录到一个 PDF 文件
        else if ("md2pdf".equals(ctype)) {
            throw Lang.noImplement();
        }
        // 靠，不支持
        else {
            throw Lang.makeThrow("e.zdoc.main.unknowCType : %s", ctype);
        }

        // 打印结果
        sw.stop();
        log.infof("All done in %s.\n%s", Times.mss((int) sw.du()), sw.toString());
    }

    protected static void usage() {
        System.out.println("ZDoc Usage");
        System.out.println("  zdoc [md2html|md2docx|md2pdf] source dest");
    }
}
