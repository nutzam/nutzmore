package org.nutz;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.nutz.lang.Encoding;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.lang.util.CmdParams;
import org.nutz.lang.util.Disks;
import org.nutz.lang.util.FileVisitor;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class AggregateMaker {
    
    private static final Log log = Logs.get();

    public static void main(String[] args) throws Throwable {
        final CmdParams params = CmdParams.parse(new String[]{}, null);
        final ZipOutputStream jar = new ZipOutputStream(new FileOutputStream("D:\\tmp\\nutzmore-aggregate-1.r.60.r2.jar"));
        final ZipOutputStream javadoc = new ZipOutputStream(new FileOutputStream("D:\\tmp\\nutzmore-aggregate-1.r.60.r2-javadoc.jar"));
        final ZipOutputStream sources = new ZipOutputStream(new FileOutputStream("D:\\tmp\\nutzmore-aggregate-1.r.60.r2-sources.jar"));
        Disks.visitFile(new File("C:\\Users\\wendal\\workspace\\git\\github\\nutzmore\\"), new FileVisitor() {
            public void visit(File f) {
                if (!f.isFile())
                    return;
                System.out.println(f);
                String name = f.getName();
                try {
                    if (name.endsWith("-sources.jar")) {
                        merge(params, new ZipInputStream(new FileInputStream(f), Encoding.CHARSET_UTF8), sources);
                    } else if (name.endsWith("-javadoc.jar")) {
                        merge(params, new ZipInputStream(new FileInputStream(f), Encoding.CHARSET_UTF8), javadoc);
                    } else{
                        merge(params, new ZipInputStream(new FileInputStream(f), Encoding.CHARSET_UTF8), jar);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new FileFilter() {
            public boolean accept(File f) {
                if (f.length() > 128*1024)
                    return false;
                if (f.isDirectory())
                    return true;
                if (!f.getParentFile().getName().equals("target"))
                    return false;
                String name = f.getName();
                if (!name.endsWith(".jar"))
                    return false;
                if (name.contains("multiview") || name.contains("mock") || name.contains("oauth2"))
                    return false;
                if (name.startsWith("nutz-plugins-"))
                    return true;
                if (name.startsWith("nutz-integration-"))
                    return true;
                return false;
            }
        });
        javadoc.finish();
        javadoc.flush();
        javadoc.close();
        sources.finish();
        sources.flush();
        sources.close();
        jar.finish();
        jar.flush();
        jar.close();
    }
    
    public static void merge(CmdParams params, String srcA, String srcB, String target) throws Exception {
        Files.createFileIfNoExists(target);

        ZipInputStream zin_b = new ZipInputStream(new FileInputStream(srcB), Encoding.CHARSET_UTF8);
        ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(target), Encoding.CHARSET_UTF8);
        
        merge(params, zin_b, zout);
        
        zout.flush();
        zout.finish();
        zout.close();
    }
    
    public static void merge(CmdParams params, ZipInputStream zin_b, ZipOutputStream zout) {
        try {
            while (true) {
                ZipEntry en = zin_b.getNextEntry();
                if (en == null)
                    break;
                if (params.is("debug"))
                    log.debug("add " + en.getName());
                try {
                    zout.putNextEntry(en);
                    Streams.write(zout, zin_b);
                    zout.closeEntry();
                }
                catch (Exception e) {
                    if (!en.getName().endsWith("/"))
                        log.info("dup ? " + en.getName());
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
