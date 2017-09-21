package org.nutz.integration.json4excel.bean;

import org.nutz.integration.json4excel.annotation.J4EName;

@J4EName("Sheet1")
public class I68Bean {

    @J4EName("关键词")
    private String keyWorld;

    @J4EName("总文章数")
    private int number;

    public String getKeyWorld() {
        return keyWorld;
    }

    public I68Bean setKeyWorld(String keyWorld) {
        this.keyWorld = keyWorld;
        return this;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

}
