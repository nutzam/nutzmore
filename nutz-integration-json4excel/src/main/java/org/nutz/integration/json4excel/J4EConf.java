package org.nutz.integration.json4excel;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.nutz.integration.json4excel.annotation.J4ECell;
import org.nutz.integration.json4excel.annotation.J4ECellStyle;
import org.nutz.integration.json4excel.annotation.J4EDefine;
import org.nutz.integration.json4excel.annotation.J4EExt;
import org.nutz.integration.json4excel.annotation.J4EIgnore;
import org.nutz.integration.json4excel.annotation.J4EName;
import org.nutz.json.Json;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Disks;

@SuppressWarnings("rawtypes")
public class J4EConf {

    // excel中的sheet的index, 从1开始
    private int sheetIndex;

    // excel中sheet的名字
    private String sheetName;

    // 跳过的列
    private int passColumn;

    // 跳过的行
    private int passRow;

    // 跳过内容行数
    private int passContentRow;

    // 跳过标头
    private boolean passHead;

    // 最大写入行数
    private long maxWrite;

    // 最大读取行数
    private long maxRead;

    // 跳过空行检查
    private J4EEmptyRow passEmptyRow;

    // sheet中对应的列
    private List<J4EColumn> columns;

    // 每一行生成之后, 可以做一定的处理
    private J4EEachRow eachPrepare;

    // 访问每一行，可以修改当前行内容
    private J4EEachRowModify eachModify;

    private OutputStream modifyOut;

    // 不返回结果, 一般跟eachPrepare配合使用
    private boolean noReturn;

    // 声明使用2007格式, 读入一般会做兼容, 写入查看当前配置
    private boolean use2007;

    public boolean isUse2007() {
        return use2007;
    }

    public J4EConf setUse2007(boolean use2007) {
        this.use2007 = use2007;
        return this;
    }

    public boolean isNoReturn() {
        return noReturn;
    }

    public J4EConf setNoReturn(boolean noReturn) {
        this.noReturn = noReturn;
        return this;
    }

    public J4EEachRow getEachPrepare() {
        return eachPrepare;
    }

    public J4EConf setEachPrepare(J4EEachRow eachPrepare) {
        this.eachPrepare = eachPrepare;
        this.noReturn = true;
        return this;
    }

    public J4EEachRowModify getEachModify() {
        return eachModify;
    }

    public OutputStream getModifyOutput() {
        return modifyOut;
    }

    public J4EConf setEachModify(J4EEachRowModify eachModify,
                                 OutputStream out) {
        this.eachModify = eachModify;
        this.modifyOut = out;
        this.noReturn = true;
        return this;
    }

    public int getSheetIndex() {
        return sheetIndex;
    }

    public J4EConf setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
        return this;
    }

    public String getSheetName() {
        return sheetName;
    }

    public J4EConf setSheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    public List<J4EColumn> getColumns() {
        return columns;
    }

    public J4EConf setColumns(List<J4EColumn> columns) {
        this.columns = columns;
        return this;
    }

    public int getPassColumn() {
        return passColumn;
    }

    public J4EConf setPassColumn(int passColumn) {
        this.passColumn = passColumn;
        return this;
    }

    public int getPassRow() {
        return passRow;
    }

    public J4EConf setPassRow(int passRow) {
        this.passRow = passRow;
        return this;
    }

    // ===================== 生成J4EConf的快捷方法

    public boolean isPassHead() {
        return passHead;
    }

    public void setPassHead(boolean passHead) {
        this.passHead = passHead;
    }

    public int getPassContentRow() {
        return passContentRow;
    }

    public void setPassContentRow(int passContentRow) {
        this.passContentRow = passContentRow;
    }

    public OutputStream getModifyOut() {
        return modifyOut;
    }

    public void setModifyOut(OutputStream modifyOut) {
        this.modifyOut = modifyOut;
    }

    public void setEachModify(J4EEachRowModify eachModify) {
        this.eachModify = eachModify;
    }

    public J4EEmptyRow<?> getPassEmptyRow() {
        return passEmptyRow;
    }

    public void setPassEmptyRow(Class<? extends J4EEmptyRow> cellFun) {
        Mirror<? extends J4EEmptyRow> mc = Mirror.me(cellFun);
        passEmptyRow = mc.born();
    }

    public long getMaxWrite() {
        return maxWrite;
    }

    public void setMaxWrite(long maxWrite) {
        this.maxWrite = maxWrite;
    }

    public long getMaxRead() {
        return maxRead;
    }

    public void setMaxRead(long maxRead) {
        this.maxRead = maxRead;
    }

    public static J4EConf from(Class<?> clz) {
        J4EConf jc = new J4EConf();
        // Ext
        J4EExt ecnf = clz.getAnnotation(J4EExt.class);
        if (ecnf != null) {
            jc.passRow = ecnf.passRow();
            jc.passColumn = ecnf.passColumn();
            jc.passHead = ecnf.passHead();
            jc.passContentRow = ecnf.passContentRow();
            jc.maxRead = ecnf.maxRead();
            jc.maxWrite = ecnf.maxWrite();
            Class<? extends J4EEmptyRow<?>> perow = ecnf.passEmptyRow();
            if (!perow.isAssignableFrom(J4EEmptyRowImpl.class)) {
                jc.setPassEmptyRow(perow);
            }
        }
        // sheet
        String sheetName = null;
        J4EName cName = clz.getAnnotation(J4EName.class);
        if (cName != null && !Strings.isBlank(cName.value())) {
            sheetName = cName.value();
        }
        jc.setSheetName(sheetName);
        // columns
        List<J4EColumn> jclist = new ArrayList<J4EColumn>();
        Mirror<?> mc = Mirror.me(clz);
        for (Field cf : mc.getFields()) {
            J4EColumn jcol = new J4EColumn();
            jcol.setFieldName(cf.getName());
            jcol.setColumnName(cf.getName());
            J4EName fName = cf.getAnnotation(J4EName.class);
            if (fName != null && !Strings.isBlank(fName.value())) {
                jcol.setColumnName(fName.value());
            }
            J4EDefine define = cf.getAnnotation(J4EDefine.class);
            if (define != null) {
                jcol.setColumnType(define.type());
                jcol.setPrecision(define.precision());
                jcol.setImgHeight(define.imgHeight());
                jcol.setImgWidth(define.imgWidth());
                if (define.columnIndex() > -1) {
                    jcol.setColumnIndex(define.columnIndex());
                }
            }
            J4EIgnore ignore = cf.getAnnotation(J4EIgnore.class);
            if (ignore != null) {
                jcol.setIgnore(true);
            }
            J4ECell cellset = cf.getAnnotation(J4ECell.class);
            if (cellset != null) {
                Class<? extends J4ECellToExcel> toExcelFun = cellset.toExcel();
                if (!toExcelFun.isAssignableFrom(J4ECellToExcelImpl.class)) {
                    jcol.setToExcelFun(toExcelFun);
                }
                Class<? extends J4ECellFromExcel> fromExcelFun = cellset.fromExcel();
                if (!fromExcelFun.isAssignableFrom(J4ECellFromExcelImpl.class)) {
                    jcol.setFromExcelFun(fromExcelFun);
                }
            }
            J4ECellStyle cellStyle = cf.getAnnotation(J4ECellStyle.class);
            if (cellStyle != null) {
                Class<? extends J4ECellSetStyle> toExcelFun = cellStyle.setStyle();
                if (!toExcelFun.isAssignableFrom(J4ECellSetStyleImpl.class)) {
                    jcol.setCellStyle(toExcelFun);
                }
            }
            jclist.add(jcol);
        }
        jc.setColumns(jclist);
        return jc;
    }

    public static J4EConf from(File confFile) {
        return Json.fromJsonFile(J4EConf.class, confFile);
    }

    public static J4EConf from(String confPath) {
        return Json.fromJsonFile(J4EConf.class,
                                 new File(Disks.absolute(confPath)));
    }

    public static J4EConf from(Reader confReader) {
        return Json.fromJson(J4EConf.class, confReader);
    }

    public static J4EConf from(InputStream confInputStream) {
        return Json.fromJson(J4EConf.class,
                             new InputStreamReader(confInputStream));
    }

    public static J4EConf fromConf(CharSequence confStr) {
        return Json.fromJson(J4EConf.class, confStr);
    }
}
