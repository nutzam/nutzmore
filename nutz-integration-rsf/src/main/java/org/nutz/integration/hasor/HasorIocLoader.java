/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nutz.integration.hasor;
import net.hasor.core.*;
import net.hasor.core.utils.IOUtils;
import net.hasor.core.utils.ResourcesUtils;
import net.hasor.core.utils.StringUtils;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.ioc.meta.IocObject;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Set;
/**
 * 用法：
 *  <code>@IocBy(args={"*hasor"})</code>
 *  <code>@IocBy(args={"*hasor", "......."})</code>
 * @version : 2017年02月20日
 * @author 赵永春(zyc@hasor.net)
 */
public class HasorIocLoader extends JsonLoader implements LifeModule {
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~IocLoader
    private static final Log    log           = Logs.get();
    public static final  String NUTZ_HASOR_JS = "org/nutz/integration/hasor/hasor.js";
    private final String[] args;
    public HasorIocLoader() {
        super(NUTZ_HASOR_JS);
        this.args = new String[0];
    }
    public HasorIocLoader(String[] args) {
        super(NUTZ_HASOR_JS);
        this.args = args;
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
    protected Ioc        ioc;
    protected AppContext appContext;
    //
    private void loadProperties(String arg, Properties properties) throws IOException {
        InputStream envIn = ResourcesUtils.getResourceAsStream(arg);
        if (envIn == null)
            return;
        try {
            properties.load(new InputStreamReader(envIn, Settings.DefaultCharset));
        } finally {
            IOUtils.closeQuietly(envIn);
        }
    }
    public void init() throws IOException {
        //
        // .创建 Hasor 对象，后面用来创建 AppContext
        Hasor creter = Hasor.create(null);
        //
        // .从插件参数中加载环境变量
        String mainConfig = null;
        Properties properties = new Properties();
        if (this.args != null && this.args.length > 0) {
            for (String arg : this.args) {
                if (StringUtils.isBlank(arg))
                    continue;
                if (arg.toLowerCase().endsWith(".xml"))
                    mainConfig = arg;                   // - 可能是主配置文件
                else
                    loadProperties(arg, properties);    // - 属性文件
                //
            }
        }
        //
        // .确定主配置文件
        if (StringUtils.isBlank(mainConfig))
            mainConfig = properties.getProperty("hasor.config");
        properties.remove("hasor.config");
        if (!StringUtils.isBlank(mainConfig)) {
            creter.setMainSettings(mainConfig);
        }
        //
        // .环境变量设置到 Hasor 中
        if (!properties.isEmpty()) {
            for (String key : properties.stringPropertyNames()) {
                creter.putData(key, properties.getProperty(key));
            }
        }
        //
        // .创建 AppContext上下文
        try {
            this.appContext = creter.build(this);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
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
    // .Hasor启动（导出Hasor中的Bean）
    @Override
    public void onStart(AppContext appContext) throws Throwable {
        //
        String[] bindIDs = appContext.getBindIDs();
        for (String bindID : bindIDs) {
            BindInfo<?> bindInfo = appContext.getBindInfo(bindID);
            String bindName = bindInfo.getBindName();
            Class<?> bindType = bindInfo.getBindType();
            //
            if (StringUtils.isBlank(bindName)) {
                bindName = Strings.lowerFirst(bindType.getSimpleName());
            }
            //
            if (bindID.split("-").length == 5) {
                if (bindName.split("-").length == 5) {
                    continue;
                }
            }
            //
            if ("appContext".equals(bindName) || Ioc.class == bindInfo.getBindType()) {
                continue;
            }
            //
            log.debugf("define hasor bindID=%s ,name=%s ,type=%s",//
                    bindInfo.getBindID(), bindName, bindType.getName());
            NutMap _map = new NutMap().setv("factory", "$appContext#getInstance");
            _map.setv("args", new String[] { bindID });
            _map.setv("type", bindType.getName());
            map.put(bindName, _map);
        }
    }
    // .Hasor销毁
    @Override
    public void onStop(AppContext appContext) throws Throwable {
        //
    }
}