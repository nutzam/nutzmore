package org.nutz.plugins.view.pdf;

import java.io.File;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Encoding;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.View;

import com.google.typography.font.tools.sfnttool.SfntTool;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class PdfView implements View {

    private static final Log log = Logs.get();
    
    public static String defaultfontPath;

    static {
        String[] paths = new String[]{
                                      "fonts/pdf.ttc",
                                      "fonts/pdf.ttf",
                                      "C:\\windows\\fonts\\msyhl.ttc",
                                      "/usr/share/fonts/msyhl.ttc"
                                      };
        for (String path : paths) {
            try {
                if (new File(path).exists()) {
                    defaultfontPath = path;
                    log.debug("微软雅黑Light found");
                    break;
                }

            }
            catch (Exception e) {}
        }
    }

    public static BaseFont DEFAULT_FONT;

    protected PdfViewFormat format;

    public PdfView(String value) {
        if (Strings.isBlank(value))
            throw new IllegalArgumentException("pdf view need template file path");
        format = new PdfViewFormat();
        format.tmpl = value;
    }

    public PdfView(PdfViewFormat format) {
        super();
        this.format = format;
    }

    public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
            throws Throwable {
        File f = Files.findFile(this.format.tmpl);
        if (!f.exists()) {
            resp.sendError(404);
            return;
        }
        Context cnt = (Context) obj;
        resp.setContentType("application/pdf");
        if (!resp.containsHeader("Content-Disposition")) {
            String filename = URLEncoder.encode(cnt.getString("filename", "out.pdf"),
                                                Encoding.UTF8);
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        }
        PdfReader reader = new PdfReader(Streams.fileIn(f));
        OutputStream out = resp.getOutputStream();
        PdfStamper ps = new PdfStamper(reader, out);
        AcroFields fields = ps.getAcroFields();
        StringBuilder sb = new StringBuilder();
        for (String key : fields.getFields().keySet()) {
            sb.append(Strings.sBlank(cnt.get(key)));
        }
        BaseFont bf = subFont(format.fontPath == null ? defaultfontPath : format.fontPath, sb.toString());
        for (String key : fields.getFields().keySet()) {
            fields.setField(key, Strings.sBlank(cnt.get(key)));
            if (bf != null)
                fields.setFieldProperty(key, "textfont", bf, null);
        }
        ps.setFormFlattening(true);
        ps.close();
    }
    
    public static BaseFont subFont(String sourceFont, String strs) {
        if (sourceFont == null)
            return null;
        File f = null;
        try {
            f = File.createTempFile("nutz.pdfview.", ".ttf");
            SfntTool.main(new String[]{"-s", strs, sourceFont, f.getPath()});
            return BaseFont.createFont(f.getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        } finally {
            if (f != null)
                f.delete();
        }
    }
}
