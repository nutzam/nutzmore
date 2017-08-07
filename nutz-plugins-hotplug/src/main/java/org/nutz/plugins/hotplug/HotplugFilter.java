package org.nutz.plugins.hotplug;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Each;
import org.nutz.lang.Encoding;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;

public class HotplugFilter implements Filter {

    protected String prefix;

    public void init(FilterConfig filterConfig) throws ServletException {
        prefix = "/assets/";
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Thread.currentThread().setContextClassLoader(Hotplug.me().hpcl);
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getPathInfo();
        if (path == null)
            path = req.getServletPath();
        if (path.startsWith(prefix)) {
            String tmp = path.substring(1);
            File f = Hotplug.find(tmp);
            if (f != null) {
                setContentType(path, resp);
                resp.getOutputStream().write(Files.readBytes(f));
                return;
            }
            String _etag = req.getHeader("If-None-Match");
            for (HotplugConfig hc : Hotplug.getActiveHotPlugList()) {
                // String key = en.getKey();
                Map<String, HotplugAsset> asserts = hc.assets;
                HotplugAsset asset = asserts.get(tmp);
                if (asset == null) {
                    InputStream ins = hc.classLoader.getResourceAsStream(tmp);
                    if (ins != null) {
                        asset = new HotplugAsset(Streams.readBytesAndClose(ins));
                    }
                }
                if (asset != null) {
                    if (asset.etag.equals(_etag)) {
                        resp.setStatus(304);
                        return;
                    }
                    resp.setHeader("ETag", asset.etag);
                    setContentType(path, resp);
                    resp.getOutputStream().write(asset.buf);
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }

    public void destroy() {}

    protected static final Map<String, String> contentTypeMap = new HashMap<String, String>();

    static {
        InputStream ins = Hotplug.class.getResourceAsStream("mime.types");
        Reader reader = new InputStreamReader(ins, Encoding.CHARSET_UTF8);
        Streams.eachLine(reader, new Each<String>() {
            public void invoke(int index, String line, int count) {
                line = line.trim();
                if (!line.endsWith(";"))
                    return;
                line = line.substring(0, line.length() - 1);
                String[] tmp = line.split(" ", 2);
                String type = tmp[0];
                tmp = Strings.splitIgnoreBlank(tmp[1], " ");
                for (int i = 0; i < tmp.length; i++) {
                    contentTypeMap.put(tmp[i].trim(), type);
                }
            }
        });
    }

    protected void setContentType(String path, HttpServletResponse resp) {
        String suffix = Files.getSuffix(path);
        if (Strings.isBlank(suffix))
            return;
        suffix = suffix.substring(1);
        String type = contentTypeMap.get(suffix);
        if (type != null)
            resp.setContentType(type);
    }
}
