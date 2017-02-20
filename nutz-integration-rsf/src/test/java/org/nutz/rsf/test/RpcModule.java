package org.nutz.rsf.test;
import net.hasor.core.Provider;
import net.hasor.rsf.RsfApiBinder;
import org.nutz.integration.hasor.Configuration;
import org.nutz.integration.hasor.NutzRsfModule;
import org.nutz.rsf.test.provider.EchoService;
import org.nutz.rsf.test.provider.EchoServiceImpl;
//
//
@Configuration
public class RpcModule extends NutzRsfModule {
    @Override
    public void loadModule(RsfApiBinder apiBinder) throws Throwable {
        //
        apiBinder.bindType(EchoService.class).toProvider(apiBinder.converToProvider(//
                apiBinder.rsfService(EchoService.class)     // 声明服务接口
                        .to(EchoServiceImpl.class)          // 绑定服务实现类(使用 Hasor bean 容器)
                        .register()                         // 发布服务
        ));
        //
        Provider<EchoService> nutzBean = nutzBean(apiBinder, EchoService.class);
        apiBinder.bindType(EchoService.class).toProvider(apiBinder.converToProvider(//
                apiBinder.rsfService(EchoService.class)     // 声明服务接口
                        .toProvider(nutzBean)               // 绑定服务实现类(使用 nutz Bean 容器)
                        .register()                         // 发布服务
        ));
    }
}