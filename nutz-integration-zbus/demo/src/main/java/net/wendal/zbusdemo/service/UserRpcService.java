package net.wendal.zbusdemo.service;

import org.nutz.integration.zbus.annotation.ZBusInvoker;

@ZBusInvoker
public interface UserRpcService {

    String sayhi(String name);
}
