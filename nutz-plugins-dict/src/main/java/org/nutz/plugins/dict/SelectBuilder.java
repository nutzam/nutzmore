package org.nutz.plugins.dict;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.dict.Select.Fields;
import org.nutz.plugins.dict.chain.GlobalSelectProcessor;
import org.nutz.plugins.dict.chain.SelectProcessor;
import org.nutz.plugins.dict.chain.SelectProcessorBuilder;
import org.nutz.resource.Scans;


/**
 * 下拉框注解，用于生成全局字典
 * 
 * @author 邓华锋 http://dhf.ink
 *
 */
public class SelectBuilder {
	private static final Log log = Logs.get();
	private boolean systemProperties;
	private SelectProcessor selectProcessor;
	private LinkedList<SelectProcessor> selectFirst;
	private LinkedList<SelectProcessor> selectLast;
	private String[] packages;
	private String jsonFilePath;

	public final SelectBuilder setPackages(String... packages) {
		this.packages = packages;
		return this;
	}

	public final SelectBuilder setJsonFilePath(String jsonFilePath) {
		this.jsonFilePath = jsonFilePath;
		return this;
	}

	public static SelectBuilder create() {
		return new SelectBuilder();
	}

	public final SelectBuilder useSystemProperties() {
		this.systemProperties = true;
		return this;
	}

	public final SelectBuilder addProcessorFirst(final SelectProcessor itcp) {
		if (itcp == null) {
			return this;
		}
		if (selectFirst == null) {
			selectFirst = new LinkedList<SelectProcessor>();
		}
		selectFirst.addFirst(itcp);
		return this;
	}

	public final SelectBuilder addProcessorLast(final SelectProcessor itcp) {
		if (itcp == null) {
			return this;
		}
		if (selectLast == null) {
			selectLast = new LinkedList<SelectProcessor>();
		}
		selectLast.addLast(itcp);
		return this;
	}

	@SuppressWarnings("unchecked")
	public void build() {
		if (systemProperties) {

		}
		SelectProcessor selectProcessorCopy = this.selectProcessor;
		// 添加执行链
		if (selectProcessorCopy == null) {
			final SelectProcessorBuilder b = SelectProcessorBuilder.create();
			if (selectFirst != null) {
				for (final SelectProcessor i : selectFirst) {
					b.addFirst(i);
				}
			}
			b.addAll(new GlobalSelectProcessor());
			if (selectLast != null) {
				for (final SelectProcessor i : selectLast) {
					b.addLast(i);
				}
			}
			selectProcessorCopy = b.build();
		}
		// 全局 用于生成文件
		Map<String, Object> globalDictVal = new HashMap<String, Object>();
		for (String pk : this.packages) {
			for (Class<?> clazz : Scans.me().scanPackage(pk)) {
				Select select = clazz.getAnnotation(Select.class);
				if (select == null) {
					continue;
				}
				String name = select.name();
				Fields value = select.value();
				Fields text = select.text();
				if (Strings.isBlank(name)) {
					name = Strings.lowerFirst(clazz.getSimpleName());
				}

				Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) clazz;
				String val = value.name().toLowerCase();
				String txt = text.name().toLowerCase();
				try {
					Method valMethod = enumClass.getMethod(val);
					Method txtMethod = enumClass.getMethod(txt);
					Enum<?>[] enumElems = enumClass.getEnumConstants();
					for (int i = 0; i < enumElems.length; i++) {
						Enum<?> enumElem = enumElems[i];
						// TODO 如果没有value方法，则调用name方法
						String valStr = valMethod.invoke(enumElem).toString();
						String txtStr = txtMethod.invoke(enumElem).toString();
						selectProcessorCopy.process(valStr, txtStr);
					}
					selectProcessorCopy.put(name);
				} catch (NoSuchMethodException e) {
					log.error("枚举没有找到此方法", e);
				} catch (SecurityException e) {
					log.error("SecurityException", e);
				} catch (IllegalAccessException e) {
					log.error("IllegalAccessException", e);
				} catch (IllegalArgumentException e) {
					log.error("IllegalArgumentException", e);
				} catch (InvocationTargetException e) {
					log.error("InvocationTargetException", e);
				}
			}
		}
		//put全局变量里
		selectProcessorCopy.putGlobalDict(globalDictVal);
		
		//进行遍历生成js文件
		Iterator<Entry<String, Object>> it = globalDictVal.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Object> entry = it.next();
			Object dictVal = entry.getValue();
			String key = entry.getKey();
			String valKey = "${" + entry.getKey() + "};";
			String jsonTpl = "var " + key + "=" + valKey + ";";
			String json = jsonTpl.replace(valKey, Json.toJson(dictVal));
			System.out.println(json);
			String fileName = jsonFilePath + "/" + key + ".js";
			File file = new File(fileName);
			try {
				if (!file.getParentFile().exists()) {
					// 如果目标文件所在的目录不存在，则创建父目录
					log.info("目标文件所在目录不存在，准备创建它！");
					if (!file.getParentFile().mkdirs()) {
						log.info("创建目标文件所在目录失败！");
					}
				}
				if (!file.exists()) {
					file.createNewFile();
				} else {
					file.delete();
					file.createNewFile();
				}
				FileWriter writer = new FileWriter(file, true);
				writer.write(json);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		/*try {
			StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
			Configuration cfg = Configuration.defaultConfiguration();
			GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
			Template t = gt.getTemplate(FileUtils.readFileToString(new File(jsFilePath)));
			t.binding("dictSelectMap", dictSelect);
			//t.binding("jqgridSelectMap", jqgridSelect);
			//t.binding("editableSelectMap", editableSelect);
			t.renderTo(new FileOutputStream(new File(jsFilePath)));
		} catch (BeetlException e) {
			log.error("BeetlException",e);
		} catch (IOException e) {
			log.error("IOException",e);
		}*/
	}
	/*public static void outToJs(String jsFilePath){
		SelectUtils.loadSelectToMap();
		String dictSelect=Json.toJson(SelectUtils.dictSelectMap);
		log.info("var dictSelect="+dictSelect);
		String jqgridSelect=Json.toJson(SelectUtils.jqgridSelectMap);
		log.info("var jqgridSelect="+jqgridSelect);
		String editableSelect=Json.toJson(SelectUtils.editableSelectMap);
		log.info("var editableSelect="+editableSelect);
		try {
			StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
			Configuration cfg = Configuration.defaultConfiguration();
			GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
			Template t = gt.getTemplate(JS_TPL);
			t.binding("dictSelectMap", dictSelect);
			t.binding("jqgridSelectMap", jqgridSelect);
			t.binding("editableSelectMap", editableSelect);
			t.renderTo(new FileOutputStream(new File(jsFilePath)));
		} catch (BeetlException e) {
			log.error("BeetlException",e);
		} catch (IOException e) {
			log.error("IOException",e);
		}
	}*/
	
}