package org.nutz.integration.spring;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.regex.Pattern;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJsonHttpMessageConverter;

/**
 * @author Kerbores(kerbores@gamil.com)
 *
 */
public class NutzJsonMessageConverter extends AbstractJsonHttpMessageConverter {

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

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.http.converter.json.AbstractJsonHttpMessageConverter#
     * readInternal(java.lang.reflect.Type, java.io.Reader)
     */
    @Override
    protected Object readInternal(Type resolvedType, Reader reader) throws Exception {
        return Json.fromJson(resolvedType, reader);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.http.converter.json.AbstractJsonHttpMessageConverter#
     * writeInternal(java.lang.Object, java.lang.reflect.Type, java.io.Writer)
     */
    @Override
    protected void writeInternal(Object o, Type type, Writer writer) throws Exception {
        Json.toJson(writer, o, format);
    }
}
