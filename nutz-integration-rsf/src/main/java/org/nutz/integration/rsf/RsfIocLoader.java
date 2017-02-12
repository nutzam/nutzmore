package org.nutz.integration.rsf;

import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.Scans;

import net.hasor.rsf.RsfService;

/**
 * 用法 <code>@IocBy(args={..., "*rsf", "net.wendal.nutzbook.service"})</code>
 * @author wendal
 *
 */
public class RsfIocLoader extends JsonLoader {
    
    private static final Log log = Logs.get();

    public RsfIocLoader(String... pkgs) {
        super("org/nutz/integration/rsf/rsf.js");
        for (String pkg : pkgs) {
            add(pkg);
        }
    }
    
    protected void add(String pkg) {
        // 根据所声明的pkg,扫描RsfService注解标注的类(通常是接口),然后生成代理bean
        for (Class<?> klass : Scans.me().scanPackage(pkg)) {
            RsfService rsf = klass.getAnnotation(RsfService.class);
            if (rsf != null) {
                String name = rsf.name();
                if (Strings.isBlank(name))
                    name = Strings.lowerFirst(klass.getSimpleName());
                log.debugf("define rsf bean name=%s type=%s", name, klass.getName());
                NutMap _map = new NutMap().setv("factory", "$rsfClient#wrapper");
                _map.setv("args", new String[] { klass.getName() });
                _map.setv("type", klass.getName());
                map.put(name, _map);
            }
        }
    }
}
