package org.nutz.plugin.spring.boot.converters;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

/**
 * @author kerbores kerbores@gmail.com
 *
 */
public class NutzJsonMessageConverter extends AbstractGenericHttpMessageConverter<Object> {
	{
		setSupportedMediaTypes(Lang.array2list(new MediaType[] { MediaType.APPLICATION_JSON }));
	}

	@Override
	public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		return Json.fromJson(type, new InputStreamReader(inputMessage.getBody()));
	}

	@Override
	protected void writeInternal(Object t, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		Json.toJson(new OutputStreamWriter(outputMessage.getBody()), t, JsonFormat.compact());
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return true;
	}

	@Override
	protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		return Json.fromJson(clazz, new InputStreamReader(inputMessage.getBody()));
	}
}
