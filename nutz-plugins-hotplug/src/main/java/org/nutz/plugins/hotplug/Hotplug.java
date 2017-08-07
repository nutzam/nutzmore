package org.nutz.plugins.hotplug;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.ObjectProxy;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.impl.ScopeContext;
import org.nutz.ioc.loader.annotation.AnnotationIocLoader;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Disks;
import org.nutz.lang.util.FileVisitor;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionChainMaker;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.mvc.UrlMapping;
import org.nutz.mvc.ViewMaker;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.SetupBy;
import org.nutz.mvc.impl.NutLoading;
import org.nutz.mvc.impl.ServletValueProxyMaker;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;
import org.nutz.resource.impl.JarResourceLocation;

/**
 * 动态增减web插件
 * 
 * 用法:  <p/>
 * 主项目的MainModule标注<code>@LoadingBy(HotPlug.class)</code><p/>
 * 插件项目的格式要求: 需要一个XXXMainModule, 必须带一个hotplug.XXX.json文件,且文件格式如下<p/>
 * <code>{name:"cms", "version":"1.0", "base": "net.wendal.nutzbook.cms", "main":"net.wendal.nutzbook.cms.CmsMainModule"}</code>
 * @author wendal
 *
 */
@SuppressWarnings("unchecked")
public class Hotplug extends NutLoading {
    
    private static final Log log = Logs.get();
    
    protected static Properties hpconf = new Properties();
    
    protected HotplugClassLoader hpcl;
    
    protected static Hotplug me;
    
    public Hotplug() throws IOException {
        me = this;
        hpcl = new HotplugClassLoader(Thread.currentThread().getContextClassLoader());
        InputStream ins = getClass().getClassLoader().getResourceAsStream("/hotplug.properties");
        if (ins != null) {
            hpconf.load(ins);
        }
        Scans.me().addResourceLocation(new HotplugResourceLocation());
    }

    /**
     * 保持已有的Ioc容器,映射插件的@At的时候需要用到
     */
    protected Ioc ioc;
    //-----------------------------------------------------
    // 为了动态增减ioc内的对象,需要hack一下NutIoc内的私有属性
    protected ScopeContext scopeContext;
    //-----------------------------------------------------
    
    /**
     *  主项目的配置对象
     */
    protected NutConfig config;
    
    /**
     * 代理原有的映射关系,优先使用插件的映射关系
     */
    protected HotplugUrlMapping ump;
    
    /**
     * 插件列表,为了方便,这里直接用静态属性了
     */
    protected static Map<String, HotplugConfig> _plugins = new LinkedHashMap<String, HotplugConfig>();
    
    /**
     * 主项目的@Ok/@Fail处理类,新增插件时需要用到.
     */
    protected ViewMaker[] views;
    
    protected ActionChainMaker chainMaker;
    
    @Override
    public UrlMapping load(NutConfig config) {
        this.config = config; // 保存起来,后面会用到
        UrlMapping um = super.load(config);
        ump = new HotplugUrlMapping(um, config.getServletContext()); // 代理之
        return ump;
    }
    
    protected Ioc createIoc(NutConfig config, Class<?> mainModule) throws Exception {
        NutIoc ioc = null;
        IocBy ib = mainModule.getAnnotation(IocBy.class);
        if (ib == null)
            throw new RuntimeException("Ioc is needed!");
        List<String> argList = new ArrayList<String>();
        String[] args = ib.args();
        for (String arg : args) {
            argList.add(arg);
        }
        if (!argList.contains("*hotplug"))
            argList.add("*hotplug");
        args = argList.toArray(new String[argList.size()]);
        if (log.isDebugEnabled())
            log.debugf("@IocBy(type=%s, args=%s,init=%s)",
                           ComboIocLoader.class,
                           Json.toJson(args),
                           Json.toJson(ib.init()));
        scopeContext = new ScopeContext("app");
        ioc = new NutIoc(new ComboIocLoader(args), scopeContext, "app");
        ioc.addValueProxyMaker(new ServletValueProxyMaker(config.getServletContext()));
        // 将自身放入ioc容器, 这样就能通过主项目的入口方法调用本类的方法
        ((NutIoc)ioc).getIocContext().save("app", "hotplug", new ObjectProxy(this));
        Mvcs.setIoc(ioc);
        return ioc;
    }
    
    protected ViewMaker[] createViewMakers(Class<?> mainModule, Ioc ioc) throws Exception {
        if (views == null) //保持主项目的@Ok/@Fail处理器
            views = super.createViewMakers(mainModule, ioc);
        return views;
    }
    
    @Override
    protected ActionChainMaker createChainMaker(NutConfig config, Class<?> mainModule) {
        if (chainMaker == null)
            chainMaker = super.createChainMaker(config, mainModule);
        return chainMaker;
    }
    
    /**
     * 载入一个插件,必须符合特定的jar包
     * @param f 插件jar文件,通过文件上传/数据库读取等方式保存到本地,然后调用本方法
     * @return 插件信息
     * @throws Exception
     */
    public HotplugConfig enable(File f, HotplugConfig hc) throws Exception {
        if (hc == null)
            hc = checkHotplugFile(f);
        try {
            if (Hotplug._plugins.containsKey(hc.getName()))
                disable(hc.getName());
        } catch (Exception e) {
            log.info("something happen when remove old hotplug", e);
        }
        // 首先,我们需要解析这个jar. Jar文件也是Zip. 解析完成前,还不会影响到现有系统的运行
        ZipFile zf = new ZipFile(f);
        Enumeration<ZipEntry> en = (Enumeration<ZipEntry>) zf.entries();
        HashMap<String, HotplugAsset> assets = new HashMap<String, HotplugAsset>();
        HashMap<String, String> tmpls = new HashMap<String, String>();
        while (en.hasMoreElements()) {
            ZipEntry ze = en.nextElement();
            String name = ze.getName();
            if (name.endsWith("/"))
                continue;
            // 解析资源文件
            if (name.startsWith("assets/")) {
                byte[] buf = Streams.readBytes(zf.getInputStream(ze));
                HotplugAsset asset = new HotplugAsset(buf);
                assets.put(name.substring("assets/".length()), asset);
            } else if (name.startsWith("templates/")) {
                tmpls.put(name.substring("templates/".length()), new String(Streams.readBytes(zf.getInputStream(ze))));
            }
        }
        zf.close();
        hc.assets = assets;
        hc.tmpls = tmpls;
        // 解析完成, 开始影响现有系统.
        // -----------------------------------------------------
        
        
        // 先保存当前的类加载器
        ClassLoader prevClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            // 构建URLClassLoader,开始加载这个jar包
            URLClassLoader classLoader = new URLClassLoader(new URL[]{f.toURI().toURL()}, hpcl);
            hc.classLoader = classLoader;
            
            // 放入插件列表, 因为tmpls已经生效,所以会影响beetl的模板加载系统(其实嘛,一点问题没有...)
            _plugins.put(hc.getName(), hc);
            // 将其设置为线程上下文的ClassLoader, 这样才能是下面的资源扫描和类扫描生效
            Thread.currentThread().setContextClassLoader(classLoader);
            // 添加资源扫描路径,为下面的类扫描打下基础, 这里开始影响"资源扫描子系统",其实也是没一点问题...
            hc.resourceLocation = new JarResourceLocation(f.getAbsolutePath());
            
            abc(hc);
            new File(f.getParent(), f.getName() + ".enable").createNewFile();
            hc.put("enable", true);
        } catch (Exception e) {
            disable(hc.getName());
            throw e;
        } finally {
            // 还原ClassLoader
            Thread.currentThread().setContextClassLoader(prevClassLoader);
        }
        return hc;
    }
    
    public void disable(String key) {
        HotplugConfig hc = _plugins.get(key);
        if (hc == null) {
            return;
        }
        if ("file".equals(hc.getOrigin())) {
            try {
                new File(hc.getOriginPath() + ".enable").delete();
            } catch (Throwable e) {
            }
        }
        // 移除URL映射, 对外服务停止.
        // 移除出插件列表, 同时移除静态资源和URL映射.
        hc.urlMapping = null;
        // 如果存在iocLoader,清理一下. 为空的可能性,只有初始化过程中抛出异常,但,还没写呢...
        if (hc.iocLoader != null) {
            // 变量所持有的ioc bean,逐一销毁
            for (String beanName : hc.iocLoader.getName()) {
                try {
                    ObjectProxy op = scopeContext.fetch(beanName);
                    if (op == null)
                        continue;
                    op.depose();
                }
                catch (Exception e) {
                    log.debug("depose hotplug bean fail", e);
                }
            }
            // 移除ioc上下文中存在的ioc bean
            for (String beanName : hc.iocLoader.getName()) {
                scopeContext.remove("app", beanName);
            }
        }
        if ("file".equals(hc.getOrigin()) && hc.classLoader != null && hc.getClassLoader() instanceof URLClassLoader) {
            try {
                ((URLClassLoader)hc.classLoader).close();
            } catch (Throwable e) {
                log.warn("something happen when close UrlClassLoader", e);
            }
        }
        _plugins.remove(key);
    }
    
    protected List<Setup> setups = new ArrayList<Setup>();
    
    protected void setupInit(Class<?> klass) {
        Setup setup;
        if (klass.getAnnotation(IocBean.class) != null)
            setup = (Setup) ioc.get(klass);
        else
            setup = (Setup) Mirror.me(klass).born();
        setups.add(setup);
        setup.init(config);
    }
    
    public void setupInit() {
        List<NutResource> list = Scans.me().scan("hotplug/", ".+.(js|json)$");
        List<HotplugConfig> hclist = new ArrayList<HotplugConfig>();
        for (NutResource nr : list) {
            log.debug("Check " + nr.getName());
            try {
                HotplugConfig hc = Json.fromJson(HotplugConfig.class, nr.getReader());
                log.debugf("Found name=%s base=%s", hc.getName(), hc.getBase());hclist.add(hc);
                hc.put("origin", "embed");
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        //TODO 自定义顺序
        sort(hclist);
        
        // 加载内置插件
        for (HotplugConfig hc : hclist) {
            hc.put("enable", true);
            hc.classLoader = getClass().getClassLoader();
            hc.assets = new HashMap<String, HotplugAsset>();
            hc.tmpls = new HashMap<String, String>();
            _plugins.put(hc.getName(), hc);
            log.debug("init hotplug name=" + hc.getName());
            try {
                abc(hc);
            }
            catch (Exception e) {
                log.error("fail at hotplug name=" + hc.getName());
                throw new RuntimeException(e);
            }
        }
        hclist = getHotPlugJarList(false);
        sort(hclist);
        // 加载外部插件
        for (HotplugConfig hc : hclist) {
            log.infof("hotplug name=%s version=%s enable=%s", hc.getName(), hc.getVersion(), hc.isEnable());
            if (!hc.isEnable()) {
                continue;
            }
            String path = hc.getOriginPath();
            log.debugf("hotplug from dir path=%s", path);
            try {
                enable(new File(path), hc);
            }
            catch (Exception e) {
                log.error("load hotplug fail!!! path=" + path, e);
                throw Lang.wrapThrow(e);
            }
        }
    }
    
    public void sort(List<HotplugConfig> hclist) {
        Collections.sort(hclist, new Comparator<HotplugConfig>() {
            public int compare(HotplugConfig prev, HotplugConfig next) {
                if (prev.getName().equals(next.getName()))
                    return 0;
                if ("core".equals(prev.getName()))
                    return -1;
                else if ("core".equals(next.getName()))
                    return 1;
                return prev.getName().compareTo(next.getName());
            }
        });
    }
    
    public void setupDestroy(){
        for (Setup setup : setups) {
            setup.destroy(config);
        }
    }
    
    public void abc(HotplugConfig hc) throws Exception {
        String mainClass = hc.getMain();
        if (Strings.isBlank(mainClass)) {
            mainClass = hc.getBase() + "." + Strings.upperFirst(hc.getName()) + "MainModule";
        }
        Class<?> klass = hc.getClassLoader().loadClass(mainClass);
        // 放入NutIoc的Ioc加载器列表,开始影响"Ioc子系统", 恩, 一般情况下很好.
        // 加载旗下的@IocBean
        IocBy iocBy = klass.getAnnotation(IocBy.class);
        if (iocBy == null)
            hc.iocLoader = new AnnotationIocLoader(hc.getBase());
        else
            hc.iocLoader = new ComboIocLoader(iocBy.args());
        // 生成插件的URL映射, 即@At的配置
        UrlMapping um = evalUrlMapping(config, klass, ioc);
        // 看看有无setupby
        SetupBy setupBy = klass.getAnnotation(SetupBy.class);
        if (setupBy != null) {
            setupInit(setupBy.value());
        } else {
            try {
                Class<?> setupClass = hc.getClassLoader().loadClass(hc.getBase() + "." + Strings.upperFirst(hc.getName()) + "MainSetup");
                setupInit(setupClass);
            } catch (ClassNotFoundException e) {
            }
        }
        // 赋值给hc,然后让ump添加映射表, 正式对外服务的开始, 插件相关的URL可以访问了
        hc.urlMapping = um;
    }
    
    public static File find(String key) {
        String dir = hpconf.getProperty("hotplug.parent_projects");
        if (dir != null) {
            if (key.contains("?")) {
                key = key.substring(0, key.indexOf('?'));
            }
            File parentProjectRoot = new File(dir);
            if (parentProjectRoot.isDirectory()) {
                for (String subprojectName : parentProjectRoot.list()) {
                    if (!new File(parentProjectRoot, subprojectName).isDirectory())
                        continue;
                    File f = new File(dir
                                      + "/"
                                      + subprojectName
                                      + "/src/main/resources/"
                                      + key);
                    if (f.exists() && f.isFile()) {
                        log.debugf("found %s", f.getAbsolutePath());
                        return f;
                    }
                    f = new File(dir + "/" + subprojectName + "/conf/" + key);
                    if (f.exists() && f.isFile()) {
                        log.debugf("found %s", f.getAbsolutePath());
                        return f;
                    }
                }
            }
        }
        return null;
    }
    
    public static Properties getHpconf() {
        return hpconf;
    }
    
    public static String getLibPath() {
        return hpconf.getProperty("hotplug.localdir", "/var/lib/hotplug");
    }
    
    public static List<HotplugConfig> getHotPlugJarList(final boolean getAll) {
        final List<HotplugConfig> list = new ArrayList<HotplugConfig>();

        String hcdir = getLibPath();
        File f = new File(Disks.normalize(hcdir));
        log.debug("check hotplug.localdir : " + f.getAbsolutePath());
        if (f.exists() && f.isDirectory()) {
            Disks.visitFile(f, new FileVisitor() {
                public void visit(File file) {
                    if (file.isDirectory())
                        return;
                    HotplugConfig hc = checkHotplugFile(file);
                    if (hc == null) {
                        log.debug("not hotplug : "+ file.getAbsolutePath());
                        return;
                    }
                    if (log.isDebugEnabled())
                        log.debugf("found hotplug name=%s version=%s enable=%s", hc.getName(), hc.getVersion(), hc.isEnable());
                    list.add(hc);
                }
            }, new FileFilter() {
                public boolean accept(File f) {
                    if (f.isDirectory())
                        return true;
                    return f.getName().endsWith(".jar");
                }
            });
        }
        return list;
    }
    
    public static HotplugConfig checkHotplugFile(File f) {
        try {
            ZipFile zf = new ZipFile(f);
            Enumeration<ZipEntry> en = (Enumeration<ZipEntry>) zf.entries();
            HotplugConfig hc = null;
            try {
                while (en.hasMoreElements()) {
                    ZipEntry ze = en.nextElement();
                    String name = ze.getName();
                    if (name.endsWith("/"))
                        continue;
                    if (name.startsWith("hotplug/hotplug.") && name.endsWith(".json")) {
                        String j = new String(Streams.readBytes(zf.getInputStream(ze)));
                        hc = Json.fromJson(HotplugConfig.class, j);
                        hc.put("origin", "file");
                        hc.put("origin_path", f.getAbsolutePath());
                        hc.put("sha1", Lang.sha1(f));
                        hc.put("enable", new File(f.getParentFile(), f.getName() + ".enable").exists());
                        return hc;
                    }
                } 
            }
            finally {
                zf.close();
            }
        }
        catch (Exception e) {
            log.debug("bad hotplug file="+f.getAbsolutePath(), e);
            return null;
        }
        return null;
    }
    
    public boolean add(File f) {
        HotplugConfig hc = checkHotplugFile(f);
        if (hc == null)
            return false;
        String dst = String.format("%s/%s-%s.jar", getLibPath(), hc.getName(), hc.getVersion());
        Files.createFileIfNoExists(new File(dst));
        Files.copy(f, new File(dst));
        return true;
    }
    
    public static Map<String, HotplugConfig> getActiveHotPlug() {
        return new LinkedHashMap<String, HotplugConfig>(_plugins);
    }
    
    public static List<HotplugConfig> getActiveHotPlugList() {
        return new ArrayList<HotplugConfig>(_plugins.values());
    }
    
    public static Hotplug me() {
        return me;
    }
    
    public HotplugClassLoader getHpcl() {
        return hpcl;
    }
}
