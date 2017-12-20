nutz-plugins-mongodb
=========

MongoDB 驱动的薄封装

# 加入到项目中

发布版

```xml
	<dependencies>
		<dependency>
			<groupId>org.nutz</groupId>
			<artifactId>nutz-plugins-mongodb</artifactId>
			<version>1.r.63.r3</version>
		</dependency>
		<!-- 其他依赖 -->
	</dependencies>
```

快照版本在每次提交后会自动deploy到sonatype快照库,享受各种bug fix和新功能

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
			<groupId>org.nutz</groupId>
			<artifactId>nutzmongo</artifactId>
			<version>1.r.65-SNAPSHOT</version>
		</dependency>
		<!-- 其他依赖 -->
	</dependencies>
```

也可以将repositories配置放入$HOME/.m2/settings.xml中

或者直接去[快照库下载](https://jfrog.nutz.cn/artifactory/snapshots/org/nutz/nutzmongo/1.r.65-SNAPSHOT/)

# 声明Ioc Bean

```js
	var ioc={
		zMongo : {
			args : ["127.0.0.1", 27017], // 或者不写参数，默认就是127.0.0.1和27017
			factory : "org.nutz.mongo.ZMongo#me"
		},
		//zMongo : {
		//	args : ["mongodb://root:mypass@192.168.2.199:3717,192.168.2.200:3717/admin?replicaSet=mgset"], // 基于Mongo URI创建ZMongo
		//	factory : "org.nutz.mongo.ZMongo#uri"
		//},
		zMoDB : {
			args : ["nutzbook"], // 数据库名称
			factory : "$zMongo#db"
		},
		zMoCoUser : {
			args : ["user"],
			factory : "$zMoDB#c"
		}
		/* // 还可以声明几个常用的集合,也可以在Service中生成
		,zMoCoTopic : {
			args : ["topic", false],
			factory : "$zMoDB#cc"
		},
		zMoCoReply : {
			args : ["reply", false],
			factory : "$zMoDB#cc"
		}
		*/
	}
```

# Service中的注入和使用

```java
	@IocBean
	public class XXXService {
		// 按需注入几个核心对象
		@Inject 
		protected ZMongo zmongo;  //注意大小写与配置的名字一致
		@Inject
		protected ZMoDB zMoDB; // 当前数据
		
		@Inject ZMoCo zMoCoUser; // 按js里面的配置取
		
		@Inject("java:$zMoDB.c('role')") // 也可以直接取. 当然了,也可以代码调用zMoDB.c(集合名词)来动态获取
		ZMoCo zMoCoRole;
		
		public void insert(User...users) {
			zMoCoUser.insert(ZMo.toDocArray(users));
		}
		
		public List<User> query(ZMoDoc cnd) {
			List<User> list = new ArrayList<User>();
			DBCursor cursor = MoCoUser.find(cnd);
			if(cursor.hasNext()) {
       			DBObject obj = cursor.next();
       			list.add(ZMo.me().fromDocToObj(obj, User.class));
    		}
    		return list;
		}
		
		// ZMoCoUser还有很多方法哦，请挖掘
	}
```