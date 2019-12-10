package org.nutz.plugin.spring.boot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nutz.dao.sqltpl")
public class SqlTemplateProperties {

    public enum Type {
        BEETL, FREEMARKER, JETBRICK, VELOCITY
    }

    Type type = Type.BEETL;

    boolean enable = false;

    /**
     * @return the enable
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * @param enable
     *            the enable to set
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }

}
