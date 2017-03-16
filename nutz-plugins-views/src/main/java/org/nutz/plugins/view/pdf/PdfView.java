package org.nutz.plugins.view.pdf;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Encoding;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.Callback;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.View;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;

import com.google.typography.font.tools.sfnttool.SfntTool;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class PdfView implements View {

    private static final Log log = Logs.get();
    
    protected static byte[] defaultFontData;
    
    public static void setDefaultFontData(byte[] defaultFontData) {
        PdfView.defaultFontData = defaultFontData;
    }
    
    public static void setDefaultFontPath(String path) {
        if (path == null)
            defaultFontData = null;
        else
            defaultFontData = Files.readBytes(path);
    }

    static {
        String[] paths = new String[]{
                                      "fonts/pdf.ttc",
                                      "fonts/pdf.ttf",
                                      "C:\\windows\\fonts\\msyhl.ttc",
                                      "/usr/share/fonts/msyhl.ttc",
                                      "/System/Library/Fonts/msyhl.ttc"
                                      };
        for (String path : paths) {
            try {
                if (new File(path).exists()) {
                    setDefaultFontPath(path);
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

    @SuppressWarnings("unchecked")
    public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
            throws Throwable {
        InputStream ins;
        File f = Files.findFile(this.format.tmpl);
        if (f == null) {
            List<NutResource> resources = Scans.me().scan(this.format.tmpl);
            if (resources.isEmpty()) {
                log.info("pdf tmpl not found --> " + format.tmpl);
                resp.sendError(404);
                return;
            }
            ins = resources.get(0).getInputStream();
        } else {
            ins = Streams.fileIn(f);
        }
        try {
            Context cnt = (Context) obj;
            resp.setContentType("application/pdf");
            if (!resp.containsHeader("Content-Disposition") && !cnt.getBoolean("*viewOnly")) {
                String filename = URLEncoder.encode(cnt.getString("filename", "out.pdf"),
                                                    Encoding.UTF8);
                resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            }
            PdfReader reader = new PdfReader(ins);
            OutputStream out = resp.getOutputStream();
            PdfStamper ps = new PdfStamper(reader, out);
            AcroFields fields = ps.getAcroFields();
            StringBuilder sb = new StringBuilder();
            for (String key : fields.getFields().keySet()) {
                sb.append(Strings.sBlank(cnt.get(key)));
            }
            BaseFont bf = subFont(format.fontData, sb.toString());
            for (String key : fields.getFields().keySet()) {
                fields.setField(key, Strings.sBlank(cnt.get(key)));
                if (bf != null)
                    fields.setFieldProperty(key, "textfont", bf, null);
            }
            ps.setFormFlattening(true);
            Callback<PdfStamper> callback = cnt.getAs(Callback.class, "*callback");
            if (callback != null)
                callback.invoke(ps);
            ps.close();
        }
        finally {
            Streams.safeClose(ins);
        }
    }
    
    public static BaseFont subFont(byte[] source, String strs) {
        if (source == null)
            source = defaultFontData;
        if (source == null)
            return null;
        try {
            byte[] buf = SfntTool.sub(source, strs, false);
            return BaseFont.createFont("pdfview."+R.UU32()+".ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, false, buf, null);
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }
}
