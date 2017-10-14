nutz-plugins-jedisque
==================================

简介(可用性:试用,维护者:wendal)
==================================

简单集成Disque

Disque简介
==================================
Disque是Redis之父Salvatore Sanfilippo新开源的一个分布式内存消息代理。它适应于“Redis作为作业队列”的场景，但采用了一种专用、独立、可扩展且具有容错功能的设计，兼具Redis的简洁和高性能，并且用C语言实现为一个非阻塞网络服务器。有一点需要提请读者注意，在Disque项目文档及本文中，“消息（Message）”和“作业（Job）”可互换。

Disque是一个独立于Redis的新项目，但它重用了Redis网络源代码、节点消息总线、库和客户端协议的一大部分。由于Disque使用了与Redis相同的协议，所以可以直接使用Redis客户端连接Disque集群，只是需要注意，Disque的默认端口是7711，而不是6379。

作为消息代理，Disque充当了需要进行消息交换的进程之间的一个中间层，生产者向其中添加供消费者使用的消息。这种生产者-消费者队列模型非常常见，其主要不同体现在一些细节方面：

    Disque是一个同步复制作业队列，在默认情况下，新增任务会复制到W个节点上，W-1个节点发生故障也不会影响消息的传递。
    Disque支持至少一次和至多一次传递语义，前者是设计和实现重点，而后者可以通过将重试时间设为0来实现。每个消息的传递语义都是单独设置的，因此，在同一个消息队列中，语义不同的消息可以共存。
    按照设计，Disque的至少一次传递是近似一次传递，它会尽力避免消息的多次传递。
    Disque集群的所有节点都有同样的角色，也就是“多主节点（multi-master）”。生产者和消费者可以连接到不同的队列或节点，节点会根据负载和客户端请求自动交换消息。
    Disque支持可选的异步命令。在这种模式下，生产者在向一个复制因子不为1的队列中添加一个作业后，可以不必等待复制完成就可以转而执行其它操作，节点会在后台完成复制。
    在超过指定的消息重试时间后，Disque会自动将未收到响应的消息重新放入队列。
    在Disque中，消费者使用显式应答来标识消息已经传递完成。
    Disque只提供尽力而为排序。队列根据消息创建时间对消息进行排序，而创建时间是通过本地节点的时钟获取的。因此，在同一个节点上创建的消息通常是按创建顺序传递的，但Disque并不提供严格的FIFO语义保证。比如，在消息重新排队或者因为负载均衡而移至其它节点时，消息的传递顺序就无法保证了。所以，Salvatore指出，从技术上讲，Disque严格来说并不是一个队列，而更应该称为消息代理。
    Disque通过四个参数提供了细粒度的作业控制，分别是复制因子（指定消息的副本数）、延迟时间（将消息放入队列前的最小等待时间）、重试时间（设置消息何时重新排队）、过期时间（设置何时删除消息）。

需要注意的是，Disque项目尚处于RC1测试阶段，代码和算法未经充分测试，还不适合用于生产环境。在接下来的几个月里，其实现和API很可能会发生重大变化。此外，它还有如下限制：

其中还包含许多没有用到的Redis代码；

    它并非源于Salvatore的项目需求，而是源于他看到人们将Redis用作队列，但他不是这方面的专家；
    同Redis一样，它是单线程的，但鉴于它所操作的数据结构并不复杂，将来可以考虑改为多线程；
    Disque进程中的作业数量受可用内存限制；
    Disque没有进行性能优化。

背景
==================================

这个插件的代码,是因为@howechiang要用 哈哈哈

本插件包含几个核心类
==================================

* JedisqueAgent -- 它封装了Jedisque

使用方法
-------------------------

本插件提供了ioc加载器(加载源码中的jedisque.js),配置方式主要走properties文件

### 在IocBy中引用本插件

```java
@IocBy(args={
	"*js", "ioc/",
	"*anno", "net.wendal.nutzbook",
	"jedisque*" // 是的,并没有什么参数
	})
```


### 使用DisqueService操作

DisqueService类继承Jedisque类

```java
@Inject DisqueService disqueService;

public void addJob(String queueName, String job, long mstimeout) {
	disqueService.addJob(queueName, job, mstimeout);
}
```

### 注入JedisAgent

```java
@Inject JedisqueAgent jedisqueAgent;

public void addJob(String queueName, String job, long mstimeout) {
    try (Jedisque jedisque = jedisqueAgent.build()) { 
		jedisque.addJob(queueName, job, mstimeout);
	}
}
```


配置方式
-----------------------------

### 与其他插件类似, 本插件从conf读取disque开头的参数

基本配置

```
disque.uris=disque://{host1}:{port},disque://{host2}:{port},disque://{host3}:{port}
```
略坑不能设密码

Maven
-----------------------------
Jedisque的作者很坑啊, 依赖的jedis只能2.7.2, 关键2.7.3以上版本不向下兼容, 只能fork重新改了, 把jedis2.7.2改名字打入jedisque快照版里

```xml
<repositories>
		<repository>
			<id>nutzcn-snapshots</id>
			<url>https://jfrog.nutz.cn/artifactory/snapshots</url>
			<snapshots>
				<enabled>true</enabled>
			</snapshots>
		</repository>
	</repositories>
	<dependencies>
		<dependency>
            <groupId>com.github.xetorthio</groupId>
            <artifactId>jedisque</artifactId>
            <version>0.0.5-SNAPSHOT</version>
        </dependency>
    </dependencies>
</repositories>
```
