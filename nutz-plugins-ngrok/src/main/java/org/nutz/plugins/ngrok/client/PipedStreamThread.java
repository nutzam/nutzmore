package org.nutz.plugins.ngrok.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

import org.nutz.log.Log;
import org.nutz.log.Logs;

public class PipedStreamThread implements Callable<Boolean> {

    private static final Log log = Logs.get();

    protected InputStream ins;
    protected OutputStream out;
    protected String name;
    protected int bufSize;

    public PipedStreamThread(String name, InputStream ins, OutputStream out, int bufSize) {
        this.name = name;
        this.ins = ins;
        this.out = out;
        this.bufSize = bufSize > 0 ? bufSize : 16*1024;
    }

    public Boolean call() throws Exception {
        byte[] buf = new byte[bufSize];
        while (true) {
            try {
                int len = ins.read(buf);
                if (log.isTraceEnabled())
                    log.tracef("%s read %s bytes", name, len);
                if (len > 0) {
                    this.out.write(buf, 0, len);
                    this.out.flush();
                } else if (len < 0)
                    break;
            }
            catch (IOException e) {
                break;
            }
        }
        return true;
    }

}
