package org.nutz.plugins.wkcache.test;

import java.io.Serializable;

/**
 * Created by wizzer on 2017/6/15.
 */
public class TestBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
