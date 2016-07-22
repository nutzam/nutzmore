# nutzmore Nutz的插件与扩展

[![Build Status](https://travis-ci.org/nutzam/nutzmore.png?branch=master)](https://travis-ci.org/nutzam/nutzmore)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutzmore/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutzmore/)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

### 各种官方插件的集合

每个插件都有自己的文件夹,均为maven module, 请按需获取.

# 快照版地址

https://oss.sonatype.org/content/repositories/snapshots/org/nutz/

```xml
	<repositories>
		<repository>
			<id>ossrh</id>
			<url>https://oss.sonatype.org/content/repositories/snapshots</url>
			<snapshots>
				<enabled>true</enabled>
			</snapshots>
		</repository>
	</repositories>
	<dependencies>
		<dependency>
			<groupId>org.nutz</groupId>
			<artifactId>填nutz插件名</artifactId>
			<version>填版本号-SNAPSHOT</version>
		</dependency>
		<!-- 其他依赖 -->
	</dependencies>
```
