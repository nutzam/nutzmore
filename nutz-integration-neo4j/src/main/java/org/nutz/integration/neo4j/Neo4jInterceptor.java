package org.nutz.integration.neo4j;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;

public class Neo4jInterceptor implements MethodInterceptor {

    protected Driver driver;

    protected static ThreadLocal<Session> TL = new ThreadLocal<Session>();

    public void filter(InterceptorChain chain) throws Throwable {
        if (TL.get() != null) {
            chain.doChain();
            return;
        }
        try (Session session = driver.session()) {
            TL.set(session);
            chain.doChain();
        }
        finally {
            TL.remove();
        }
    }

    public static Session neo4j() {
        return TL.get();
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}
