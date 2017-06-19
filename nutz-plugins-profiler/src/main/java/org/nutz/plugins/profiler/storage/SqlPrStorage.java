package org.nutz.plugins.profiler.storage;

import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.lang.util.NutMap;
import org.nutz.plugins.profiler.PrSpan;
import org.nutz.plugins.profiler.PrStorage;

/**
 * 基于NutDao的存储
 * @author wendal
 *
 */
public class SqlPrStorage implements PrStorage {

    protected Dao dao;
    
    public SqlPrStorage(Dao dao) {
        super();
        this.dao = dao;
        this.dao.create(PrSpan.class, false);
    }

    public void save(PrSpan span) {
        dao.fastInsert(span);
    }

    public List<PrSpan> query(NutMap params) {
        Cnd cnd = Cnd.NEW();
        int page = 1;
        if (params != null) {
            cnd.andEX("traceId", "=", params.getString("trace_id"));
            cnd.andEX("spanId",  "=", params.getString("span_id"));
            cnd.andEX("name",  "=", params.getString("name"));
            cnd.andEX("createTime",  ">", params.getInt("create_time"));
            cnd.andEX("spantime",  ">", params.getInt("span_time"));
            page = params.getInt("page");
            if (page < 1)
                page = 1;
        }
        return dao.query(PrSpan.class, cnd, dao.createPager(page, 20));
    }

    public void clear() {
        dao.clear(PrSpan.class);
    }
}
