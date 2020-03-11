package org.nutz.postgis.json;

import org.nutz.lang.Strings;

/**
 * 
 * @author Kerbores(kerbores@gmail.com)
 *
 */
public enum Type {
    /**
     * 
     */
    POINT("Point"),
    /**
     * 
     */
    LINE_STRING("LineString"),
    /**
     * 
     */
    POLYGON("Polygon"),
    /**
     * 
     */
    MULTI_POINT("MultiPoint"),
    /**
     * 
     */
    MULTI_LINE_STRING("MultiLineString"),
    /**
     * 
     */
    MULTI_POLYGON("MultiPolygon"),
    /**
     * 
     */
    GEOMETRY_COLLECTION("GeometryCollection"),
    /**
     * 
     */
    FEATURE("Feature"),
    /**
     * 
     */
    FEATURE_COLLECTION("FeatureCollection");

    String value;

    /**
     * 
     */
    private Type(String value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    public static Type from(String type) {
        for (Type t : values()) {
            if (Strings.equals(type, t.getValue())) {
                return t;
            }
        }
        return null;
    }

}
