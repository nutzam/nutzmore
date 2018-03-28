package org.nutz.integration.json4excel.bean;

import org.nutz.integration.json4excel.J4EColumnType;
import org.nutz.integration.json4excel.annotation.J4ECell;
import org.nutz.integration.json4excel.annotation.J4EDefine;
import org.nutz.integration.json4excel.annotation.J4EName;

@J4EName("大鲨鱼测试数据")
public class DashayuChild extends Dashayu {

    @J4EName("创建时间")
    @J4EDefine(type = J4EColumnType.STRING)
    @J4ECell(toExcel = CellFunLong2Str.class)
    public long createAt;

    @J4EName("状态")
    @J4EDefine(type = J4EColumnType.STRING)
    @J4ECell(toExcel = CellFunStatus.class)
    public int status;
}
