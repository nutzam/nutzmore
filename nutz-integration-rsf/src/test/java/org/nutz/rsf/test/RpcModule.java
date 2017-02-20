package org.nutz.rsf.test;
import net.hasor.rsf.RsfApiBinder;
import net.hasor.rsf.RsfModule;
import org.nutz.integration.hasor.Configuration;
import org.nutz.rsf.test.provider.EchoService;
import org.nutz.rsf.test.provider.EchoServiceImpl;
//
//
@Configuration
public class RpcModule extends RsfModule {
    @Override
    public void loadModule(RsfApiBinder apiBinder) throws Throwable {
        //
        apiBinder.bindType(EchoService.class).toProvider(apiBinder.converToProvider(//
                apiBinder.rsfService(EchoService.class)     // 声明服务接口
                        .toInstance(new EchoServiceImpl())  // 绑定服务实现类(使用Hasor Ioc 容器)
                        .register()                         // 发布服务
        ));
        //
    }
}