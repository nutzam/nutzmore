package org.nutz.integration.rsf;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Strings;
import org.nutz.mvc.Mvcs;
import org.nutz.resource.Scans;

import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.rsf.RsfApiBinder;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfModule;
import net.hasor.rsf.RsfService;

public class RsfFactory extends RsfModule {

    /**
     * 需要扫描的package
     */
    protected String packages;
    /**
     * server-config.xml或client-config.xml
     */
    protected String main;
    /**
     * server或client模式
     */
    protected String mode;
    
    /**
     * ioc容器本身
     */
    protected Ioc ioc;
    
    protected AppContext app;
    protected RsfClient client;
    
    public void init() {
        app = Hasor.createAppContext(main, this);
        client = app.getInstance(RsfClient.class);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void loadModule(RsfApiBinder apiBinder) throws Throwable {
        for (String pkg : Strings.splitIgnoreBlank(packages)) {
            for (Class klass : Scans.me().scanPackage(pkg)) {
                RsfService service = (RsfService) klass.getAnnotation(RsfService.class);
                if (service != null) {
                    if ("server".equals(mode)) {
                        apiBinder.rsfService(klass).toInstance(ioc.get(klass)).register();
                    }
                    else {
                        apiBinder.rsfService(klass).register();
                    }
                }
            }
        }
    }
    
    public AppContext getApp() {
        return app;
    }
    
    public RsfClient getClient() {
        return client;
    }
    
    public void setPackages(String packages) {
        if (packages == null)
            packages = Mvcs.ctx().getDefaultNutConfig().getMainModule().getPackage().getName();
        this.packages = packages;
    }
    
    public void setMode(String mode) {
        this.mode = mode;
    }
    
    public void setIoc(Ioc ioc) {
        this.ioc = ioc;
    }
    
    public void setMain(String main) {
        this.main = main;
    }
    
}
