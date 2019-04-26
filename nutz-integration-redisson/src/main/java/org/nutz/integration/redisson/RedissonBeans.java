package org.nutz.integration.redisson;

import java.io.IOException;
import java.lang.reflect.Field;

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
import org.redisson.codec.FstCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.codec.KryoCodec;
import org.redisson.codec.LZ4Codec;
import org.redisson.codec.SerializationCodec;
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
            return Config.fromJSON(fromJson);
        }
        if (conf.has("redisson.config.fromYaml")) {
            String fromYaml = conf.get("redisson.config.fromYaml");
            return Config.fromYAML(fromYaml);
        }
        Config config = conf.make(Config.class, "redisson.");
        String mode = conf.get("redisson.mode", "single");
        switch (mode) {
        case "single": // 单节点模式
            SingleServerConfig ssc = config.useSingleServer();
            ssc.setAddress(conf.check("redisson.single.address"));
            setupBeanByConf(ssc, PRE + "single.");
            break;
        case "masterslave": // 主从
            MasterSlaveServersConfig mssc = config.useMasterSlaveServers();
            mssc.setMasterAddress(conf.check("redisson.masterslave.masterAddress"));
            mssc.addSlaveAddress(Strings.splitIgnoreBlank(conf.check("redisson.masterslave.slaveAddress")));
            setupBeanByConf(mssc, PRE + "masterslave.");
            break;
        case "cluster": // 集群
            ClusterServersConfig csc = config.useClusterServers();
            csc.addNodeAddress(conf.check("redisson.cluster.nodeAddress"));
            setupBeanByConf(csc, PRE + "cluster.");
            break;
        case "replicated": // 副本
            ReplicatedServersConfig rsc = config.useReplicatedServers();
            rsc.addNodeAddress(conf.check("redisson.replicated.nodeAddress"));
            setupBeanByConf(rsc, PRE + "replicated.");
            break;
        case "sentinel": // 分片
            SentinelServersConfig ssc2 = config.useSentinelServers();
            ssc2.addSentinelAddress(conf.check("redisson.sentinel.sentinelAddress"));
            setupBeanByConf(ssc2, PRE + "sentinel.");
            break;
        default:
            throw Lang.noImplement();
        }
        if (conf.has("redisson.codec")) {
            switch (conf.get("redisson.codec")) {
            case "jst":
                config.setCodec(new FstCodec());
                break;
            case "jason":
                config.setCodec(new JsonJacksonCodec());
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
