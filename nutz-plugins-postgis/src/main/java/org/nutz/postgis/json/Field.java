package org.nutz.postgis.json;

import org.nutz.lang.Strings;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 */
public enum Field {

    /**
     * 
     */
    TYPE("type"),
    /**
     * 
     */
    GEOMETRIES("geometries"),
    /**
     * 
     */
    COORDINATES("coordinates"),
    /**
     * 
     */
    BOUNDING_BOX("bbox");

    String value;

    /**
     * 
     */
    private Field(String value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    public static Field from(String field) {
        for (Field f : values()) {
            if (Strings.equals(field, f.getValue())) {
                return f;
            }
        }
        return null;
    }
}
