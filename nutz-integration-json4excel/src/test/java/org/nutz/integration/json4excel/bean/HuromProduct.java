package org.nutz.integration.json4excel.bean;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.nutz.integration.json4excel.J4E;
import org.nutz.integration.json4excel.J4EColumnType;
import org.nutz.integration.json4excel.annotation.J4ECell;
import org.nutz.integration.json4excel.annotation.J4EDefine;
import org.nutz.integration.json4excel.annotation.J4EExt;
import org.nutz.integration.json4excel.annotation.J4EName;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

@J4EName("Homepeage_Specification")
@J4EExt(passEmptyRow = HuromEmptyRow.class)
public class HuromProduct {
    // 型号 Key 前体区分 产品种类 额定电压 额定频率 额定功率 转数 电线长度 电机 保险丝 产品重量 外形尺寸 前体容量 Color
    // Color(Detail) 颜色 具体颜色 生产地 渠道 제품분류 产品分类

    @J4EName("id")
    private String id;

    @J4EName("型号")
    private String name;

    @J4EName("Key")
    private String key;

    @J4EName("前体区分")
    private String forebody_type;

    @J4EName("产品种类")
    private String product_type;

    @J4EName("转数")
    private String speed;

    @J4EName("电线长度")
    private String elen;

    @J4EName("产品重量")
    @J4EDefine(precision = 1, type = J4EColumnType.NUMERIC)
    @J4ECell(fromExcel = HuromWeight.class)
    private String weight;

    @J4EName("外形尺寸")
    private String size;

    @J4EName("前体容量")
    private String forebody_size;

    @J4EName("产品分类")
    private String catelog;

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getForebody_type() {
        return forebody_type;
    }

    public void setForebody_type(String forebody_type) {
        this.forebody_type = forebody_type;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getElen() {
        return elen;
    }

    public void setElen(String elen) {
        this.elen = elen;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public static void main(String[] args) throws FileNotFoundException {
        // 第一步，使用j4e解析excel文件获得数据集合
        InputStream in = new FileInputStream("E:\\原汁机规格参数.xls");
        List<HuromProduct> ps = J4E.fromExcel(in, HuromProduct.class, null);
        System.out.println(Json.toJson(ps, JsonFormat.full()));
    }
}
