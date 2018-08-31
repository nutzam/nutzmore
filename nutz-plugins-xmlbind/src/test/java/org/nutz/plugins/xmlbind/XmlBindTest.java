package org.nutz.plugins.xmlbind;

import org.junit.Assert;
import org.junit.Test;
import org.nutz.json.Json;
import org.nutz.lang.Xmls;
import org.nutz.plugins.xmlbind.bean.GpxFile;
import org.nutz.plugins.xmlbind.entity.XmlEntity;
import org.nutz.plugins.xmlbind.entity.XmlEntityAnnotationMaker;
import org.w3c.dom.Element;

public class XmlBindTest extends Assert {

    @Test
    public void test_simple_parse() {
        XmlEntity<GpxFile> en = new XmlEntityAnnotationMaker().makeEntity(null, GpxFile.class);
        //assertTrue(en.getAttrs().size() > 0);
        assertTrue(en.getNodes().size() > 0);
        assertNotNull(en.getNodes().get("rtept"));
        
        System.out.println(Json.toJson(en));
    }

    @Test
    public void test_read_trk() {
        XmlEntity<GpxFile> en = new XmlEntityAnnotationMaker().makeEntity(null, GpxFile.class);
        Element top = Xmls.xml(getClass().getClassLoader().getResourceAsStream("gpxfiles/trk.gpx")).getDocumentElement();
        System.out.println(top);
        GpxFile gpx = en.read(top);
        assertNotNull(gpx);
        System.out.println(Json.toJson(gpx));
    }
    
    @Test
    public void test_read_rte() {
        XmlEntity<GpxFile> en = new XmlEntityAnnotationMaker().makeEntity(null, GpxFile.class);
        Element top = Xmls.xml(getClass().getClassLoader().getResourceAsStream("gpxfiles/rte.gpx")).getDocumentElement();
        System.out.println(top);
        GpxFile gpx = en.read(top);
        assertNotNull(gpx);
        System.out.println(Json.toJson(gpx));
    }
    

    @Test
    public void test_read_combo() {
        XmlEntity<GpxFile> en = new XmlEntityAnnotationMaker().makeEntity(null, GpxFile.class);
        Element top = Xmls.xml(getClass().getClassLoader().getResourceAsStream("gpxfiles/combo.gpx")).getDocumentElement();
        System.out.println(top);
        GpxFile gpx = en.read(top);
        assertNotNull(gpx);
        System.out.println(Json.toJson(gpx));
    }
    
    @Test
    public void test_write_trk() {
        XmlEntity<GpxFile> en = new XmlEntityAnnotationMaker().makeEntity(null, GpxFile.class);
        Element top = Xmls.xml(getClass().getClassLoader().getResourceAsStream("gpxfiles/trk.gpx")).getDocumentElement();
        System.out.println(top);
        GpxFile gpx = en.read(top);
        assertNotNull(gpx);
        System.out.println(Json.toJson(gpx));
        String text = en.write(gpx, "gpx");
        System.out.println("XML===>\r\n" + text);
    }
}
