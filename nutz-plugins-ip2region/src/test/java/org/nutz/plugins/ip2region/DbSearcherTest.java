package org.nutz.plugins.ip2region;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

public class DbSearcherTest extends Assert {

    @Test
    public void testMemorySearchString() throws IOException, InterruptedException {
        final DbSearcher searcher = new DbSearcher();
        
        ExecutorService es = Executors.newFixedThreadPool(64);
        final String ip2 = searcher.memorySearch("219.136.76.152").getRegion();
        System.out.println(ip2);
        final AtomicInteger atom = new AtomicInteger(0);
        for (int i = 0; i < 10000; i++) {
            es.submit(new Runnable() {
                public void run() {
                    try {
                        String ip = searcher.memorySearch("219.136.76.152").getRegion();
                        Assert.assertEquals(ip2, ip);
                        ip = searcher.btreeSearch("219.136.76.152").getRegion();
                        Assert.assertEquals(ip2, ip);
                        ip = searcher.binarySearch("219.136.76.152").getRegion();
                        Assert.assertEquals(ip2, ip);
                        atom.incrementAndGet();
                    }
                    catch (Throwable e) {
                        e.printStackTrace();
                        fail();
                    }
                }
            });
        }
        es.shutdown();
        es.awaitTermination(1, TimeUnit.MINUTES);
        assertEquals(10000, atom.get());
    }

}
