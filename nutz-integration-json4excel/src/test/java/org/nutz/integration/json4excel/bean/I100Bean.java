package org.nutz.integration.json4excel.bean;

import org.nutz.integration.json4excel.annotation.J4EDateFormat;
import org.nutz.integration.json4excel.annotation.J4EDefine;
import org.nutz.integration.json4excel.annotation.J4EName;

@J4EName("flash_sale")
public class I100Bean {

    @J4EName("开始时间")
    @J4EDateFormat(from = "", to = "yyyy-MM-dd hh:mm")
    private String from;

    @J4EName("结束时间")
    @J4EDateFormat(from = "", to = "yyyy-MM-dd hh:mm")
    private String to;

    @J4EName("商品ID")
    private long itemId;

    @J4EName("skuID")
    private long skuId;

    @J4EName("闪购价")
    @J4EDefine(precision = 2)
    private float price;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

}
