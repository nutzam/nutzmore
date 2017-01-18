package org.nutz.integration.spring;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.Charset;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

/**
 * @author Kerbores(kerbores@gamil.com)
 *
 */
public class NutzJsonMessageConverter extends AbstractHttpMessageConverter<Object> {

	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	/**
	 * 
	 */
	public NutzJsonMessageConverter() {
		super(new MediaType[] { MediaType.APPLICATION_JSON, new MediaType("application", "*+json") });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.http.converter.AbstractHttpMessageConverter#supports(
	 * java.lang.Class)
	 */
	@Override
	protected boolean supports(Class<?> paramClass) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.http.converter.AbstractHttpMessageConverter#
	 * readInternal(java.lang.Class, org.springframework.http.HttpInputMessage)
	 */
	@Override
	protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		Reader reader = new InputStreamReader(inputMessage.getBody(), getCharset(inputMessage.getHeaders()));
		try {
			return Json.fromJson(clazz, reader);
		} catch (Exception ex) {
			throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
		}
	}

	/**
	 * @param headers
	 * @return
	 */
	private Charset getCharset(HttpHeaders headers) {

		if ((headers == null) || (headers.getContentType() == null)
				|| (headers.getContentType().getCharSet() == null)) {
			return DEFAULT_CHARSET;
		}
		return headers.getContentType().getCharSet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.http.converter.AbstractHttpMessageConverter#
	 * writeInternal(java.lang.Object,
	 * org.springframework.http.HttpOutputMessage)
	 */
	@Override
	protected void writeInternal(Object obj, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		Charset charset = getCharset(outputMessage.getHeaders());
		OutputStreamWriter writer = new OutputStreamWriter(outputMessage.getBody(), charset);
		try {
			Json.toJson(writer, obj, JsonFormat.compact());
		} catch (Exception ex) {
			throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
		}
	}

}
