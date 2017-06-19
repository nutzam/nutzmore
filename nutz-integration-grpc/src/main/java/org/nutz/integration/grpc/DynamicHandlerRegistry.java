package org.nutz.integration.grpc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import io.grpc.BindableService;
import io.grpc.ExperimentalApi;
import io.grpc.HandlerRegistry;
import io.grpc.MethodDescriptor;
import io.grpc.ServerMethodDefinition;
import io.grpc.ServerServiceDefinition;

/**
 * 以MutableHandlerRegistry为原型改造的版本, 添加几个方法, 例如根据名称直接移除服务.
 * @author wendal
 *
 */
@ThreadSafe
public final class DynamicHandlerRegistry extends HandlerRegistry {
    
    protected ConcurrentHashMap<String, ServerServiceDefinition> services = new ConcurrentHashMap<String, ServerServiceDefinition>();

    /**
     * Registers a service.
     *
     * @return the previously registered service with the same service
     *         descriptor name if exists, otherwise {@code null}.
     */
    @Nullable
    public ServerServiceDefinition addService(ServerServiceDefinition service) {
        return services.put(service.getServiceDescriptor().getName(), service);
    }

    /**
     * Registers a service.
     *
     * @return the previously registered service with the same service
     *         descriptor name if exists, otherwise {@code null}.
     */
    @Nullable
    public ServerServiceDefinition addService(BindableService bindableService) {
        return addService(bindableService.bindService());
    }

    public boolean removeService(ServerServiceDefinition service) {
        return services.remove(service.getServiceDescriptor().getName(), service);
    }
    
    public boolean removeService(String name) {
        return services.remove(name) != null;
    }

    /**
     * Note: This does not necessarily return a consistent view of the map.
     */
    @Override
    @ExperimentalApi("https://github.com/grpc/grpc-java/issues/2222")
    public List<ServerServiceDefinition> getServices() {
        return new ArrayList<ServerServiceDefinition>(services.values());
    }

    /**
     * Note: This does not actually honor the authority provided. It will,
     * eventually in the future.
     */
    @Override
    @Nullable
    public ServerMethodDefinition<?, ?> lookupMethod(String methodName,
                                                     @Nullable String authority) {
        String serviceName = MethodDescriptor.extractFullServiceName(methodName);
        if (serviceName == null) {
            return null;
        }
        ServerServiceDefinition service = services.get(serviceName);
        if (service == null) {
            return null;
        }
        return service.getMethod(methodName);
    }
    
    public List<String> getNames() {
        return Collections.list(services.keys());
    }
    
    public ServerServiceDefinition getService(String name) {
        return services.get(name);
    }
}
