package org.nutz.integration.redisson;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.RedissonRxClient;
import org.redisson.codec.CborJacksonCodec;
import org.redisson.codec.FstCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.codec.KryoCodec;
import org.redisson.codec.LZ4Codec;
import org.redisson.codec.MsgPackJacksonCodec;
import org.redisson.codec.SerializationCodec;
import org.redisson.codec.SmileJacksonCodec;
import org.redisson.codec.SnappyCodec;
import org.redisson.codec.SnappyCodecV2;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.MasterSlaveServersConfig;
import org.redisson.config.ReplicatedServersConfig;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;

@IocBean
public class RedissonBeans {

    public static final String PRE = "redisson.";

    @Inject
    protected PropertiesProxy conf;

    @IocBean(name = "redissonConfig")
    public Config createredissonConfig() throws IOException {
        if (conf.has("redisson.config.fromJson")) {
            String fromJson = conf.get("redisson.config.fromJson");
            if (fromJson.startsWith("classpath:")) {
            	String tmp = fromJson.substring("classpath:".length());
            	URL url = getClass().getClassLoader().getResource(tmp);
            	if (url == null) {
            		throw new RuntimeException("classpath resource " + tmp + " not found");
            	}
            	return Config.fromJSON(url);
            }
            if (fromJson.startsWith("file:")) {
            	String tmp = fromJson.substring("file:".length());
            	File file = new File(tmp);
            	if (!file.exists()) {
            		throw new RuntimeException("file resource " + tmp + " not found");
            	}
            	return Config.fromJSON(file);
            }
            return Config.fromJSON(fromJson);
        }
        if (conf.has("redisson.config.fromYaml")) {
            String fromYaml = conf.get("redisson.config.fromYaml");
            if (fromYaml.startsWith("classpath:")) {
            	String tmp = fromYaml.substring("classpath:".length());
            	URL url = getClass().getClassLoader().getResource(tmp);
            	if (url == null) {
            		throw new RuntimeException("classpath resource " + tmp + " not found");
            	}
            	return Config.fromYAML(url);
            }
            if (fromYaml.startsWith("file:")) {
            	String tmp = fromYaml.substring("file:".length());
            	File file = new File(tmp);
            	if (!file.exists()) {
            		throw new RuntimeException("file resource " + tmp + " not found");
            	}
            	return Config.fromYAML(file);
            }
            return Config.fromYAML(fromYaml);
        }
        Config config = conf.make(Config.class, "redisson.");
        String mode = conf.get("redisson.mode", "single");
        switch (mode) {
        case "single": // 单节点模式
            SingleServerConfig ssc = config.useSingleServer();
            ssc.setAddress(conf.get("redisson.single.address", "redis://127.0.0.1:6379"));
            setupBeanByConf(ssc, PRE + "single.");
            if(Strings.isBlank(conf.get("redisson.single.password"))) {
                ssc.setPassword(null);
            }
            break;
        case "masterslave": // 主从
            MasterSlaveServersConfig mssc = config.useMasterSlaveServers();
            mssc.setMasterAddress(conf.check("redisson.masterslave.masterAddress"));
            mssc.addSlaveAddress(Strings.splitIgnoreBlank(conf.check("redisson.masterslave.slaveAddress")));
            setupBeanByConf(mssc, PRE + "masterslave.");
            if(Strings.isBlank(conf.get("redisson.masterslave.password"))) {
                mssc.setPassword(null);
            }
            break;
        case "cluster": // 集群
            ClusterServersConfig csc = config.useClusterServers();
            List<String> clusterList = conf.getList("redisson.cluster.nodeAddress",",");
            for(String host: clusterList) {
                csc.addNodeAddress(host);
            }
            setupBeanByConf(csc, PRE + "cluster.");
            if(Strings.isBlank(conf.get("redisson.cluster.password"))) {
                csc.setPassword(null);
            }
            break;
        case "replicated": // 副本
            ReplicatedServersConfig rsc = config.useReplicatedServers();
            List<String> replicatedList = conf.getList("redisson.replicated.nodeAddress",",");
            for(String host: replicatedList) {
                rsc.addNodeAddress(host);
            }
            setupBeanByConf(rsc, PRE + "replicated.");
            if(Strings.isBlank(conf.get("redisson.replicated.password"))) {
                rsc.setPassword(null);
            }
            break;
        case "sentinel": // 分片
            SentinelServersConfig ssc2 = config.useSentinelServers();
            List<String> sentinelList = conf.getList("redisson.sentinel.sentinelAddress",",");
            for(String host: sentinelList) {
                ssc2.addSentinelAddress(host);
            }
            setupBeanByConf(ssc2, PRE + "sentinel.");
            if(Strings.isBlank(conf.get("redisson.sentinel.password"))) {
                ssc2.setPassword(null);
            }
            break;
        default:
            throw Lang.noImplement();
        }
        if (conf.has("redisson.codec")) {
            switch (conf.get("redisson.codec")) {
            case "jst":
                config.setCodec(new FstCodec());
                break;
            case "json-jackson":
                config.setCodec(new JsonJacksonCodec());
                break;
            case "msgpack-jackson":
                config.setCodec(new MsgPackJacksonCodec());
                break;
            case "cbor-jackson":
                config.setCodec(new CborJacksonCodec());
                break;
            case "smile-jackson":
                config.setCodec(new SmileJacksonCodec());
                break;
            case "kryo":
                config.setCodec(new KryoCodec());
                break;
            case "lz4":
                config.setCodec(new LZ4Codec());
                break;
            case "jdk":
                config.setCodec(new SerializationCodec());
                break;
            case "snappy":
                config.setCodec(new SnappyCodec());
            case "snappy2":
                config.setCodec(new SnappyCodecV2());
                break;
            // TODO nutz json codec
            default:
                config.setCodec(new FstCodec());
                break;
            }
        }
        return config;
    }

    @IocBean(name = "redissonClient")
    public RedissonClient createRedissonClient(@Inject("refer:redissonConfig") Config redissonConfig) {
        return Redisson.create(redissonConfig);
    }

    @IocBean(name = "redissonRxClient")
    public RedissonRxClient createRedissonRxClient(@Inject("refer:redissonConfig") Config redissonConfig) {
        return Redisson.createRx(redissonConfig);
    }

    @IocBean(name = "redissonReactiveClient")
    public RedissonReactiveClient createRedissonReactive(@Inject("refer:redissonConfig") Config redissonConfig) {
        return Redisson.createReactive(redissonConfig);
    }

    public void setupBeanByConf(Object obj, String prefix) {
        Mirror<? extends Object> mirror = Mirror.me(obj.getClass());
        Field[] fields = mirror.getFields();
        for (Field field : fields) {
            String key = prefix + field.getName();
            if (!conf.containsKey(key))
                continue;
            Class<?> klass = field.getType();
            if (klass.isPrimitive() || String.class.equals(klass)) {
                mirror.setValue(obj, field, conf.get(key));
            }
        }
    }
}
