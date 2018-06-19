package org.nutz.http.sender.jetty;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.client.util.MultiPartContentProvider;
import org.eclipse.jetty.client.util.PathContentProvider;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpField;
import org.nutz.http.Header;
import org.nutz.http.HttpException;
import org.nutz.http.Request;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.lang.Strings;

public class JettySender extends Sender {

    protected HttpClient client;

    protected JettySender(HttpClient client, Request request) {
        super(request);
        this.client = client;
    }

    @Override
    public Response send() throws HttpException {
        org.eclipse.jetty.client.api.Request req = createJettyRequest();
        try {
            final ContentResponse jettyResp = req.send();
            Response resp = new Response() {

                public String getContent(String charsetName) {
                    try {
                        return new String(jettyResp.getContent(), Strings.sBlank(charsetName, "UTF-8"));
                    }
                    catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }

                public Header getHeader() {
                    Header header = Header.create();
                    header.clear();
                    for (HttpField jettyHeader : jettyResp.getHeaders()) {
                        header.set(jettyHeader.getName(), jettyHeader.getValue());
                    }
                    return header;
                }

                public Reader getReader(String charsetName) {
                    try {
                        return new InputStreamReader(new ByteArrayInputStream(jettyResp.getContent()), charsetName);
                    }
                    catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }
                public String getEncode() {
                    return jettyResp.getEncoding();
                }
                public int getStatus() {
                    return jettyResp.getStatus();
                }
                public String getDetail() {
                    return jettyResp.getReason();
                }
                public String getProtocal() {
                    return jettyResp.getVersion().asString();
                }
                @Override
                public String getEncodeType() {
                    return jettyResp.getMediaType();
                }
            };
            return resp;
        }
        catch (InterruptedException | TimeoutException | ExecutionException e) {
            throw new HttpException(req.getURI().toString(), e);
        }
    }

    public org.eclipse.jetty.client.api.Request createJettyRequest() {
        try {
            org.eclipse.jetty.client.api.Request req = client.newRequest(request.getUrl().toString());
            // 设置请求的方法
            if (Strings.isBlank(request.getMethodString()))
                req.method(request.getMethod().toString());
            else
                req.method(request.getMethodString());
            // 设置header
            Header header = request.getHeader();
            for (String headerName : header.keys()) {
                req.header(headerName, header.get(headerName));
            }
            // 处理一下请求的参数
            if (request.getData() != null) {
                req.content(new BytesContentProvider(request.getData()));
            } else if (request.getParams() != null) {
                // 首先, 看看是不是文件上传
                boolean hasFile = false;
                for (Object value : request.getParams().values()) {
                    if (value != null && (value instanceof File || value instanceof File[])) {
                        hasFile = true;
                    }
                }
                if (hasFile) {
                    // 文件上传需要用MultiPartContentProvider
                    MultiPartContentProvider multiPart = new MultiPartContentProvider();
                    for (Map.Entry<String, Object> en : request.getParams().entrySet()) {
                        String key = en.getKey();
                        Object value = en.getValue();
                        if (value != null) {
                            if (value instanceof File) {
                                File f = (File) value;
                                multiPart.addFilePart(key, f.getName(), new PathContentProvider(Paths.get(f.getAbsolutePath())), null);
                            } else if (value instanceof File[]) {
                                for (File f : (File[]) value) {
                                    multiPart.addFilePart(key, f.getName(), new PathContentProvider(Paths.get(f.getAbsolutePath())), null);
                                }
                            } else {
                                multiPart.addFieldPart(key, new StringContentProvider(String.valueOf(value), request.getEnc()), null);
                            }
                        }
                    }
                    req.content(multiPart);
                } else {
                    // 非文件上传,走BytesContentProvider完事
                    ContentProvider cv = new BytesContentProvider(request.getURLEncodedParams().getBytes(request.getEnc()));
                    req.content(cv, "application/x-www-form-urlencoded; charset=" + request.getEnc().toUpperCase());
                }
            }
            // 设置一下超时
            req.timeout(getTimeout(), TimeUnit.MILLISECONDS);
            req.followRedirects(followRedirects);
            return req;
        }
        catch (UnsupportedEncodingException e) {
            throw new HttpException(request.getUrl().toString(), e);
        }
        catch (IOException e) {
            throw new HttpException(request.getUrl().toString(), e);
        }
    }

}
