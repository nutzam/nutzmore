package org.nutz.plugin.sigar;

/**
 * 占位, 用于兼容老版本的nutz加载本插件
 * @author wendal
 *
 */
@Deprecated
public class SigarIocLoader extends org.nutz.plugins.sigar.SigarIocLoader {

    public SigarIocLoader() {
        super();
    }

    public SigarIocLoader(String... args) {
        super(args);
    }
	
}
