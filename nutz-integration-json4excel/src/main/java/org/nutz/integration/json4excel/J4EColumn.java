package org.nutz.integration.json4excel;

import java.lang.reflect.Field;

import org.nutz.json.JsonField;
import org.nutz.lang.Mirror;

public class J4EColumn {

    // 在对应obj中的属性名称
    private String fieldName;

    // 在excel中的列, 从1开始计算
    private Integer columnIndex;

    // 在excel中的标题名字
    private String columnName;

    // 列按照什么类型读取
    private J4EColumnType columnType;

    private int imgWidth;

    private int imgHeight;

    // 自定义处理方法
    private J4ECellToExcel toExcelFun;
    private J4ECellFromExcel fromExcelFun;

    // 设置表格样式
    private J4ECellSetStyle cellStyle;

    private int precision;

    private boolean isIgnore;

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

    public boolean isIgnore() {
        return isIgnore;
    }

    public void setIgnore(boolean isIgnore) {
        this.isIgnore = isIgnore;
    }

    public J4ECellToExcel getToExcelFun() {
        return toExcelFun;
    }

    public J4ECellFromExcel getFromExcelFun() {
        return fromExcelFun;
    }

    public void setToExcelFun(Class<? extends J4ECellToExcel> cellFun) {
        Mirror<? extends J4ECellToExcel> mc = Mirror.me(cellFun);
        toExcelFun = mc.born();
    }

    public void setFromExcelFun(Class<? extends J4ECellFromExcel> cellFun) {
        Mirror<? extends J4ECellFromExcel> mc = Mirror.me(cellFun);
        fromExcelFun = mc.born();
    }

    public J4ECellSetStyle getCellStyle() {
        return cellStyle;
    }

    public void setCellStyle(Class<? extends J4ECellSetStyle> cellStyle) {
        Mirror<? extends J4ECellSetStyle> mc = Mirror.me(cellStyle);
        this.cellStyle = mc.born();
    }

    public int getImgWidth() {
        return imgWidth;
    }

    public void setImgWidth(int imgWidth) {
        this.imgWidth = imgWidth;
    }

    public int getImgHeight() {
        return imgHeight;
    }

    public void setImgHeight(int imgHeight) {
        this.imgHeight = imgHeight;
    }

}
