package org.nutz.plugins.view.pdf;

import java.io.File;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Encoding;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.View;

import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class PdfView implements View {

    private static final Log log = Logs.get();

    static {
        String[] paths = new String[]{"C:\\windows\\fonts\\msyhl.ttc",
                                      "/usr/share/fonts/msyhl.ttc"};
        for (String path : paths) {
            try {
                if (new File(path).exists()) {
                    FontFactory.register(path, "msyh");
                    DEFAULT_FONT = BaseFont.createFont(path+ ",1",
                                                       BaseFont.IDENTITY_H,
                                                       BaseFont.NOT_EMBEDDED);
                    log.debug("微软雅黑Light Loaded");
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
        BaseFont bf = format.font;
        if (bf == null)
            bf = DEFAULT_FONT;
        ArrayList<BaseFont> _bf = new ArrayList<BaseFont>();
        _bf.add(bf);
        PdfReader reader = new PdfReader(Streams.fileIn(f));
        OutputStream out = resp.getOutputStream();
        PdfStamper ps = new PdfStamper(reader, out);
        AcroFields fields = ps.getAcroFields();
        // fields.setSubstitutionFonts(_bf);
        for (String key : fields.getFields().keySet()) {
            fields.setField(key, Strings.sBlank(cnt.get(key)));
            fields.setFieldProperty(key, "textfont", bf, null);
        }
        ps.setFormFlattening(true);
        ps.close();
    }

}
