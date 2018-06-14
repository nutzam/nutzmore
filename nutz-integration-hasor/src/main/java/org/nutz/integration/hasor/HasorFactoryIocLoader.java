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

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;

import org.nutz.integration.hasor.annotation.HasorConfiguration;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.map.MapLoader;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.Hasor;
import net.hasor.core.LifeModule;
import net.hasor.core.Module;
/**
 * 用法：ioc.get(HasorFactoryIocLoader.class, "hasor");
 * @version : 2017年02月20日
 * @author 赵永春(zyc@hasor.net)
 */
public class HasorFactoryIocLoader extends MapLoader implements LifeModule {
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~IocLoader
    private static final Log log = Logs.get();
    private PropertiesProxy conf;
    public HasorFactoryIocLoader(PropertiesProxy conf) {
        this.conf = conf;
    }
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Create Hasor
    //
    protected Ioc        ioc;
    protected AppContext appContext;
    public void init() throws IOException {
        //
        // .创建 Hasor 对象，后面用来创建 AppContext
        Hasor creter = Hasor.create(null);
        //
        // .从插件参数中加载环境变量
        String mainConfig = conf.get("hasor.config");
        if (!isBlank(mainConfig)) {
            creter.setMainSettings(mainConfig);
        }
        //
        // .环境变量设置到 Hasor 中
        for (Entry<String, String> en : conf.entrySet()) {
            String key = en.getKey();
            if (!key.startsWith("hasor.")) {
                continue;
            }
            key = key.substring("hasor.".length());
            if ("config".equals(key)) {
                continue;
            }
            creter.putData(key, en.getValue());
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
    private boolean isBlank(String mainConfig) {
        return mainConfig == null || mainConfig.trim().equalsIgnoreCase("");
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
        // .扫描所有配置了 @HasorConfiguration 注解的类，并将 Hasor 的 Module 加载进来
        //   -- 包扫描范围通过 Nutz 配置文件以环境变量方式传递进来。
        Set<Class<?>> aClass = apiBinder.findClass(HasorConfiguration.class);
        for (final Class<?> klass : aClass) {
            if (klass == HasorConfiguration.class) {
                continue;
            }
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
            if (isBlank(bindName)) {
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
