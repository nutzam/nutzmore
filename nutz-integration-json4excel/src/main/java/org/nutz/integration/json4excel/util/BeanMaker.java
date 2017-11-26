package org.nutz.integration.json4excel.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.nutz.integration.json4excel.J4E;
import org.nutz.lang.Strings;

public class BeanMaker {

    public static String fromHeader(String hstr, String split) {
        String[] hlist = Strings.splitIgnoreBlank(hstr, split);
        StringBuilder sb = new StringBuilder();
        for (String h : hlist) {
            sb.append("@J4EName(\"").append(h).append("\")").append("\n");
            sb.append("public String ").append(h).append(";").append("\n\n");
        }
        return sb.toString();
    }

    public static String fromExcel(InputStream in, String sheetName, boolean withTable) {
        return fromExcel(in, sheetName, 0, 0, withTable);
    }

    public static String fromExcel(InputStream in,
                                   String sheetName,
                                   int passRow,
                                   int passColumn,
                                   boolean withTable) {
        Workbook wb = J4E.loadExcel(in);
        Sheet sheet = null;
        if (Strings.isBlank(sheetName)) {
            sheet = wb.getSheetAt(0);
        } else {
            sheet = wb.getSheet(sheetName);
        }
        StringBuilder sb = new StringBuilder();
        Iterator<Row> rlist = sheet.rowIterator();
        int currRow = 0;
        while (rlist.hasNext()) {
            Row row = rlist.next();
            if (row == null)
            	continue;
            if (currRow >= passRow) {
                Iterator<Cell> clist = row.cellIterator();
                while (clist.hasNext()) {
                    Cell chead = clist.next();
                    String h = J4E.cellValue(chead, null);
                    sb.append("@J4EName(\"").append(h).append("\")").append("\n");
                    if (withTable) {
                        sb.append("@ColDefine()").append("\n");
                    }
                    sb.append("public String ")
                      .append(h.replace(" ", "").replace("/", "").replace("(", "").replace(")", ""))
                      .append(";")
                      .append("\n\n");
                }
                break;
            }
            currRow++;
        }
        try {
            wb.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
