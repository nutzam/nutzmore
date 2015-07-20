package org.nutz.plugins.protobuf;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.PairAdaptor;

public class ProtobufAdaptor extends PairAdaptor {
    
    private static final Log log = Logs.get();

    protected Object getReferObject(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, String[] pathArgs) {
        try {
            Object obj = null;
            InputStream ins = req.getInputStream();
            // 开始解析输入流, TLD格式? or any other...
            
            //
            return obj;
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

}
