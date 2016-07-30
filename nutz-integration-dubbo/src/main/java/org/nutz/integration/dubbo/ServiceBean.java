package org.nutz.integration.dubbo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.meta.IocObject;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ModuleConfig;
import com.alibaba.dubbo.config.MonitorConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.alibaba.dubbo.config.annotation.Service;

@SuppressWarnings("serial")
public class ServiceBean<T> extends ServiceConfig<T> {
    
    protected Ioc ioc;
    
    protected Map<String, IocObject> iobjs;
    
    protected String beanName;
    
    public ServiceBean() {}

    public ServiceBean(Service service) {
        super(service);
    }



    @SuppressWarnings("deprecation")
    public void _init() {
        if (getProvider() == null) {
            Map<String, ProviderConfig> providerConfigMap = DubboAgent.getByType(ioc, iobjs, ProviderConfig.class);
            if (providerConfigMap != null && providerConfigMap.size() > 0) {
                Map<String, ProtocolConfig> protocolConfigMap = DubboAgent.getByType(ioc, iobjs, ProtocolConfig.class);
                if ((protocolConfigMap == null || protocolConfigMap.size() == 0)
                        && providerConfigMap.size() > 1) { // 兼容旧版本
                    List<ProviderConfig> providerConfigs = new ArrayList<ProviderConfig>();
                    for (ProviderConfig config : providerConfigMap.values()) {
                        if (config.isDefault() != null && config.isDefault().booleanValue()) {
                            providerConfigs.add(config);
                        }
                    }
                    if (providerConfigs.size() > 0) {
                        setProviders(providerConfigs);
                    }
                } else {
                    ProviderConfig providerConfig = null;
                    for (ProviderConfig config : providerConfigMap.values()) {
                        if (config.isDefault() == null || config.isDefault().booleanValue()) {
                            if (providerConfig != null) {
                                throw new IllegalStateException("Duplicate provider configs: " + providerConfig + " and " + config);
                            }
                            providerConfig = config;
                        }
                    }
                    if (providerConfig != null) {
                        setProvider(providerConfig);
                    }
                }
            }
        }
        if (getApplication() == null
                && (getProvider() == null || getProvider().getApplication() == null)) {
            Map<String, ApplicationConfig> applicationConfigMap = DubboAgent.getByType(ioc, iobjs, ApplicationConfig.class);
            if (applicationConfigMap != null && applicationConfigMap.size() > 0) {
                ApplicationConfig applicationConfig = null;
                for (ApplicationConfig config : applicationConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault().booleanValue()) {
                        if (applicationConfig != null) {
                            throw new IllegalStateException("Duplicate application configs: " + applicationConfig + " and " + config);
                        }
                        applicationConfig = config;
                    }
                }
                if (applicationConfig != null) {
                    setApplication(applicationConfig);
                }
            }
        }
        if (getModule() == null
                && (getProvider() == null || getProvider().getModule() == null)) {
            Map<String, ModuleConfig> moduleConfigMap = DubboAgent.getByType(ioc, iobjs, ModuleConfig.class);
            if (moduleConfigMap != null && moduleConfigMap.size() > 0) {
                ModuleConfig moduleConfig = null;
                for (ModuleConfig config : moduleConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault().booleanValue()) {
                        if (moduleConfig != null) {
                            throw new IllegalStateException("Duplicate module configs: " + moduleConfig + " and " + config);
                        }
                        moduleConfig = config;
                    }
                }
                if (moduleConfig != null) {
                    setModule(moduleConfig);
                }
            }
        }
        if ((getRegistries() == null || getRegistries().size() == 0)
                && (getProvider() == null || getProvider().getRegistries() == null || getProvider().getRegistries().size() == 0)
                && (getApplication() == null || getApplication().getRegistries() == null || getApplication().getRegistries().size() == 0)) {
            Map<String, RegistryConfig> registryConfigMap = DubboAgent.getByType(ioc, iobjs, RegistryConfig.class);
            if (registryConfigMap != null && registryConfigMap.size() > 0) {
                List<RegistryConfig> registryConfigs = new ArrayList<RegistryConfig>();
                for (RegistryConfig config : registryConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault().booleanValue()) {
                        registryConfigs.add(config);
                    }
                }
                if (registryConfigs != null && registryConfigs.size() > 0) {
                    super.setRegistries(registryConfigs);
                }
            }
        }
        if (getMonitor() == null
                && (getProvider() == null || getProvider().getMonitor() == null)
                && (getApplication() == null || getApplication().getMonitor() == null)) {
            Map<String, MonitorConfig> monitorConfigMap = DubboAgent.getByType(ioc, iobjs, MonitorConfig.class);
            if (monitorConfigMap != null && monitorConfigMap.size() > 0) {
                MonitorConfig monitorConfig = null;
                for (MonitorConfig config : monitorConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault().booleanValue()) {
                        if (monitorConfig != null) {
                            throw new IllegalStateException("Duplicate monitor configs: " + monitorConfig + " and " + config);
                        }
                        monitorConfig = config;
                    }
                }
                if (monitorConfig != null) {
                    setMonitor(monitorConfig);
                }
            }
        }
        if ((getProtocols() == null || getProtocols().size() == 0)
                && (getProvider() == null || getProvider().getProtocols() == null || getProvider().getProtocols().size() == 0)) {
            Map<String, ProtocolConfig> protocolConfigMap = DubboAgent.getByType(ioc, iobjs, ProtocolConfig.class);
            if (protocolConfigMap != null && protocolConfigMap.size() > 0) {
                List<ProtocolConfig> protocolConfigs = new ArrayList<ProtocolConfig>();
                for (ProtocolConfig config : protocolConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault().booleanValue()) {
                        protocolConfigs.add(config);
                    }
                }
                if (protocolConfigs != null && protocolConfigs.size() > 0) {
                    super.setProtocols(protocolConfigs);
                }
            }
        }
        if (getPath() == null || getPath().length() == 0) {
            if (beanName != null && beanName.length() > 0 
                    && getInterface() != null && getInterface().length() > 0
                    && beanName.startsWith(getInterface())) {
                setPath(beanName);
            }
        }
        //if (! isDelay()) {
            export();
        //}
    }
    
    public void depose() {
        this.unexport();
    }
}
