package org.nutz.plugins.profiler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.SimpleDataSource;
import org.nutz.lang.util.Callback;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.profiler.impl.DefaultPr;
import org.nutz.plugins.profiler.storage.SqlPrStorage;

public abstract class Pr implements Callback<PrSpan> {

    private static final Log log = Logs.get();

    private static Pr me = new DefaultPr();

    public static Pr me() {
        return me;
    }

    protected PrStorage storage;

    protected ExecutorService es;

    public void setStorage(PrStorage storage) {
        this.storage = storage;
    }

    public PrStorage getStorage() {
        return storage;
    }

    public void setExecutorService(ExecutorService es) {
        this.es = es;
    }

    public void setup() {
        if (this.es == null)
            es = Executors.newFixedThreadPool(64);
        if (this.storage == null) {
            log.info("using h2database as 'InMemory' PrStorage");
            SimpleDataSource ds = new SimpleDataSource();
            ds.setJdbcUrl("jdbc:h2:~/profiler");
            Dao dao = new NutDao(ds);
            this.storage = new SqlPrStorage(dao);
        }
    }

    public void setup(Dao dao) {
        this.storage = new SqlPrStorage(dao);
        setup();
    }

    public void shutdown() {
        if (this.es != null || !this.es.isShutdown())
            es.shutdown();
        es = null;
    }

    public static PrSpan begin(String name) {
        return me().begin(name, null, null);
    }

    public abstract PrSpan begin(String type,
                                 String trace_id,
                                 String parent_id);
}
