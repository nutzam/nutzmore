package net.wendal.zbusdemo;

import java.io.IOException;
import java.util.Date;

import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.integration.zbus.ZBusFactory;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Lang;
import org.nutz.lang.random.R;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.zbus.mq.server.MqServer;
import org.zbus.net.http.Message;
import org.zbus.rpc.RpcProcessor;
import org.zbus.rpc.mq.Service;

import net.wendal.zbusdemo.bean.User;

public class MainSetup implements Setup {

    public void init(NutConfig nc) {
        Ioc ioc = nc.getIoc();
        Dao dao = ioc.get(Dao.class);
        Daos.createTablesInPackage(dao, getClass(), false);

        if (0 == dao.count(User.class)) {
            User user = new User();
            user.setName("admin");
            user.setSalt(R.UU32());
            user.setPassword(Lang.digest("SHA-256", user.getSalt() + "123456"));
            dao.insert(user);
        }
        PropertiesProxy conf = ioc.get(PropertiesProxy.class, "conf");

        //=========================================================================
        // zbus 的相关代码
        

        ZBusFactory zbus = ioc.get(ZBusFactory.class, "zbus");
        
        // 启动内置zbus服务器,通常不需要!!! 尤其是新版zbus支持jvm模式后, 这个选项很少使用了.
        if (conf.getBoolean("zbus.server.embed.enable", false)) {
            ioc.get(MqServer.class);
        }
        
        // 启动RPC服务端, 若不需要,无需调用
        if (conf.getBoolean("zbus.rpc.service.enable", false)) {
            RpcProcessor rpcProcessor = ioc.get(RpcProcessor.class);
            // 通过buildServices扫描所有标准了@ZBusService的类
            ZBusFactory.buildServices(rpcProcessor, ioc, getClass().getPackage().getName());
            ioc.get(Service.class, "rpcService"); // 注意, Service与服务器连接是异步操作
        }
        // 启动 生产者/消费者(即MQ服务), 若不需要切勿调用.
        zbus.init(getClass().getPackage().getName());

        // 演示主动发送一条mq信息, UserService会收到该消息
        try {
            zbus.getProducer("user").sendAsync(new Message("hi >> "+ new Date()));
        }
        catch (IOException e) {}
        
        //====================================================================
    }

    public void destroy(NutConfig nc) {}

}
