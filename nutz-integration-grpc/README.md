nutz-integration-grpc
==================================

简介(可用性:开发中,维护者:wendal)
==================================

封装grpc

功能介绍
==================================

1. 动态服务注册, 扩展grpc原有的MutableHandlerRegistry实现更方便的动态增减Service
2. 代码生成, 根据Java接口类,生成proto文件/服务端代理类/客户端代理类

完成情况
==================================

- [X] 动态注册
- [X] proto文件生成
- [X] 服务端代理类生成
- [X] 客户端代理类生成
- [X] 支持普通类型(String,int,long,float,double,boolean)
- [ ] 支持枚举
- [ ] 支持map/list
- [ ] 支持复杂的自定义对象
- [ ] 基于etcd的服务发现

使用实例
==================================

Java接口,方法名需要唯一,不可重复

```java
public interface SimpleGrpcService {
    void ping(); // 无参数无返回值
    String pong(); // 无参数,有返回值
    void hello(String name); // 有参数,无返回值
    String incr(String key); // 有参数,有返回值
    int simple(String name, int age, float x, double y, boolean flag); // 各种基本数据类型
}
```

生成代码,当前仅生成字符串,需要手工创建对应的文件

```java
    @Test
    public void testMake() {
        GrpcProtoMaker maker = new GrpcProtoMaker();
        maker.klass = SimpleGrpcService.class;
        maker.make();
        System.out.println("-====================");
        System.out.println(maker.proto); // simple.proto的内容
        System.out.println("-====================");
        System.out.println(maker.serverProxy); // 服务端代理类的java源码
        System.out.println("-====================");
        System.out.println(maker.clientProxy); // 客户端代理类的java源码
        System.out.println("-====================");
    }
```

服务器端的启动

```java
// 获取SimpleGrpcService的真实实现类的实例,这是你自己写的类!!
SimpleGrpcService simpleService = new SimpleGrpcServiceImpl(); // 或者从ioc取出

// 创建grpc代理实例
SimpleGrpcServiceGrpcServerProxy simple = new SimpleGrpcServiceGrpcServerProxy();
simple.proxy = simpleService;

// 启动grpc服务, 端口50015
ServerBuilder.forPort(50015).addService(simple).build().start();
```

客户端的访问

```java
ManagedChannel channel = ManagedChannelBuilder.forAddress("127.0.0.1", 50051).usePlaintext(true).build();
SimpleGrpcServiceGrpcClientProxy proxy = new SimpleGrpcServiceGrpcClientProxy();
proxy.setChannel(channel);
        
// SimpleGrpcServiceGrpcClientProxy 实现了SimpleGrpcService接口哦
SimpleGrpcService simple = proxy;
        
simple.hello("spring");
System.out.println(simple.incr("nutz"));

// 嗯,再见
channel.shutdown();
channel.awaitTermination(5, TimeUnit.SECONDS);
```