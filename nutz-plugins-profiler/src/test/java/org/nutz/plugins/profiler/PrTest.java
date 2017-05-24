package org.nutz.plugins.profiler;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nutz.lang.Lang;

public class PrTest {

    @Before
    public void setup() {
        assertNotNull(Pr.me());
        Pr.me().setup();
        Pr.me().getStorage().clear();
    }
    
    @After
    public void depose() {
        Pr.me().shutdown();
    }
    
    @Test
    public void testBeginStringString() {
        PrSpan span = Pr.begin("junit", "first class");
        Lang.quiteSleep(1000);
        span.end();
        Lang.quiteSleep(1000);
        List<PrSpan> list = Pr.me().getStorage().query(null);
        assertNotNull(list);
        assertEquals(1, list.size());
    }

}
