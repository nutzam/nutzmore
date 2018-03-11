package org.nutz.integration.json4excel.bean;

import java.io.InputStream;

import org.nutz.integration.json4excel.J4EColumnType;
import org.nutz.integration.json4excel.annotation.J4EDefine;
import org.nutz.integration.json4excel.annotation.J4EName;

@J4EName("超级英雄")
public class Hero {
    @J4EName("姓名")
    public String name;

    @J4EName("头像")
    @J4EDefine(type = J4EColumnType.IMAGE, imgHeight = 150, imgWidth = 150)
    public InputStream avatar1;

}
