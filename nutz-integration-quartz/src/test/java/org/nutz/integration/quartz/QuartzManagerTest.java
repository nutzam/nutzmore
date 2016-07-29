package org.nutz.integration.quartz;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.ObjectProxy;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.quartz.JobKey;

public class QuartzManagerTest {
    
    NutIoc ioc;
    QuartzManager manager;

    @Before
    public void setUp() throws Exception {
        ioc = new NutIoc(new ComboIocLoader("*quartz"));
        ioc.getIocContext().save("app", "conf", new ObjectProxy(new PropertiesProxy()));
        manager = ioc.get(QuartzManager.class);
        manager.clear();
    }

    @After
    public void tearDown() throws Exception {
        if (ioc != null)
            ioc.depose();
    }

    @Test
    public void testCronStringClassOfQ() {
        manager.cron("0 0/5 * * * ?", SimpleJob.class);
        assertEquals(1, manager.query(null, null, null).size());
    }

    @Test
    public void testCronStringClassOfQStringString() {
        manager.cron("0 0/5 * * * ?", SimpleJob.class, "simple", "simple");
        assertEquals(1, manager.query(null, null, null).size());
    }

    @Test
    public void testQuery() {
        for (int i = 0; i < 99; i++) {
            manager.cron("0 0/5 * * * ?", SimpleJob.class, "simple"+String.format("%02d", i), "simple");
        }
        assertEquals(99, manager.query(null, null, null).size());
        assertEquals(10, manager.query(null, null, new Pager().setPageNumber(1).setPageSize(10)).size());
        // 没法排序?
        //assertEquals("simple09", manager.query(null, null, new Pager().setPageNumber(1).setPageSize(10)).get(9).getJobName());
        
        assertEquals(9, manager.query(null, null, new Pager().setPageNumber(10).setPageSize(10)).size());
        

        assertEquals(1, manager.query("90$", null, null).size());
        assertEquals(10, manager.query("0$", null, null).size());
    }

    @Test
    public void testDeleteQuartzJob() {
        manager.cron("0 0/5 * * * ?", SimpleJob.class, "simple", "simple");
        QuartzJob qj = new QuartzJob();
        qj.setJobName("simple");
        qj.setJobGroup("simple");
        manager.delete(qj);
        assertEquals(0, manager.query(null, null, null).size());
    }

    @Test
    public void testDeleteJobKey() {
        manager.cron("0 0/5 * * * ?", SimpleJob.class, "simple", "simple");
        manager.delete(new JobKey("simple", "simple"));
        assertEquals(0, manager.query(null, null, null).size());
    }

    @Test
    public void testAddStringStringStringClassOfQ() {
        manager.add("simple", "simple", "0 0/5 * * * ?", SimpleJob.class);
        assertEquals(1, manager.query(null, null, null).size());
    }

    @Test
    public void testAddQuartzJob() {
        QuartzJob qj = new QuartzJob();
        qj.setJobName("simple");
        qj.setJobGroup("simple");
        qj.setClassName(SimpleJob.class.getName());
        qj.setCron("0 0/5 * * * ?");
        
        manager.add(qj);
        
        assertEquals(1, manager.query(null, null, null).size());
        QuartzJob f = manager.query(null, null, null).get(0);
        assertEquals(f.getCron(), "0 0/5 * * * ?");
    }

    @Test
    public void testExistQuartzJob() {
        QuartzJob qj = new QuartzJob();
        qj.setJobName("simple");
        qj.setJobGroup("simple");
        qj.setClassName(SimpleJob.class.getName());
        qj.setCron("0 0/5 * * * ?");
        
        assertFalse(manager.exist(qj));
        
        manager.add(qj);
        
        assertTrue(manager.exist(qj));
    }

    @Test
    public void testExistJobKey() {
        QuartzJob qj = new QuartzJob();
        qj.setJobName("simple");
        qj.setJobGroup("simple");
        qj.setClassName(SimpleJob.class.getName());
        qj.setCron("0 0/5 * * * ?");
        
        assertFalse(manager.exist(new JobKey("simple", "simple")));
        
        manager.add(qj);
        
        assertTrue(manager.exist(new JobKey("simple", "simple")));
    }

    @Test
    public void testResumeJobKey() {
        // TODO 这么测...
    }

    @Test
    public void testResumeQuartzJob() {
       // TODO 这么测...
    }

    @Test
    public void testClear() {
        testExistJobKey();
        manager.clear();
        assertEquals(0, manager.query(null, null, null).size());
    }

    @Test
    public void testPauseQuartzJob() {
        // TODO 这么测...
    }

    @Test
    public void testPauseJobKey() {
     // TODO 这么测...
    }

    @Test
    public void testInterruptJobKey() {
     // TODO 这么测...
    }

    @Test
    public void testInterruptQuartzJob() {
     // TODO 这么测...
    }

    @Test
    public void testGetState() {
     // TODO 这么测...
    }

}
