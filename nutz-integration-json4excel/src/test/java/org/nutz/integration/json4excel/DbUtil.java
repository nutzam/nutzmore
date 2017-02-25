package org.nutz.integration.json4excel;

import org.junit.After;
import org.junit.Before;
import org.nutz.dao.Dao;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.json.JsonLoader;

public abstract class DbUtil extends TestUtil {

    protected Dao dao;
    protected Ioc ioc;

    @Before
    public void setUp() {
        ioc = new NutIoc(new JsonLoader("dao-test.js"));
        dao = ioc.get(Dao.class, "dao");
        before();
    }

    @After
    public void tearDown() {
        after();
    }

    protected void before() {}

    protected void after() {}
}
