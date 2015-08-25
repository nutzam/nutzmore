package org.nutz.plugins.cache.dao.impl.convert;

/**
 * 多种序列化形式的组合实现<p>
 * <code>
TYPE      1 byte 
DATA      N byte, depens on TYPE

# 序列化类型

0x20     简化类型序列化,即后面的数据是基本类型/List/数组
0x21     Java原生序列化, +4字节长度
0x22     类自行序列化, 紧接着一个String Type的数据,然后+4字节的长度

# Type类型, 基本类型
0x31      保留
0x32      null值
0x33      true
0x34      false
0x35      byte,后面+1个字节
0x36      short,后面+2个字节
0x37      int, 后面+4个字节
0x38      long, 后面加8个字节
0x39      float, 后面加8个字节
0x40      double, 后面加8个字节
0x41      String, 后面+4个字节长度,然后是数据

# List类型, 后面+4个字节的大小,然后是每个元素的数据
0x51      ArrayList, (len)
0x52      LinkedList, (len)

# 数组类型, 后面紧接着一个String Type的数据, 然后4字节的长度
0x61      T[],String,(len)


 * </code>
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class MulitTldCacheSerializer extends AbstractCacheSerializer {

    public final static byte CUSTOM  = 0x20;
    public final static byte JAVA  = 0x21;
    public final static byte SELF = 0x22;
    
    public final static byte NULL  = 0x32;
    public final static byte TRUE  = 0x33;
    public final static byte FALSE  = 0x34;
    public final static byte BYTE  = 0x35;
    public final static byte SHORT  = 0x36;
    public final static byte INT  = 0x37;
    public final static byte LONG  = 0x38;
    public final static byte FLOAT  = 0x39;
    public final static byte DOUBLE  = 0x40;
    public final static byte STRING  = 0x41;
    

    public final static byte ARRAYLIST  = 0x51;
    public final static byte LINKEDLIST  = 0x52;
    public final static byte ARRAY  = 0x61;

    protected final static byte[] NULL_DATA = new byte[]{CUSTOM, NULL};
    protected final static byte[] FALSE_DATA = new byte[]{CUSTOM, FALSE};
    protected final static byte[] TRUE_DATA = new byte[]{CUSTOM, NULL};
    
    public Object from(Object obj) {
        return serialize(obj);
    }
    
    public byte[] serialize(Object obj) {
        if (obj == null) {
            return NULL_DATA;
        }
        if (obj instanceof Boolean) {
            if ((Boolean)obj)
                return TRUE_DATA;
            else
                return FALSE_DATA;
        }
        if (obj instanceof Long) {
            
        }
        return null;
    }

    
    public Object back(Object obj) {
        return null;
    }

}
