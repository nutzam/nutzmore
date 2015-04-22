package org.nutz.integration.quartz;

import java.io.StringReader;

import org.nutz.ioc.IocLoading;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.ioc.meta.IocObject;
import org.nutz.json.Json;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 预定义的Quartz Ioc配置
 * @author wendal
 *
 */
public class QuartzIocLoader extends JsonLoader {

	private static final Log log = Logs.get();
	
    protected JsonLoader proxy;
    
    public QuartzIocLoader(String...args) {
    	String confName = args.length > 0 ? args[0] : "conf";
        StringBuilder sb = new StringBuilder("{");
        sb.append("scheduler:{type : 'org.quartz.Scheduler',\n");
        sb.append("factory:'org.quartz.impl.StdSchedulerFactory#getDefaultScheduler',events:{\n");
        sb.append("create:'start',depose:'shutdown',},\n");
        sb.append("fields:{jobFactory:{refer:'jobFactory'}}},");
        sb.append("jobFactory:{type:'org.nutz.integration.quartz.NutQuartzJobFactory', args:[{refer:'$ioc'}]},");
        sb.append("nutQuartzCronJobFactory:{type:'org.nutz.integration.quartz.NutQuartzCronJobFactory', events:{create:'init'}, fields:{'scheduler':{refer:'scheduler'}, conf:{refer:'" + confName + "'}}}");
        sb.append("}");
        String json = Json.toJson(Json.fromJson(sb.toString()));
        log.debug("Quartz Ioc Define as:\n" + json);
        proxy = new JsonLoader(new StringReader(json));
    }
    
    public String[] getName() {
        return proxy.getName();
    }

    public IocObject load(IocLoading loading, String name) throws ObjectLoadException {
        return proxy.load(loading, name);
    }

    public boolean has(String name) {
        return proxy.has(name);
    }

}
