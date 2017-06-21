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
    public void test_simple() {
        PrSpan span = Pr.begin("junit.level1");
        
        for (int i = 0; i < 5; i++) {
            PrSpan loop = Pr.begin("junit.level2");
            for (int j = 0; j < 2; j++) {
                Pr.begin("junit.level3").end(); // 这里产生 2*5=10条
            }
            loop.end();//这里产生5条
        }
        
        span.end(); // 这里产生1条
        Lang.quiteSleep(1000);
        List<PrSpan> list = Pr.me().getStorage().query(null);
        assertNotNull(list);
        assertEquals(16, list.size());
        
    }

}
