package org.nutz.integration.json4excel;

import java.lang.reflect.Field;

import org.nutz.json.JsonField;

public class J4EColumn {

    // 在对应obj中的属性名称
    private String fieldName;

    // 在excel中的列, 从1开始计算
    private Integer columnIndex;

    // 在excel中的标题名字
    private String columnName;

    // 列按照什么类型读取
    private J4EColumnType columnType;

    private int precision;

    // 真实的field
    @JsonField(ignore = true)
    private Field field;

    // 时间日期转换
    private String[] dtFormat;

    void setField(Field field) {
        this.field = field;
    }

    Field getField() {
        return field;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Integer getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(Integer columnIndex) {
        this.columnIndex = columnIndex;
    }

    public J4EColumnType getColumnType() {
        return columnType;
    }

    public void setColumnType(J4EColumnType columnType) {
        this.columnType = columnType;
    }

    public String[] getDtFormat() {
        return dtFormat;
    }

    public void setDtFormat(String[] dtFormat) {
        this.dtFormat = dtFormat;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

}
