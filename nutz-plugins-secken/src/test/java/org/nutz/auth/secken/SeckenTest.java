package org.nutz.auth.secken;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.nutz.http.Http;

public class SeckenTest {
    
    Secken secken;

    @Before
    public void setUp() throws Exception {
        secken = new Secken("x5GU1gBYPxL0CszcgPcxtJTDkbsbP2Z4", "Er2jXT7jvCfY5zt2MSWx", "Y1MxTac4UnaqkvNQFYrX");
        Http.disableJvmHttpsCheck();
        Secken.DEBUG = true;
    }

    @Test
    public void testGetAuth() {
        SeckenResp sr;
        sr = secken.getAuth(1, null);
        System.out.println(sr);
        assertTrue(sr.ok());
        
        sr = secken.getAuth(1, "https://nutz.cn/cgi-bin/blackhole");
        System.out.println(sr);
        assertTrue(sr.ok());
    }

    @Test
    public void testGetBind() {
        SeckenResp sr = secken.getBind(1, null);
        System.out.println(sr);
        assertTrue(sr.ok());
        

        sr = secken.getBind(1, "https://nutz.cn/cgi-bin/blackhole");
        System.out.println(sr);
        assertTrue(sr.ok());
    }

    @Test
    public void testAuthPage() {
        SeckenResp sr = secken.authPage("http://nutz.cn");
        System.out.println(sr);
        assertTrue(sr.ok());
    }

    @Test
    public void testRealtimeAuth() {
        SeckenResp sr = secken.realtimeAuth(1, 1, "Cpqo6BOVJnO1eggyemQM3w==", null, null, null);
        System.out.println(sr);
        assertTrue(sr.ok());
    }

    @Test
    public void testOfflineAuth() {
        // 无法自动化测试
        //SeckenResp sr = secken.offlineAuth("397546", "Cpqo6BOVJnO1eggyemQM3w==");
        //System.out.println(sr);
        //assertTrue(sr.ok());
    }

    @Test
    public void testGetResult() {
        // 无法自动化测试
//        SeckenResp sr = secken.getResult("1442533055.84FYuteJA");
//        System.out.println(sr);
//        assertTrue(sr.ok());
    }
}
