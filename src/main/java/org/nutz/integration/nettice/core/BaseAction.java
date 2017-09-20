package org.nutz.integration.nettice.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.nutz.integration.nettice.core.convertor.PriTypeConverter;
import org.nutz.integration.nettice.core.convertor.PrimitiveType;
import org.nutz.integration.nettice.core.exception.ActionInvocationException;
import org.nutz.integration.nettice.core.exception.ValidationException;
import org.nutz.integration.nettice.core.utils.GenericsUtil;
import org.nutz.json.Json;
import org.nutz.lang.Encoding;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

/**
 * Action基础类，所有Action必须继承自此类。BaseAction封装了参数填充、转发、方法调用等功能，并向子类提供render等渲染方法。
 */
public class BaseAction {

	protected final Log logger = Logs.get();

	public Return processRequest(Method method, String methodName) throws Exception {
		if (method == null) {
			throw new NoSuchMethodException("Can not find specified method: " + methodName);
		}
		return exec(method);
	}

	/**
	 * 供内部使用的调用所指定方法的方法
	 * 
	 * @param method
	 *            所指定的需要被调用的方法
	 * @return 调用方法后所返回的结果
	 * @throws Exception
	 */
	private Return exec(Method method) throws Exception {
		GET getAnnotation = method.getAnnotation(GET.class);
		POST postAnnotation = method.getAnnotation(POST.class);
		if (getAnnotation != null && postAnnotation != null) {
			throw new IllegalStateException("Both get annotation and post annotation are used, only one can be used at an action");
		}
		// 获得所调用方法的参数类型和所使用的Annotation数组
		Class<?>[] type = method.getParameterTypes();
		Annotation[][] annotationArray = method.getParameterAnnotations();
		// 用于存放调用参数的对象数组
		Object[] paramTarget = new Object[type.length];

		// 获取参数列表
		Map<String, List<String>> paramMap = getParamMap();
		// 构造调用所需要的参数数组
		for (int i = 0; i < type.length; i++) {
			Class<?> typeClasz = (Class<?>) type[i];
			Annotation[] annotation = annotationArray[i];
			if (annotation == null || annotation.length == 0) {
				throw new Exception("Must specify a @Read annotation for every parameter in method: " + method.getName());
			}
			Param read = (Param) annotation[0];
			// 生成当前的调用参数
			try {
				Object paramValue = getParamValue(paramMap, typeClasz, read, method, i);
				paramTarget[i] = paramValue;
			} catch (Throwable e) {
				throw getInvokeException(method, paramTarget, new IllegalArgumentException("参数不合法:" + read.value(), e));
			}
		}
		try {
			return (Return) method.invoke(this, paramTarget);
		} catch (InvocationTargetException e) {
			throw getInvokeException(method, paramTarget, e.getCause());
		} catch (IllegalArgumentException e) {
			throw getInvokeException(method, paramTarget, e);
		}
	}

	/**
	 * GET 参数解析
	 * 
	 * @param paramMap
	 * @param type
	 * @param read
	 * @param method
	 * @param index
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object getParamValue(Map<String, List<String>> paramMap, Class<?> type, Param read, Method method, int index) throws InstantiationException, IllegalAccessException {
		Object value = null;
		String key = read.value();
		String defaultValue = read.df();
		if (key != null && key.length() > 0) {
			if (Map.class.isAssignableFrom(type)) {
				if (index > 0) {
					throw new ValidationException("Must have only one Map type parameter");
				}

				List<Class> types = GenericsUtil.getMethodGenericParameterTypes(method, index);
				if (types.size() == 2 && (types.get(0) != String.class || types.get(1) != String.class)) {
					throw new ValidationException("Map type parameter must both be String, Occuring Point: " + method.toGenericString());
				}

				Map<String, String> valueMap = new HashMap<String, String>();
				for (String paramKey : paramMap.keySet()) {
					List<String> valueList = paramMap.get(paramKey);
					valueMap.put(paramKey, valueList.get(0));
				}
				value = valueMap;
			} else {
				List<String> params = paramMap.get(key);
				if (params != null) {
					if (PrimitiveType.isPriType(type)) {
						value = PriTypeConverter.getInstance().convertValue(params.get(0), type);

					} else if (type.isArray()) {
						Object[] objArray = params.toArray();
						String[] strArray = objArray2StrArray(objArray);
						value = PriTypeConverter.getInstance().convertValue(strArray, type);

					} else if (List.class.isAssignableFrom(type)) {
						List<Object> list = null;
						List<Class> types = GenericsUtil.getMethodGenericParameterTypes(method, index);
						Class<?> listType = types.size() == 1 ? types.get(0) : String.class;
						if (List.class == type) {
							list = new ArrayList<Object>();
						} else {
							list = (List<Object>) type.newInstance();
						}
						for (int i = 0; i < params.size(); i++) {
							if (params.get(i).length() > 0) {
								list.add(PriTypeConverter.getInstance().convertValue(params.get(i), listType));
							}
						}
						value = list;
					}
				} else {
					if (defaultValue != null && PrimitiveType.isPriType(type)) {
						value = PriTypeConverter.getInstance().convertValue(defaultValue, type);
					}
				}
			}
		}
		return value;
	}

	private String[] objArray2StrArray(Object[] objArray) {
		int length = objArray.length;
		String[] strArray = new String[length];
		for (int i = 0; i < length; i++) {
			strArray[i] = String.valueOf(objArray[i]);
		}
		return strArray;
	}

	/**
	 * 获取请求参数 Map
	 */
	private Map<String, List<String>> getParamMap() {
		Map<String, List<String>> paramMap = new HashMap<String, List<String>>();

		Object msg = DataHolder.getRequest();
		HttpRequest request = (HttpRequest) msg;
		HttpMethod method = request.method();
		if (method.equals(HttpMethod.GET)) {
			String uri = request.uri();
			QueryStringDecoder queryDecoder = new QueryStringDecoder(uri, Charset.forName(Encoding.UTF8));
			paramMap = queryDecoder.parameters();

		} else if (method.equals(HttpMethod.POST)) {
			FullHttpRequest fullRequest = (FullHttpRequest) msg;
			paramMap = getPostParamMap(fullRequest);
		}

		return paramMap;
	}

	// 目前支持最常用的 application/json 、application/x-www-form-urlencoded 几种 POST
	// Content-type，可自行扩展！！！
	@SuppressWarnings("unchecked")
	private Map<String, List<String>> getPostParamMap(FullHttpRequest fullRequest) {
		Map<String, List<String>> paramMap = new HashMap<String, List<String>>();
		HttpHeaders headers = fullRequest.headers();
		String contentType = getContentType(headers);
		if ("application/json".equals(contentType)) {
			String jsonStr = fullRequest.content().toString(Charset.forName(Encoding.UTF8));
			NutMap obj = Json.fromJson(NutMap.class, jsonStr);
			Iterator<Entry<String, Object>> iterator = obj.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Object> entry = iterator.next();
				String key = entry.getKey();
				Object value = entry.getValue();
				Class<?> valueType = value.getClass();
				List<String> valueList = null;
				if (paramMap.containsKey(key)) {
					valueList = paramMap.get(key);
				} else {
					valueList = new ArrayList<String>();
				}
				if (PrimitiveType.isPriType(valueType)) {
					valueList.add(value.toString());
					paramMap.put(key, valueList);

				} else if (valueType.isArray()) {
					int length = Array.getLength(value);
					for (int i = 0; i < length; i++) {
						String arrayItem = String.valueOf(Array.get(value, i));
						valueList.add(arrayItem);
					}
					paramMap.put(key, valueList);

				} else if (List.class.isAssignableFrom(valueType)) {
					paramMap.put(key, (List<String>) value);
				} else if (Map.class.isAssignableFrom(valueType)) {
					Map<String, String> tempMap = (Map<String, String>) value;
					for (String tempKey : tempMap.keySet()) {
						List<String> tempList = new ArrayList<String>();
						tempList.add(tempMap.get(tempKey));
						paramMap.put(tempKey, tempList);
					}
				}
			}
			return paramMap;

		} else if ("application/x-www-form-urlencoded".equals(contentType)) {
			String jsonStr = fullRequest.content().toString(Charset.forName(Encoding.UTF8));
			QueryStringDecoder queryDecoder = new QueryStringDecoder(jsonStr, false);
			paramMap = queryDecoder.parameters();
		}
		return paramMap;
	}

	private String getContentType(HttpHeaders headers) {
		String contentType = headers.get("Content-Type").toString();
		String[] list = contentType.split(";");
		return list[0];
	}

	private ActionInvocationException getInvokeException(Method method, Object[] paramTarget, Throwable ex) {
		String msg = "action method=" + method.getName() + ", params=" + Arrays.toString(paramTarget);
		return new ActionInvocationException(method.getName(), paramTarget, msg, ex);
	}

}
