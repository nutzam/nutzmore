nutz-plugins-redisson
==================================

## 如何使用

1. 在@IocBy中添加 "*redisson"
2. 按配置项选择合适的配置, 填写服务器信息
3. 使用`@Inject redissonClient redissonClient;`获取实例

## 配置项


single(单节点)模式

```
redisson.mode=single
redisson.single.address=redis://127.0.0.1:6379
redisson.password=123456
```

masterslave(主从)模式

```
redisson.mode=masterslave
redisson.masterslave.masterAddress=redis://127.0.0.1:6379
redisson.masterslave.slaveAddress=redis://127.0.0.1:6380,redis://127.0.0.1:6381
```

cluster(集群)模式

```
redisson.mode=cluster
redisson.cluster.nodeAddress=redis://127.0.0.1:6379,redis://127.0.0.1:6380,redis://127.0.0.1:6381
```

replicated(副本)模式

```
redisson.mode=replicated
redisson.replicated.nodeAddress=redis://127.0.0.1:6379,redis://127.0.0.1:6380,redis://127.0.0.1:6381
```

sentinel(分片)模式

```
redisson.mode=replicated
redisson.sentinel.sentinelAddress=redis://127.0.0.1:6379,redis://127.0.0.1:6380,redis://127.0.0.1:6381
```

基于JSON配置

```
redisson.config.fromJson:
{
	...
}
#end
```

基于YAML配置

```
redisson.config.fromYaml:
xxx:
	xxx
#end
```

通用配置,非json/yaml时可用, 详情查阅org.redisson.config.Config,通过属性名称配置即可

```
# 线程数,默认16
redisson.threads=16 
# netty线程数,默认32
redisson.nettyThreads=32
```

编码器配置

```
# 可选值有 jason,fst,kryo,lz4,jdk,snappy,snappy2,默认值fst
redisson.codec=fst
```