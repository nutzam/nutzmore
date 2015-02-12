package org.nutz.dao.impl.entity.xml;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Test;
import org.nutz.dao.DaoException;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.impl.DaoSupport;
import org.nutz.dao.impl.EntityHolder;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.entity.NutEntity;
import org.nutz.dao.util.Daos;
import org.nutz.resource.Scans;
import org.xml.sax.SAXException;

public class XmlEntityMakerTest extends org.nutz.dao.test.DaoCase {

	@Test
	public void testAddDocument() throws IOException, SAXException, ParserConfigurationException {
		EntityHolder holder = new EntityHolder((DaoSupport)dao);
		XmlEntityMaker maker = new XmlEntityMaker(null, null, holder);
		holder.maker = maker;
		maker.setPaths("org/nutz/dao/impl/entity/xml");
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void test_to_xml() throws ParserConfigurationException, TransformerException, IOException, SAXException {
		
		Entity en = dao.getEntity(org.nutz.dao.test.meta.Master.class);
		String str = XmlEntityUtil.asXML((NutEntity)en);
		System.out.println(str);
		
		EntityHolder holder = new EntityHolder((DaoSupport)dao);
		XmlEntityMaker maker = new XmlEntityMaker(null, null, holder);
		holder.maker = maker;
		maker.add(new ByteArrayInputStream(str.getBytes()));
		try {
			maker.verify();
			fail();
		} catch (DaoException e) {
			// TODO: handle exception
		}
		maker.add(new ByteArrayInputStream(XmlEntityUtil.asXML((NutEntity)dao.getEntity(org.nutz.dao.test.meta.Pet.class)).getBytes()));
		maker.verify();
		en = maker.make(org.nutz.dao.test.meta.Master.class);
		String str2 = XmlEntityUtil.asXML((NutEntity)en);
		System.out.println(str2);
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void test_pojo_to_xml() throws ParserConfigurationException, TransformerException, IOException, SAXException {
		EntityHolder holder = new EntityHolder((DaoSupport)dao);
		XmlEntityMaker maker = new XmlEntityMaker(null, null, holder);
		holder.maker = maker;
		for(Class<?> klass : Scans.me().scanPackage("org.nutz.dao")) {
			if (klass.getAnnotation(Table.class) != null) {
				Entity en;
				String str = null;
				try {
					en = dao.getEntity(klass);
				} catch (Exception e1) {
					continue;
				}
				try{
					str = XmlEntityUtil.asXML((NutEntity)en);
					maker.add(new ByteArrayInputStream(str.getBytes()));
				} catch (Throwable e) {
					System.out.println(str);
					e.printStackTrace(System.out);
					System.out.println(klass);
				}
			}
		}
		maker.verify();
	}
	
	@Test
	public void test_create_xml_table() throws Exception {
        XmlEntityMaker maker = new XmlEntityMaker();
        maker.setPaths("org/nutz/dao/impl/entity/xml");
        
		NutDao dao = new NutDao(ioc.get(DataSource.class), maker);
		dao.create(IotUser.class, true);
		
		Daos.queryCount(dao, "select * from t_pet");
		dao.execute(Sqls.create("select count(1) from (select * from t_pet) as A") ); 
	}
}
