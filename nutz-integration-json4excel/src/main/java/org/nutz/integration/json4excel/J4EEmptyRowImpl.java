package org.nutz.integration.json4excel;

import org.apache.poi.ss.formula.functions.T;

public class J4EEmptyRowImpl implements J4EEmptyRow<T> {

    @Override
    public boolean isEmpty(T rowData) {
        // 不判断 全部返回
        return false;
    }
}
