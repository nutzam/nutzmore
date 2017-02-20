package org.nutz.rsf.test.provider;
import net.hasor.rsf.RsfApiBinder;
import net.hasor.rsf.RsfModule;
import org.nutz.integration.hasor.Configuration;
//
//
@Configuration
public class RpcModule extends RsfModule {
    @Override
    public void loadModule(RsfApiBinder apiBinder) throws Throwable {
        apiBinder.rsfService(EchoService.class).to(EchoServiceImpl.class).register();
    }
}