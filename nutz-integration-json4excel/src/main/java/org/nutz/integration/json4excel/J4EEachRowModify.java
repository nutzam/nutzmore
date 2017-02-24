package org.nutz.integration.json4excel;

import java.util.List;

import org.apache.poi.ss.usermodel.Row;

public interface J4EEachRowModify<T> {

    /**
     * 对每一行获得的数据做点什么, 可以进行数据预处理或直接插入数据库等等
     * 
     * @param rVal
     */
    public void doEach(T rVal, Row row, List<J4EColumn> cols);
}
