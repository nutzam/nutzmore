# nutz-plugins-multiview版本历史

### versions 1.68  released

Feature - **添加ThymeleafView视图** 请阅读 [文档](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-multiview#session%E5%92%8Capplication%E5%88%87%E6%8D%A2%E8%A7%86%E5%9B%BE%E7%A4%BA%E4%BE%8B) 了解更多详情

Feature - **添加CaptchaView视图** 请阅读 [文档](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-multiview#%E6%8F%92%E4%BB%B6%E7%9A%84%E6%A0%B8%E5%BF%83%E7%B1%BB) 了解更多详情

Fix-配置资源路径为空时，报空指针异常

Improvement - **每个视图都可配置其单独的视图配置文件，最终会合并到全局Config里去，页面调用cfg变量** 

Improvement -默认视图可以不配置，默认找第一个配置的视图模板引擎

### versions 1.66  released

Feature - **session和application级别切换模板路径、后缀及引擎功能添加** 请阅读 [文档](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-multiview#session%E5%92%8Capplication%E5%88%87%E6%8D%A2%E8%A7%86%E5%9B%BE%E7%A4%BA%E4%BE%8B) 了解更多详情

Feature - **MultiViewResover获取方式的灵活性** 请阅读 [文档](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-multiview#%E6%8F%92%E4%BB%B6%E7%9A%84%E6%A0%B8%E5%BF%83%E7%B1%BB) 了解更多详情

Improvement - 代码重构，去除约定的获取MultiViewResover和conf的方式

### versions 1.65 released

Feature - **默认视图设置添加** 请阅读 [文档](https://github.com/nutzam/nutzmore/blob/master/nutz-plugins-multiview/README.md) 了解更多详情

Feature - **配置视图扩展属性添加** 请阅读 [文档](https://github.com/nutzam/nutzmore/blob/master/nutz-plugins-multiview/README.md) 了解更多详情

Feature - **全局属性文件的可配置功能添加** 请阅读 [文档](https://github.com/nutzam/nutzmore/blob/master/nutz-plugins-multiview/README.md) 了解更多详情

### versions 1.65 ago released

Feature - **默认全局配置文件变量cfg添加** 请阅读 [文档](https://github.com/nutzam/nutzmore/blob/master/nutz-plugins-multiview/README.md) 了解更多详情

Feature - **直接调用视图方式添加** 请阅读 [文档](https://github.com/nutzam/nutzmore/blob/master/nutz-plugins-multiview/README.md) 了解更多详情

Improvement - 代码重构，去除了AbstractUrlBasedView.java和ViewResolver
