package org.nutz.plugins.view.velocity;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.io.VelocityWriter;
import org.apache.velocity.util.SimplePool;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.view.ForwardView;

/**
 * Created by Wizzer on 14-9-23.
 * Modify by wendal on 15-5-20
 */
public class VelocityLayoutView extends ForwardView {

    private static final Log log = Logs.get();
    
    protected static final int WRITER_BUFFER_SIZE = 8 * 1024;
    protected SimplePool writerPool = new SimplePool(40);


    public VelocityLayoutView(String dest) {
        super(dest);
    }

    public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Exception {
        String path = evalPath(req, obj);
        resp.setContentType("text/html;charset=\"UTF-8\"");
        resp.setCharacterEncoding("UTF-8");
        try {
            StringWriter sw = new StringWriter();
            org.nutz.lang.util.Context ctx = super.createContext(req, obj);
            Context context = new VelocityContext();
            for (Entry<String, Object> en : ctx.getInnerMap().entrySet()) {
                context.put(en.getKey(), en.getValue());
            }
            log.debug("Path::"+ path);
            Template template = Velocity.getTemplate(path);
            template.merge(context, sw);
            internalRenderTemplate(template, context, resp.getWriter());
        } catch (Exception e) {
            log.error("模板引擎错误", e);
            throw e;
        }
    }
    
    protected String getExt() {
        return ".vm";
    }

    private void internalRenderTemplate(Template template, Context context, Writer writer) throws Exception {
        VelocityWriter velocityWriter = null;
        try {
            velocityWriter = (VelocityWriter) writerPool.get();
            if (velocityWriter == null) {
                velocityWriter = new VelocityWriter(writer, WRITER_BUFFER_SIZE, true);
            } else {
                velocityWriter.recycle(writer);
            }
            template.merge(context, velocityWriter);
        } catch (Exception pee) {
            throw new Exception(pee);
        } finally {
            if (velocityWriter != null) {
                velocityWriter.flush();
                velocityWriter.recycle(null);
                writerPool.put(velocityWriter);
            }
            writer.flush();
            writer.close();
        }
    }
}