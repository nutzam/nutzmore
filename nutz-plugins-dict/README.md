# nutz-plugins-dict 全局字典生成插件

简介(可用性:测试,维护者:邓华锋(http://dhf.ink))
==================================

生成全局字典文件和map供前段或视图里调用


开发目的
----------------------------------------------
后台开发中经常牵涉到一些类型字典，例如（用户表的类型字段，分普通会员、高级会员等），如果用常规的常量标识不可取，没法统一，也没法很好的标识意义，推荐用枚举。
  而前段展示类型字段值的字面意思时，通常是生拉硬写，各种if判断来展示不同的字面意思，不好维护，改动一处，要改动多处。 
  而此插件的开发目的是解决这些问题，让通过后台枚举配置注解的方式，生成可配置格式的前段json字典串，及全局的字典map，供前段调用等。


使用示例
----------------------------------------------
```Java
Selects.custom().addProcessorFirst(new JqgridSelectProcessor()).addProcessorLast(new EditableSelectProcessor()).setPackages("org.nutz.plugins.dict").setJsonFilePath("e:/dict").build();
```