package org.nutz.integration.json4excel.bean;

import org.nutz.integration.json4excel.J4EColumnType;
import org.nutz.integration.json4excel.annotation.J4EDefine;
import org.nutz.integration.json4excel.annotation.J4EExt;
import org.nutz.integration.json4excel.annotation.J4EName;

@J4EName("test")
@J4EExt(passHead = true)
public class I91Bean {

    @J4EName("项目")
    @J4EDefine(columnIndex = 0)
    public String name;

    @J4EName("REF")
    @J4EDefine(precision = 2, type = J4EColumnType.NUMERIC, columnIndex = 1)
    public double ref;

    @J4EName("累计")
    @J4EDefine(precision = 2, type = J4EColumnType.NUMERIC, columnIndex = 2)
    public double d1;

    @J4EName("计划值")
    @J4EDefine(precision = 2, type = J4EColumnType.NUMERIC, columnIndex = 3)
    public double d2;

    @J4EName("与计划差值")
    @J4EDefine(precision = 2, type = J4EColumnType.NUMERIC, columnIndex = 4)
    public double d3;

}
