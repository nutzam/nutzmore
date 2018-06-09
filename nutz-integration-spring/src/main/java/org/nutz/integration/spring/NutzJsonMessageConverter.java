package org.nutz.integration.spring;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.regex.Pattern;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

/**
 * @author Kerbores(kerbores@gamil.com)
 *
 */
public class NutzJsonMessageConverter extends AbstractGenericHttpMessageConverter<Object> {

	JsonFormat format = JsonFormat.compact();

	Pattern ignoreType;

	public NutzJsonMessageConverter setIgnoreType(String ignoreType) {
		if (Strings.isBlank(ignoreType)) {
			return this;
		}
		this.ignoreType = Pattern.compile(ignoreType);
		return this;
	}

	/**
	 * @param format
	 *            the format to set
	 */
	public NutzJsonMessageConverter setFormat(JsonFormat format) {
		this.format = format;
		return this;
	}

	public NutzJsonMessageConverter() {
		super(new MediaType[] { MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8,
				new MediaType("application", "*+json") });
	}

	@Override
	public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		return Json.fromJson(type, new InputStreamReader(inputMessage.getBody()));
	}

	@Override
	protected void writeInternal(Object t, Type type, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		Json.toJson(new OutputStreamWriter(outputMessage.getBody()), t, format);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return true;
	}

	@Override
	public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {

		/**
		 * 放过swagger
		 */
		if (Pattern.matches(".*springfox.*", clazz.getName()) || Pattern.matches(".*springfox.*", type.getTypeName())) {
			return false;
		}
		/**
		 * 放过spring 本身的各种玩意儿
		 */
		if (Pattern.matches("org.springframework.*", clazz.getName())
				|| Pattern.matches("org.springframework.*", type.getTypeName())) {
			return false;
		}
		return ignoreType == null || !ignoreType.matcher(clazz.getName()).matches();
	}

	@Override
	protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		return Json.fromJson(clazz, new InputStreamReader(inputMessage.getBody()));
	}
}
