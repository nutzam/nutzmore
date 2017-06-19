package org.nutz.integration.neo4j;

import org.junit.Assert;
import org.junit.Test;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.nutz.ioc.ObjectProxy;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class Neo4jIocLoaderTest extends Assert {

    private static final Log log = Logs.get();

    @Test
    public void testNeo4jIocLoader() throws ClassNotFoundException {
        NutIoc ioc = new NutIoc(new ComboIocLoader("*neo4j"));
        ioc.getIocContext().save("app", "conf", new ObjectProxy(new PropertiesProxy()));
        assertTrue(ioc.getNames().length > 0);

        Driver driver = ioc.get(Driver.class, "neo4jd");
        try (Session session = driver.session()) {
            assertTrue(session.isOpen());
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            fail();
        }
    }

}
