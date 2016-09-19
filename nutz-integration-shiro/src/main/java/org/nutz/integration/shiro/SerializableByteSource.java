package org.nutz.integration.shiro;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;

/**
 * 可序列号的ByteSource
 * @author wendal
 *
 */
public class SerializableByteSource extends SimpleByteSource implements Serializable {

    private static final long serialVersionUID = 1L;

    public SerializableByteSource(byte[] bytes) {
        super(bytes);
    }

    public SerializableByteSource(char[] chars) {
        super(chars);
    }

    public SerializableByteSource(String string) {
        super(string);
    }

    public SerializableByteSource(ByteSource source) {
        super(source);
    }

    public SerializableByteSource(File file) {
        super(file);
    }

    public SerializableByteSource(InputStream stream) {
        super(stream);
    }

}
