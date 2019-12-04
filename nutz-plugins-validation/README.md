nutz-plugins-validation 数据校验
==================================

简介(可用性:试用,维护者:wendal)
==================================

独立,小巧且够用的校验库

用法 maven 引入最新的库

```
        <dependency>
            <groupId>org.nutz</groupId>
            <artifactId>nutz-plugins-validation</artifactId>
            <version>1.r.67</version>
        </dependency>
```

bean对象添加注解 Validations
```
@Table("sys_dict")
public class Dict extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Name
    @Column("id")
    @Comment("编号 ")
    @ColDefine(type = ColType.VARCHAR, width = 64)
    @Prev(els = {@EL("uuid()")})
    private String id;

    /**
     * 数据值
     */
    @Column("value")
    @Comment("数据值 ")
    @Validations(required=true,errorMsg = "数据值不能为空")
    private String value;

    /**
     * 标签名
     */
    @Column("label")
    @Comment("标签名 ")
    @Validations(required=true,errorMsg = "标签名不能为空")
    private String label;
}
```
动作链 配置 文档地址参考  
https://nutzam.com/core/mvc/action_chain.html  
把插件里面的处理器加进去，放在AdaptorProcessor之后
列如json配置
```
var chain={
  "default": {
    "ps": [
      "cn.wizzer.app.web.commons.processor.LogTimeProcessor",
      "cn.wizzer.app.web.commons.processor.GlobalsSettingProcessor",
      "org.nutz.mvc.impl.processor.UpdateRequestAttributesProcessor",
      "org.nutz.mvc.impl.processor.EncodingProcessor",
      "org.nutz.mvc.impl.processor.ModuleProcessor",
      "cn.wizzer.app.web.commons.processor.NutShiroProcessor",
      "cn.wizzer.app.web.commons.processor.XssSqlFilterProcessor",
      "org.nutz.mvc.impl.processor.ActionFiltersProcessor",
      "org.nutz.mvc.impl.processor.AdaptorProcessor",
      "org.nutz.plugins.validation.ValidationProcessor",
      "org.nutz.mvc.impl.processor.MethodInvokeProcessor",
      "org.nutz.mvc.impl.processor.ViewProcessor"
    ],
    "error": 'org.nutz.mvc.impl.processor.FailProcessor'
  }
};
```
自定义动作链参考  
https://github.com/TomYule/NutzSite/blob/master/src/main/java/io/nutz/nutzsite/common/mvc/MyActionChainMaker.java  
注意需要在启动方法添加注解
```
@ChainBy(type= MyActionChainMaker.class, args={})
```
Controller 里面添加Errors对象 不需要@Param
```	
@At
@POST
@Ok("json")
@Slog(tag="字典", after="新增保存字典id=${args[0].id}")
public Object addDo(@Param("..") Dict dict, Errors es,HttpServletRequest req) {
    try {
        if(es.hasError()){
               return Result.error(es.getErrorsList().toString());
           }
        dictService.insert(dict);
        return Result.success("system.success");
    } catch (Exception e) {
        return Result.error("system.error");
    }
}
```
如有问题 可先前往 https://nutz.cn/ 搜索validation  
无类似问题可提问