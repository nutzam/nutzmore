package org.nutz.integration.json4excel.util;

import java.lang.reflect.Field;

public class FieldUtil {

    public static boolean isString(Field field) {
        return field.getType().equals(String.class);
    }

    public static boolean isInteger(Field field) {
        return field.getType().equals(Integer.class);
    }
}
