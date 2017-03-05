package org.nutz.plugins.hotplug;

import java.util.HashMap;

import org.nutz.ioc.IocLoader;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.UrlMapping;
import org.nutz.mvc.impl.ActionInvoker;
import org.nutz.mvc.impl.MappingNode;
import org.nutz.resource.impl.ResourceLocation;

public class HotplugConfig extends NutMap {

    private static final long serialVersionUID = 1L;
    
    /**
     * 映射类别
     */
    protected UrlMapping urlMapping;
    
    protected MappingNode<ActionInvoker> mapping_root;
    /**
     * IocLoader,即Ioc配置加载器
     */
    protected IocLoader iocLoader;
    /**
     * 资源文件,例如js/css/png等
     */
    protected HashMap<String, HotplugAsset> assets;
    /**
     * 模板数据,一般都是文本吧!!
     */
    protected HashMap<String, String> tmpls;
    
    protected ClassLoader classLoader;
    
    protected ResourceLocation resourceLocation;

    public String getName() {
        return getString("name");
    }
    public String getVersion() {
        return getString("version", "1.0.0");
    }
    public String getBase() {
        return getString("base");
    }
    public String getMain() {
        return getString("main");
    }
    public boolean isEnable() {
        return  getBoolean("enable", false);
    }
    public String getOrigin() {
        return getString("origin");
    }
    public String getOriginPath() {
        return getString("origin_path");
    }
    public String getSha1() {
        return getString("sha1");
    }
    public UrlMapping getUrlMapping() {
        return urlMapping;
    }
    public void setUrlMapping(UrlMapping urlMapping) {
        this.urlMapping = urlMapping;
    }
    public IocLoader getIocLoader() {
        return iocLoader;
    }
    public void setIocLoader(IocLoader iocLoader) {
        this.iocLoader = iocLoader;
    }
    public HashMap<String, HotplugAsset> getAssets() {
        return assets;
    }
    public void setAssets(HashMap<String, HotplugAsset> assets) {
        this.assets = assets;
    }
    public HashMap<String, String> getTmpls() {
        return tmpls;
    }
    public void setTmpls(HashMap<String, String> tmpls) {
        this.tmpls = tmpls;
    }
    public ClassLoader getClassLoader() {
        return classLoader;
    }
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }
    public void setResourceLocation(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }
}
