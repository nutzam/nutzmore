package org.nutz.plugins.apidoc.annotation;

/**
 * @author wendal
 *
 */
public enum ApiMatchMode {
    /**
     * 全部忽略
     */
    NONE,
    /**
     * 仅添加带@Api的类或方法
     */
    ONLY,
    /**
     * 全部入口方法
     */
    ALL
}
