package org.nutz.plugins.ip2region.impl;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.nutz.plugins.ip2region.DBReader;

public class RandomAccessFileDBReader implements DBReader {

    protected RandomAccessFile raf;

    public RandomAccessFileDBReader(RandomAccessFile raf) {
        this.raf = raf;
    }
    
    public byte[] full() throws IOException {
        byte[] buf = new byte[(int)raf.length()];
        raf.readFully(buf, 0, buf.length);
        return buf;
    }

    public void readFully(long pos, byte[] buf, int offset, int length) throws IOException {
        raf.seek(pos);
        raf.readFully(buf, offset, length);
    }

    public void close() throws IOException {
        raf.close();
    }
}
