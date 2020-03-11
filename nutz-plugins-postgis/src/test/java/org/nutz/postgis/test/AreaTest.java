package org.nutz.postgis.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.nutz.json.Json;
import org.nutz.lang.Streams;
import org.nutz.postgis.entity.Area;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 */
public class AreaTest {

    @Test
    public void test() {
        Area area = Json.fromJson(Area.class, Streams.fileInr("org/nutz/postgis/test/area.json"));
        // XXX 这个判断是无效的哦,需要判断center或者boundary字段的geo信息
        assertNotNull(area);
    }
}
