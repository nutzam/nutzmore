package org.nutz.plugins.view.thymeleaf;

import org.thymeleaf.dialect.IDialect;

public class ThymeleafProperties {
    private static final String DEFAULT_ENCODING = "UTF-8";

    private static final String DEFAULT_CONTENT_TYPE = "text/html";

    public static final String DEFAULT_PREFIX = "/WEB-INF/template/";

    public static final String DEFAULT_SUFFIX = ".html";

    private String prefix = DEFAULT_PREFIX;

    private String suffix = DEFAULT_SUFFIX;

    private String mode = "HTML5";

    private String encoding = DEFAULT_ENCODING;

    private String contentType = DEFAULT_CONTENT_TYPE;

    private boolean cache = true;

    private Long cacheTTLMs = 3600000L;

    private IDialect[] dialects = null;

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getMode() {
        return this.mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public boolean isCache() {
        return this.cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public Long getCacheTTLMs() {
        return cacheTTLMs;
    }

    public void setCacheTTLMs(Long cacheTTLMs) {
        this.cacheTTLMs = cacheTTLMs;
    }

    public IDialect[] getDialects() {
        return dialects;
    }

    public void setDialects(IDialect... dialects) {
        this.dialects = dialects;
    }
}
