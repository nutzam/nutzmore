package org.nutz.integration.hasor;
import net.hasor.core.*;
import net.hasor.core.utils.StringUtils;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.ioc.meta.IocObject;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.List;
import java.util.Set;
/**
 * 用法 <code>@IocBy(args={..., "*hasor", "net.wendal.nutzbook.service"})</code>
 * @author wendal
 */
public class HasorIocLoader extends JsonLoader implements LifeModule {
    private static final Log log = Logs.get();
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~IocLoader
    public HasorIocLoader() {
        super("org/nutz/integration/hasor/hasor.js");
    }
    @Override
    public String[] getName() {
        return super.getName();
    }
    @Override
    public boolean has(String name) {
        return super.has(name);
    }
    @Override
    public IocObject load(IocLoading loading, String name) throws ObjectLoadException {
        return super.load(loading, name);
    }
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Create Hasor
    //
    protected Ioc             ioc;
    protected PropertiesProxy nutzConfig;
    protected AppContext      appContext;
    public void init() {
        Hasor creter = Hasor.create().putAllData(this.nutzConfig);
        //
        String hasorConfig = this.nutzConfig.get("hasor.config");
        if (!StringUtils.isBlank(hasorConfig)) {
            creter.setMainSettings(hasorConfig);
        }
        //
        this.appContext = creter.build(this);
    }
    public void shutdown() {
        this.appContext.shutdown();
        this.appContext = null;
    }
    public AppContext getAppContext() {
        return this.appContext;
    }
    //
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // .Hasor加载
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        // .注册 Nutz 的Ioc容器到 Hasor 容器中
        apiBinder.bindType(Ioc.class).toInstance(this.ioc);
        //
        // .扫描所有配置了 @Configuration 注解的类，并将 Hasor 的 Module 加载进来
        //   -- 包扫描范围通过 Nutz 配置文件以环境变量方式传递进来。
        Set<Class<?>> aClass = apiBinder.findClass(Configuration.class);
        for (final Class klass : aClass) {
            if (klass == Configuration.class)
                continue;
            if (!Module.class.isAssignableFrom(klass)) {
                continue;
            }
            //
            Module newInstance = (Module) klass.newInstance();
            apiBinder.installModule(newInstance);
        }
    }
    // .Hasor启动（取得所有服务名称）
    @Override
    public void onStart(AppContext appContext) throws Throwable {
        RsfContext instance = appContext.getInstance(RsfContext.class);
        List<String> serviceIDs = instance.getServiceIDs();
        for (String serviceID : serviceIDs) {
            RsfBindInfo<?> serviceInfo = instance.getServiceInfo(serviceID);
            //
        }
    }
    // .Hasor销毁
    @Override
    public void onStop(AppContext appContext) throws Throwable {
        //
    }
}
