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
使用select来注解枚举类，把所有字典想象成下拉框结构，所以有了selec这个注解。
```Java
/**
 * 下拉框注解，用于生成全局字典
 * 
 * @author 邓华锋 http://dhf.ink
 * @date 2016年6月29日 上午3:03:43
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Documented
public @interface Select {

	public enum Fields {
		NAME, TEXT, VALUE
	}

	/**
	 * 下拉框ID和name属性的值
	 * 
	 * @author 邓华锋
	 * @date 2016年6月29日 上午11:04:16
	 * 
	 * @return
	 */
	String name() default "";

	/**
	 * 下拉框显示的文本
	 * 
	 * @author 邓华锋
	 * @date 2016年6月29日 上午11:03:50
	 * 
	 * @return
	 */
	Fields text() default Fields.TEXT;

	/**
	 * 下拉框的值
	 * 
	 * @author 邓华锋
	 * @date 2016年6月29日 上午11:04:02
	 * 
	 * @return
	 */
	Fields value() default Fields.VALUE;

}
```


```Java
import org.nutz.plugins.dict.Select;

@Select
public enum JQGridSelectOPT {
	eq("等于","="),ne("不等于","<>"),bw("开始于","LIKE ${value}%"),bn("不开始于","NOT LIKE ${value}%"),ew("结束于","LIKE %${value}"),en("不结束于","NOT LIKE %${value}"),
	cn("包含","LIKE"),nc("不包含","NOT LIKE"),nu("空值于","IS NULL"),nn("非空值","IS NOT NULL"),in("属于","IN"),ni("不属于","NOT IN"),
	lt("小于","<"),le("小于等于","<="),gt("大于",">"),ge("大于等于",">=");

	private String text;

	private String value;

	private JQGridSelectOPT(String text, String value) {
		this.text = text;
		this.value = value;
	}

	public String text() {
		return text;
	}

	public String value() {
		return value;
	}

}
```

```Java
import org.nutz.plugins.dict.Select;
import org.nutz.plugins.dict.Select.Fields;

@Select(value=Fields.NAME)
public enum JQGridOrder {
	asc("升序",0),desc("降序",1) ;
	private String text;

	private int value;

	private JQGridOrder(String text, int value) {
		this.text = text;
		this.value = value;
	}

	public String text() {
		return text;
	}

	public int value() {
		return value;
	}
}
```
枚举让代码可维护性，可读性高
看以下示例：
```Java
String oper="edit";
if (StringUtils.equals(oper, JQGridOper.add.name())) {
	//TODO add
}else if (StringUtils.equals(oper, JQGridOper.del.name())) {
	//TODO delete
}else if(StringUtils.equals(oper, JQGridOper.edit.name())) {
	//TODO update
}
```

或 
```Java
int oper=1;
if (oper==JQGridOper.add.value()) {
	//TODO add
}else if (oper==JQGridOper.del.value()) {
	//TODO delete
}else if(oper==JQGridOper.edit.value()) {
	//TODO update
}
```

执行生成字典代码
```Java
Selects.custom().addProcessorFirst(new JqgridSelectProcessor()).addProcessorLast(new EditableSelectProcessor()).setPackages("org.nutz.plugins.dict").setJsonFilePath("e:/dict").build();
```

生成的结果：
globalSelect.js
```javascript
var globalSelect={
   "jQGridGroupOP": {
      "or": "或者",
      "and": "并且"
   },
   "jQGridOrder": {
      "asc": "升序",
      "desc": "降序"
   },
   "jQGridSelectOPT": {
      "=": "等于",
      "<>": "不等于",
      "LIKE ${value}%": "开始于",
      "NOT LIKE ${value}%": "不开始于",
      "LIKE %${value}": "结束于",
      "NOT LIKE %${value}": "不结束于",
      "LIKE": "包含",
      "NOT LIKE": "不包含",
      "IS NULL": "空值于",
      "IS NOT NULL": "非空值",
      "IN": "属于",
      "NOT IN": "不属于",
      "<": "小于",
      "<=": "小于等于",
      ">": "大于",
      ">=": "大于等于"
   }
};
```
jqgridSelect.js
```javascript
var jqgridSelect={
   "jQGridGroupOP": "or:或者;and:并且",
   "jQGridOrder": "asc:升序;desc:降序",
   "jQGridSelectOPT": "=:等于;<>:不等于;LIKE ${value}%:开始于;NOT LIKE ${value}%:不开始于;LIKE %${value}:结束于;NOT LIKE %${value}:不结束于;LIKE:包含;NOT LIKE:不包含;IS NULL:空值于;IS NOT NULL:非空值;IN:属于;NOT IN:不属于;<:小于;<=:小于等于;>:大于;>=:大于等于"
};
```
editableSelect.js
```javascript
var editableSelect={
   "jQGridGroupOP": [{
      "id": "or",
      "text": "或者"
   }, {
      "id": "and",
      "text": "并且"
   }],
   "jQGridOrder": [{
      "id": "asc",
      "text": "升序"
   }, {
      "id": "desc",
      "text": "降序"
   }],
   "jQGridSelectOPT": [{
      "id": "=",
      "text": "等于"
   }, {
      "id": "<>",
      "text": "不等于"
   }, {
      "id": "LIKE ${value}%",
      "text": "开始于"
   }, {
      "id": "NOT LIKE ${value}%",
      "text": "不开始于"
   }, {
      "id": "LIKE %${value}",
      "text": "结束于"
   }, {
      "id": "NOT LIKE %${value}",
      "text": "不结束于"
   }, {
      "id": "LIKE",
      "text": "包含"
   }, {
      "id": "NOT LIKE",
      "text": "不包含"
   }, {
      "id": "IS NULL",
      "text": "空值于"
   }, {
      "id": "IS NOT NULL",
      "text": "非空值"
   }, {
      "id": "IN",
      "text": "属于"
   }, {
      "id": "NOT IN",
      "text": "不属于"
   }, {
      "id": "<",
      "text": "小于"
   }, {
      "id": "<=",
      "text": "小于等于"
   }, {
      "id": ">",
      "text": "大于"
   }, {
      "id": ">=",
      "text": "大于等于"
   }]
};
```

如果要自定义实现字典生成，只需实现SelectProcessor接口，具体方法实现参考GlobalSelectProcessor、JqgridSelectProcessor和EditableSelectProcessor。
前段使用方法：以globalSelect.js为例,假如globalSelect.js生成的路径在web服务器的项目目录里
* 首先在页面引用这个js文件
```HTML
<script type="text/javascript" src="http://dhf.ink/dict/globalSelect.js"></script>
```

* 然后js可以这样调用
```javascript
var opName=globalSelect["jQGridGroupOP"]["or"];
console.log(opName);
```


计划：
* 不用实现SelectProcessor，直接用通过模板引擎来自定义字典生成格式


