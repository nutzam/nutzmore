package org.nutz.integration.json4excel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.nutz.castor.Castors;
import org.nutz.integration.json4excel.annotation.J4EDateFormat;
import org.nutz.integration.json4excel.annotation.J4EFormat;
import org.nutz.integration.json4excel.annotation.J4EName;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 根据json配置文件, 读取或导出excel文件
 * 
 * @author pw
 * 
 */
public class J4E {

    private J4E() {}

    private final static Log log = Logs.get();

    /**
     * 将给定的数据列表datalist, 按照j4eConf中的配置, 输出到out
     * 
     * @param out
     *            输出流
     * @param objClz
     *            转换后的对象Class, 对应一行数据
     * @param j4eConf
     *            转换配置(非必须, 可自动生成)
     * 
     * @return 是否转换并写入成功
     */

    public static <T> boolean toExcel(OutputStream out,
                                      List<T> dataList,
                                      J4EConf j4eConf) {
        Workbook wb = (j4eConf != null
                       && j4eConf.isUse2007()) ? new XSSFWorkbook()
                                               : new HSSFWorkbook();
        return toExcel(wb, out, dataList, j4eConf);
    }

    public static <T> boolean toExcel(File excel,
                                      List<T> dataList,
                                      J4EConf j4eConf) {
        Workbook wb = (j4eConf != null
                       && j4eConf.isUse2007()) ? new XSSFWorkbook()
                                               : new HSSFWorkbook();
        OutputStream out = null;
        try {
            out = new FileOutputStream(excel);
            return toExcel(wb, out, dataList, j4eConf);
        }
        catch (FileNotFoundException e) {
            log.error(e);
            return false;
        }
        finally {
            Streams.safeClose(wb);
            Streams.safeClose(out);
        }
    }

    public static <T> boolean appendExcel(File excel,
                                          List<T> dataList,
                                          J4EConf j4eConf) {
        try {
            Workbook wb = loadExcel(new FileInputStream(excel));
            if (wb == null) {
                wb = (j4eConf != null
                      && j4eConf.isUse2007()) ? new XSSFWorkbook()
                                              : new HSSFWorkbook();
            }
            return toExcel(wb, new FileOutputStream(excel), dataList, j4eConf);
        }
        catch (Exception e) {
            log.error(e);
            return false;
        }
    }

    @SuppressWarnings({"unchecked"})
    public static <T> boolean toExcel(Workbook wb,
                                      OutputStream out,
                                      List<T> dataList,
                                      J4EConf j4eConf) {
        if (dataList == null || dataList.size() == 0) {
            log.warn("datalist is empty! can't convert to excel");
            return false;
        }
        Class<T> objClz = (Class<T>) dataList.get(0).getClass();
        Mirror<T> mc = Mirror.me(objClz);
        j4eConf = checkJ4EConf(j4eConf, objClz);
        // FIXME 暂时是生成一个新的excel, 以后可以向现有的excel文件中写入
        Sheet sheet = wb.getSheet(j4eConf.getSheetName());
        if (sheet == null) {
            sheet = wb.createSheet(j4eConf.getSheetName());
        }
        // 判断column的field是否都在T中
        for (J4EColumn jcol : j4eConf.getColumns()) {
            if (!Strings.isBlank(jcol.getFieldName())) {
                try {
                    Field cfield = mc.getField(jcol.getFieldName());
                    jcol.setField(cfield);
                }
                catch (NoSuchFieldException e) {
                    log.warnf("can't find Field[%s] in Class[%s]",
                              jcol.getFieldName(),
                              objClz.getName());
                }
            }
        }
        int passRow = j4eConf.getPassRow();
        int passColumn = j4eConf.getPassColumn();
        int cindex = 0;
        if (j4eConf.isPassHead()) {
            passRow++;
        } else {
            // 写入head
            Row rhead = sheet.createRow(passRow++);
            for (J4EColumn jcol : j4eConf.getColumns()) {
                if (jcol.isIgnore()) {
                    continue;
                }
                Field jfield = jcol.getField();
                if (null != jfield) {
                    Cell c = rhead.createCell(cindex++);
                    c.setCellType(CellType.STRING);
                    c.setCellValue(Strings.isBlank(jcol.getColumnName()) ? jcol.getFieldName()
                                                                         : jcol.getColumnName());
                }
            }
        }
        // 写入row
        for (T dval : dataList) {
            if (log.isDebugEnabled()) {
                log.debugf("add Row : %s",
                           Json.toJson(dval, JsonFormat.compact()));
            }
            int crow = passRow++;
            Row row = sheet.getRow(crow);
            if (row == null) {
                row = sheet.createRow(crow);
            }
            cindex = passColumn;
            for (J4EColumn jcol : j4eConf.getColumns()) {
                if (jcol.isIgnore()) {
                    continue;
                }
                Field jfield = jcol.getField();
                if (null != jfield) {
                    int ccin = cindex++;
                    J4EColumnType columnType = jcol.getColumnType();
                    Object dfv = mc.getValue(dval, jfield);
                    Cell c = row.getCell(ccin);
                    if (c == null) {
                        c = row.createCell(ccin);
                    }
                    // 图片
                    if (columnType == J4EColumnType.IMAGE) {
                        try {
                            sheet.setColumnWidth(ccin, jcol.getImgWidth() * 43);
                            row.setHeight((short) (jcol.getImgHeight() * 20));
                            InputStream imgIn = (InputStream) dfv;
                            // BufferedImage bufImgIn =
                            // Images.scale(Images.read(imgIn),
                            // jcol.getImgWidth(),
                            // jcol.getImgHeight());
                            ByteArrayOutputStream outImg = new ByteArrayOutputStream();
                            Streams.writeAndClose(outImg, imgIn);
                            int pictureIdx = wb.addPicture(outImg.toByteArray(),
                                                           Workbook.PICTURE_TYPE_PNG);
                            CreationHelper helper = wb.getCreationHelper();
                            Drawing drawing = sheet.createDrawingPatriarch();
                            ClientAnchor anchor = helper.createClientAnchor();
                            anchor.setRow1(row.getRowNum());
                            anchor.setCol1(ccin);
                            anchor.setDx1(0);
                            anchor.setDy1(0);
                            anchor.setRow2(row.getRowNum() + 1);
                            anchor.setCol2(ccin + 1);
                            anchor.setDx1(5);
                            anchor.setDy1(5);
                            anchor.setDx2(-5);
                            anchor.setDy2(-5);
                            drawing.createPicture(anchor,
                                                                 pictureIdx);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        // 数字
                        if (columnType == J4EColumnType.NUMERIC) {
                            c.setCellType(CellType.NUMERIC);
                            int precision = jcol.getPrecision();
                            if (precision == 0) {
                                Integer intRe = Castors.me()
                                                       .castTo(dfv,
                                                               Integer.class);
                                if (intRe != null) {
                                    c.setCellValue(intRe);
                                }
                            } else {
                                Double dbRe = Castors.me().castTo(dfv,
                                                                  Double.class);
                                if (dbRe != null) {
                                    c.setCellValue(dbRe);
                                }
                            }
                        }
                        // 字符串
                        else {
                            c.setCellType(CellType.STRING);
                            if (jcol.getToExcelFun() != null) {
                                J4ECellToExcel cellFun = jcol.getToExcelFun();
                                Object setVal = cellFun.toExecl(dfv);
                                c.setCellValue(Castors.me()
                                                      .castTo(setVal,
                                                              String.class));
                            } else {
                                c.setCellValue(dfv != null ? Castors.me()
                                                                    .castTo(dfv,
                                                                            String.class)
                                                           : "");
                            }
                        }
                    }
                    J4ECellSetStyle cellSetStyle = jcol.getCellStyle();
                    if (cellSetStyle != null) {
                        cellSetStyle.setStyle(c);
                    }
                }
            }
        }

        if (out == null) {
            return true;
        }
        return saveExcel(out, wb);
    }

    

    private static <T> J4EConf checkJ4EConf(J4EConf j4eConf, Class<T> objClz) {
        if (null == j4eConf) {
            j4eConf = J4EConf.from(objClz);
        }
        if (Strings.isBlank(j4eConf.getSheetName())) {
            String sheetName = objClz.getSimpleName();
            J4EName cName = objClz.getAnnotation(J4EName.class);
            if (cName != null && !Strings.isBlank(cName.value())) {
                sheetName = cName.value();
            }
            j4eConf.setSheetName(sheetName);
        }
        return j4eConf;
    }

    /**
     * 解析输入流, 按照j4eConf中的配置, 读取后返回objClz类型的数量列表
     * 
     * @param in
     *            输入流
     * @param objClz
     *            转换后的对象Class, 对应一行数据
     * @param j4eConf
     *            转换配置(非必须, 可自动生成)
     * @return 数据列表
     */
    public static <T> List<T> fromExcel(InputStream in,
                                        Class<T> objClz,
                                        J4EConf j4eConf) {
        if (null == j4eConf) {
            j4eConf = J4EConf.from(objClz);
        }
        Workbook wb = loadExcel(in);
        Sheet sheet = getSheet(wb, objClz, j4eConf);
        if (sheet == null) {
            log.error("Not Find Sheet");
            return null;
        }
        return fromSheet(sheet, objClz, j4eConf, false);
    }

    @SuppressWarnings("unused")
    public static <T> List<T> fromSheet(Sheet sheet,
                                        Class<T> objClz,
                                        J4EConf j4eConf,
                                        boolean onlyHeader) {
        Mirror<T> mc = Mirror.me(objClz);
        List<T> dataList = j4eConf.isNoReturn() ? null : new ArrayList<T>();
        if (!Lang.equals(sheet.getSheetName(), j4eConf.getSheetName())) {
            return dataList;
        }
        Iterator<Row> rlist = sheet.rowIterator();
        int passRow = j4eConf.getPassRow();
        int passContentRow = j4eConf.getPassContentRow();
        boolean passHead = j4eConf.isPassHead();
        long maxRead = j4eConf.getMaxRead();
        J4EEmptyRow<T> emptyRowChecker = (J4EEmptyRow<T>) j4eConf.getPassEmptyRow();
        boolean needCheckEmptyRow = emptyRowChecker != null;
        int currRow = 0;
        boolean firstRow = true;
        long readNum = 0;
        while (rlist.hasNext()) {
            // 最大值
            if (maxRead > 0) {
                if (readNum >= maxRead) {
                    break;
                }
            }
            Row row = rlist.next();
            if (currRow >= passRow) {
                if (firstRow) {
                    if (!passHead) {
                        // 确定column的index
                        Iterator<Cell> clist = row.cellIterator();
                        int cindex = 0;
                        Map<String, Integer> headIndexMap = new HashMap<String, Integer>();
                        while (clist.hasNext()) {
                            Cell chead = clist.next();
                            headIndexMap.put(cellValue(chead, null), cindex++);
                        }
                        for (J4EColumn jcol : j4eConf.getColumns()) {
                            if (null != headIndexMap.get(jcol.getColumnName())) {
                                // by columnName
                                jcol.setColumnIndex(headIndexMap.get(jcol.getColumnName()));
                            } else if (null != headIndexMap.get(jcol.getFieldName())) {
                                // by field
                                jcol.setColumnIndex(headIndexMap.get(jcol.getFieldName()));
                            } else if (null != jcol.getColumnIndex()
                                       && jcol.getColumnIndex() >= 0) {
                                // 已经设置过的index ??? 这个提醒一下
                                log.warnf("J4EColumn has already set index[%d], but not sure It is right",
                                          jcol.getColumnIndex());
                            } else {
                                jcol.setColumnIndex(null);
                            }
                        }
                    }
                    for (J4EColumn jcol : j4eConf.getColumns()) {
                        // 查找field
                        if (jcol.getColumnIndex() != null
                            && jcol.getColumnIndex() >= 0) {
                            try {
                                Field cfield = mc.getField(jcol.getFieldName());
                                jcol.setField(cfield);
                                // 查看是否有特殊的格式需求
                                J4EDateFormat dFormat = cfield.getAnnotation(J4EDateFormat.class);
                                if (dFormat != null) {
                                    String f = dFormat.from();
                                    String t = dFormat.to();
                                    if (!Strings.isBlank(f)
                                        || !Strings.isBlank(t)) {
                                        jcol.setDtFormat(new String[]{f, t});
                                    }
                                }
                            }
                            catch (NoSuchFieldException e) {
                                log.warnf("can't find Field[%s] in Class[%s]",
                                          jcol.getFieldName(),
                                          objClz.getName());
                            }
                        }
                    }
                    log.debugf("J4EConf-Columns : \n%s",
                               Json.toJson(j4eConf.getColumns()));
                    firstRow = false;
                    if (onlyHeader) {
                        break;
                    }
                    continue;
                }

                // 跳过内容行数
                if (passContentRow > 0) {
                    passContentRow--;
                    continue;
                }

                readNum++;
                // 开始读数据
                T rVal = rowValue(row, j4eConf, mc);
                if (null != j4eConf.getEachPrepare()) {
                    j4eConf.getEachPrepare().doEach(rVal);
                } else if (null != j4eConf.getEachModify()) {
                    j4eConf.getEachModify()
                           .doEach(rVal, row, j4eConf.getColumns());
                }
                if (!j4eConf.isNoReturn()) {
                    if (needCheckEmptyRow) {
                        if (!emptyRowChecker.isEmpty(rVal)) {
                            dataList.add(rVal);
                        }
                    } else {
                        dataList.add(rVal);
                    }
                }
            }
            currRow++;
        }
        // 如果是modify的话
        if (null != j4eConf.getModifyOutput()) {
            saveExcel(j4eConf.getModifyOutput(), sheet.getWorkbook());
        }
        return dataList;
    }

    public static <T> boolean matchExcel(InputStream in, Class<T> objClz) {
        J4EConf j4eConf = J4EConf.from(objClz);
        Workbook wb = loadExcel(in);
        Sheet sheet = getSheet(wb, objClz, j4eConf);
        // 解析header
        fromSheet(sheet, objClz, j4eConf, true);
        // 判断header是否都找到了
        int jcnum = 0;
        int jcfind = 0;
        for (J4EColumn jcol : j4eConf.getColumns()) {
            if (jcol.getColumnIndex() != null) {
                jcfind++;
            }
            jcnum++;
        }
        return jcfind == jcnum;
    }

    public static <T> T rowValue(Row row, J4EConf j4eConf, Mirror<T> mc) {
        // FIXME 必须有标准构造函数
        T rVal = mc.born();
        for (J4EColumn jcol : j4eConf.getColumns()) {
            Field jfield = jcol.getField();
            if (null != jfield) {
                Cell cell = row.getCell(jcol.getColumnIndex()
                                        + j4eConf.getPassColumn());
                if (null == cell) {
                    log.warn(String.format("cell [%d, %d] has null, so value is ''",
                                           row.getRowNum(),
                                           jcol.getColumnIndex()));
                }
                String cVal = (null == cell ? "" : cellValue(cell, jcol));
                if (jcol.getFromExcelFun() != null) {
                    J4ECellFromExcel cellFun = jcol.getFromExcelFun();
                    Object setVal = cellFun.fromExcel(cVal);
                    mc.setValue(rVal, jfield, setVal);
                } else {
                    mc.setValue(rVal, jfield, cVal);
                }
            }
        }
        return rVal;
    }

    public static String cellValue(Cell c, J4EColumn jcol) {
        J4EColumnType colType = null;
        if (jcol != null) {
            colType = jcol.getColumnType();
        }
        if (null == colType) {
            colType = J4EColumnType.STRING;
        }
        try {
            // 3.1.5之后将可直接调用c.getCellType返回枚举
            CellType cType = c.getCellTypeEnum();
            switch (cType) {
            case NUMERIC: // 数字
                if (DateUtil.isCellDateFormatted(c)) {
                    Date dval = c.getDateCellValue();
                    if (jcol.getDtFormat() != null) {
                        try {
                            return Times.format(jcol.getDtFormat()[1], dval);
                        }
                        catch (Exception e) {
                            log.error(String.format("cell [%d, %d] datetime formate err, value %s [%s-%s]",
                                                    c.getRowIndex(),
                                                    c.getColumnIndex(),
                                                    dval.toString(),
                                                    jcol.getDtFormat()[0],
                                                    jcol.getDtFormat()[1],
                                                    e));
                        }
                    }
                    return Times.sDT(dval);
                }
                if (J4EColumnType.STRING == colType) {
                    // 按照整形来拿, 防止2B的科学计数法
                    DecimalFormat df = new DecimalFormat("0");
                    return df.format(c.getNumericCellValue());
                } else if (J4EColumnType.NUMERIC == colType) {
                    if (jcol.getPrecision() == 0) {
                        // 整数
                        return "" + (int) c.getNumericCellValue();
                    } else {
                        // 按照double数字拿
                        String fString = "0."
                                         + Strings.alignLeft("",
                                                             jcol.getPrecision(),
                                                             '0');
                        DecimalFormat df = new DecimalFormat(fString);
                        return df.format(c.getNumericCellValue());
                    }
                } else {
                    throw new RuntimeException("WTF, CELL_TYPE_NUMERIC is what!");
                }
                // 按照字符拿
            case STRING: // 字符串
                String strResult = Strings.trim(c.getStringCellValue());
                if (!Strings.isBlank(strResult) && jcol != null) {
                    // 日期转换
                    if (jcol.getDtFormat() != null) {
                        try {
                            strResult = Times.format(jcol.getDtFormat()[1],
                                                     Times.parse(jcol.getDtFormat()[0],
                                                                 strResult));
                        }
                        catch (Exception e) {
                            log.error(String.format("cell [%d, %d] datetime formate err, value %s [%s-%s]",
                                                    c.getRowIndex(),
                                                    c.getColumnIndex(),
                                                    strResult,
                                                    jcol.getDtFormat()[0],
                                                    jcol.getDtFormat()[1],
                                                    e));
                        }
                    }
                    // 文字转换
                    J4EFormat strFormat = jcol.getField()
                                              .getAnnotation(J4EFormat.class);
                    if (strFormat != null) {
                        if (strFormat.LowerCase()) {
                            strResult = strResult.toLowerCase();
                        }
                        if (strFormat.UpperCase()) {
                            strResult = strResult.toUpperCase();
                        }
                    }
                }
                return strResult;
            case BOOLEAN: // boolean
                return String.valueOf(c.getBooleanCellValue());
            case FORMULA:
                return Strings.trim(String.valueOf(c.getStringCellValue()));
            default:
                return Strings.trim(c.getStringCellValue());
            }
        }
        catch (Exception e) {
            log.error(String.format("cell [%d, %d] has error, value can't convert to string",
                                    c.getRowIndex(),
                                    c.getColumnIndex()),
                      e);
            return "";
        }
    }

    public static Sheet getSheet(Workbook wb,
                                 Class<?> objClz,
                                 J4EConf j4eConf) {
        // 读取sheet
        Sheet sheet = null;
        if (!Strings.isBlank(j4eConf.getSheetName())) {
            // sheetName 可以是多个
            String[] snArray = j4eConf.getSheetName().split("\\|");
            for (String sn : snArray) {
                sheet = wb.getSheet(sn);
                if (sheet != null) {
                    log.infof("find sheet by name [%s]", sn);
                    break;
                }
            }
        }
        if (null == sheet) {
            sheet = wb.getSheetAt(j4eConf.getSheetIndex());
        }
        if (null == sheet) {
            log.errorf("excel not has sheet at [%d] or sheetName is [%s]",
                       j4eConf.getSheetIndex(),
                       j4eConf.getSheetName());
        }
        return sheet;
    }

    /**
     * 读取excel文件, 返回wb对象, 如果读取发生错误, 返回null
     * 
     * @param excel
     * @return
     */
    public static Workbook loadExcel(InputStream in) {
        Workbook wb = null;
        try {
            try {
                wb = WorkbookFactory.create(in);
            }
            catch (Exception e1) {
                // 因为HSSF与XSSF的不同, 导致返回的sheet对象能有不同, 暂时先使用HSSF
                // FIXME 稍后实现两种, XSSF使用更少的内存, 但仅仅能访问xlsx
                try {
                    wb = new HSSFWorkbook(in);
                }
                catch (Exception e2) {
                    wb = new XSSFWorkbook(in);
                }
            }
        }
        catch (Exception e) {
            log.error("can't load inputstream for a workbook", e);
        }
        return wb;
    }

    /**
     * 保存excel文件, 返回保存是否成功
     * 
     * @param out
     * @param wb
     * @return
     */
    public static boolean saveExcel(OutputStream out, Workbook wb) {
        try {
            wb.write(out);
            return true;
        }
        catch (Exception e) {
            log.error("can't write wookbook to outputstream", e);
        }
        return false;
    }

}
