package org.nutz.integration.json4excel.bean;

import org.nutz.integration.json4excel.annotation.J4EIgnore;
import org.nutz.integration.json4excel.annotation.J4EName;

@J4EName("用户")
public class TUser {

    @J4EName("姓名")
    @J4EIgnore
    public String name;

    @J4EName("昵称")
    @J4EIgnore
    public String alias;

    @J4EName("年龄")
    @J4EIgnore
    public int age;

    @J4EName("备注")
    @J4EIgnore
    public String remark;

}
