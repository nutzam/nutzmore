package org.nutz.postgis.json;

import java.util.Optional;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 * 
 * @author Kerbores(kerbores@gmail.com)
 *
 */
public enum GeometryType {
    /**
     * Geometries of type {@link Point}.
     */
    POINT(Type.POINT),
    /**
     * Geometries of type {@link LineString}.
     */
    LINE_STRING(Type.LINE_STRING),
    /**
     * Geometries of type {@link Polygon}.
     */
    POLYGON(Type.POLYGON),
    /**
     * Geometries of type {@link MultiPoint}.
     */
    MULTI_POINT(Type.MULTI_POINT),
    /**
     * Geometries of type {@link MultiLineString}.
     */
    MULTI_LINE_STRING(Type.MULTI_LINE_STRING),
    /**
     * Geometries of type {@link MultiPolygon}.
     */
    MULTI_POLYGON(Type.MULTI_POLYGON),
    /**
     * Geometries of type {@link GeometryCollection}.
     */
    GEOMETRY_COLLECTION(Type.GEOMETRY_COLLECTION);

    private final String type;

    /**
     * 
     */
    private GeometryType(Type type) {
        this.type = type.getValue();
    }

    /**
     * The bit mask value of this {@link GeometryType}.
     *
     * @return The bit mask.
     */
    int mask() {
        return 1 << this.ordinal();
    }

    @Override
    public String toString() {
        return this.type;
    }

    /**
     * Get the geometry type from the supplied GeoJSON type value.
     *
     * @param value
     *            The GeoJSON type.
     * @return The {@link GeometryType}
     */
    public static Optional<GeometryType> fromString(String value) {
        for (GeometryType type : GeometryType.values()) {
            if (type.toString().equals(value)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }

    /**
     * Get the geometry type from the supplied {@link Geometry}.
     *
     * @param geometry
     *            The {@link Geometry}.
     * @return The {@link GeometryType}.
     */
    public static Optional<GeometryType> forGeometry(Geometry geometry) {
        if (geometry == null) {
            return Optional.empty();
        } else if (geometry instanceof Polygon) {
            return Optional.of(POLYGON);
        } else if (geometry instanceof Point) {
            return Optional.of(POINT);
        } else if (geometry instanceof MultiPoint) {
            return Optional.of(MULTI_POINT);
        } else if (geometry instanceof MultiPolygon) {
            return Optional.of(MULTI_POLYGON);
        } else if (geometry instanceof LineString) {
            return Optional.of(LINE_STRING);
        } else if (geometry instanceof MultiLineString) {
            return Optional.of(MULTI_LINE_STRING);
        } else if (geometry instanceof GeometryCollection) {
            return Optional.of(GEOMETRY_COLLECTION);
        } else {
            return Optional.empty();
        }
    }

}
