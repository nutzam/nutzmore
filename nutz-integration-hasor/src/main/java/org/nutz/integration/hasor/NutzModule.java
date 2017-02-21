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
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.Module;
import net.hasor.core.Provider;
import net.hasor.core.info.AbstractBindInfoProviderAdapter;
import org.nutz.ioc.Ioc;

import java.util.List;
/**
 * Nutz 版的 Module ，让开发者可以在 Hasor 的体系内访问到 Nutz 的 IoC 容器
 * @version : 2017年02月20日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class NutzModule implements Module {
    protected <T> Provider<T> nutzBean(ApiBinder apiBinder, final Class<T> beanType) {
        List<BindInfo<Ioc>> list = apiBinder.findBindingRegister(Ioc.class);
        BindInfo<Ioc> iocBindInfo = list.get(0);
        final Ioc ioc = (Ioc) ((AbstractBindInfoProviderAdapter<Ioc>) iocBindInfo).getCustomerProvider().get();
        return new Provider<T>() {
            @Override
            public T get() {
                return ioc.get(beanType);
            }
        };
    }
    protected <T> Provider<T> nutzBean(ApiBinder apiBinder, final Class<T> beanType, final String name) {
        List<BindInfo<Ioc>> list = apiBinder.findBindingRegister(Ioc.class);
        BindInfo<Ioc> iocBindInfo = list.get(0);
        final Ioc ioc = (Ioc) ((AbstractBindInfoProviderAdapter<Ioc>) iocBindInfo).getCustomerProvider().get();
        return new Provider<T>() {
            @Override
            public T get() {
                return ioc.get(beanType, name);
            }
        };
    }
}