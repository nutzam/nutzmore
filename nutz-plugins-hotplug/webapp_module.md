# webapp模块的必备设置

webapp模块的核心配置
=============================================

用于启动web服务的项目,它不需要遵循插件规范,因为是它启动整个插件体系

注意, 若根package是XXX.YYY,那么webapp的package应该是XXX.YYY.webapp或XXX.YYY.web


### MainModule标注以下注解

```java
@LoadingBy(HotPlug.class)   // 改变加载行为
@Modules(scanPackage=false) // 禁用自动扫描
@SetupBy(MainSetup.class) // 加载下一个小节的MainSetup类
```

### MainSetup类带下面的代码

```java
public void init(NutConfig nc) {
	// 初始化插件系统
	nc.getIoc().get(Hotplug.class).setupInit();
}

public void destroy(NutConfig nc) {
	// 销毁插件系统
	nc.getIoc().get(Hotplug.class).setupDestroy();
}
```