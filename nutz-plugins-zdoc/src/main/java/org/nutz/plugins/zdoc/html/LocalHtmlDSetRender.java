package org.nutz.plugins.zdoc.html;

import java.io.File;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.tmpl.Tmpl;
import org.nutz.lang.util.Disks;
import org.nutz.plugins.zdoc.NutDSet;

public class LocalHtmlDSetRender extends AbstractHtmlDSetRender {

    private File dTa;

    private File dSrc;

    protected Tmpl loadTmpl(String tmplDirName, String tmplName, String dftTmplName) {
        Tmpl t = this.loadTmpl(Disks.appendPath(tmplDirName, tmplName));
        if (null == t) {
            File fT = Files.findFile("org/nutz/plugins/zdoc/html/" + dftTmplName);
            return Tmpl.parse(Files.read(fT));
        }
        return t;
    }

    protected void writeToTarget(String ph, String html) {
        File ta = Files.getFile(dTa, ph);
        Files.createFileIfNoExists(ta);
        Files.write(ta, html);
    }

    protected void copyResource(String rsph) {
        File src = Files.getFile(dSrc, rsph);
        if (src.exists()) {
            File dst = Files.getFile(dTa, rsph);
            // 不存在就创建
            if (!dst.exists()) {
                // 文件
                if (src.isFile()) {
                    dst = Files.createFileIfNoExists(dst);
                }
                // 目录
                else if (src.isDirectory()) {
                    dst = Files.createDirIfNoExists(dst);
                }
                // 不可能
                else {
                    throw Lang.impossible();
                }
            }
            // 执行 copy
            Files.copy(src, dst);
        }
    }

    protected void checkTarget(String target) {
        this.dTa = Files.createDirIfNoExists(target);
    }

    protected Tmpl loadTmpl(String tmplPath) {
        File fT = Files.getFile(dSrc, tmplPath);
        if (fT.exists())
            return Tmpl.parse(Files.read(fT));
        return null;
    }

    protected void checkPrimerObj(NutDSet ds) {
        this.dSrc = (File) ds.getPrimerObj();
        if (null == dSrc || !dSrc.exists() || !dSrc.isDirectory()) {
            throw Lang.makeThrow("e.zdoc.html.render.dsrc_invalid", dSrc);
        }
    }

}
