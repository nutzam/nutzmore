package org.nutz.integration.spring;

import org.junit.Test;
import org.nutz.dao.Dao;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringTest {

    @Test
    public void test_simple() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring-beans.xml");
        Dao dao = ctx.getBean(Dao.class);
        dao.create(Pet.class, true);
        ctx.close();
    }
}
