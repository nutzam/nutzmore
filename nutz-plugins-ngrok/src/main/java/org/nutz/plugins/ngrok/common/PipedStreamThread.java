package org.nutz.plugins.ngrok.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

import org.nutz.lang.Stopwatch;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class PipedStreamThread implements Callable<String> {

    private static final Log log = Logs.get();

    protected InputStream ins;
    protected OutputStream out;
    protected String name;
    protected int bufSize;
    protected Stopwatch sw;
    protected int count;

    public PipedStreamThread(String name, InputStream ins, OutputStream out, int bufSize) {
        this.name = name;
        this.ins = ins;
        this.out = out;
        this.bufSize = bufSize;
    }

    public String call() throws Exception {
        byte[] buf = new byte[bufSize];
        if (log.isDebugEnabled())
            sw = Stopwatch.begin();
        while (true) {
            try {
                int len = ins.read(buf);
                //if (log.isTraceEnabled())
                //    log.tracef("%s read %s bytes", name, len);
                if (len > 0) {
                    this.out.write(buf, 0, len);
                    count += len;
                    this.out.flush();
                } else if (len < 0) {
                    //log.debug("break at len="+len);
                    break;
                }
            }
            catch (IOException e) {
                //log.debug("break at IOException");
                break;
            }
        }
        if (log.isDebugEnabled()) {
            sw.stop();
            if (log.isDebugEnabled())
                log.debugf("%s %dms %dkb", name, sw.getDuration(), count/1024);
        }
        return name;
    }

    public int getCount() {
        return count;
    }
}
