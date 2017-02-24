package org.nutz.integration.json4excel.bean;

import java.util.Date;

import org.nutz.integration.json4excel.annotation.J4EName;

@J4EName("人员")
public class Person {

    @J4EName("姓名")
    private String name;

    @J4EName("年龄")
    private int age;

    private Date birthday;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

}
