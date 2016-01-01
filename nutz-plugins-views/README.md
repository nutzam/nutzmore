# nutzmore Nutz的插件与扩展

### 包含freemarker、velocity 试图插件


freemarker默认请在MainModule中增加 "*org.nutz.plugins.view.freemarker.FreemarkerIocLoader"
比加载器用户加载freemarker 需要的配置文件 如需自定义模板方法 请自己复制一份freemarker.js 到/ioc 目录下 

已添加的标签函数 currentTime 使用方法 请在需要显示的地方自行填加标签  <@currentTime /> 更多使用方法请参考官方nutzbook中的使用配置