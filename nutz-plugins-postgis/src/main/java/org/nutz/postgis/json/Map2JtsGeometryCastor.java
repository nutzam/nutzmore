package org.nutz.postgis.json;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import org.postgis.jts.JtsGeometry;

/**
 * 
 * @author Kerbores(kerbores@gmail.com)
 *
 */
public class Map2JtsGeometryCastor extends Castor<NutMap, JtsGeometry> {
    private static final int DEFAULT_SRID = 4326;

    /**
     * @param src
     * @param toType
     * @param args
     * @return
     * @throws FailToCastObjectException
     * @see org.nutz.castor.Castor#cast(java.lang.Object, java.lang.Class,
     *      java.lang.String[])
     */
    @Override
    public JtsGeometry cast(NutMap map, Class<?> toType, String... args) {
        return new JtsGeometry(deserializeGeometry(map));
    }

    private Geometry deserializeGeometry(Object node) {
        NutMap map = Lang.obj2nutmap(node);
        String typeName = map.getString(Field.TYPE.getValue());

        GeometryType type = GeometryType.fromString(typeName)
                                        .orElseThrow(() -> Lang.makeThrow("Invalid geometry type: " + typeName));

        switch (type) {
        case POINT:
            return deserializePoint(map);
        case MULTI_POINT:
            return deserializeMultiPoint(map);
        case LINE_STRING:
            return deserializeLineString(map);
        case MULTI_LINE_STRING:
            return deserializeMultiLineString(map);
        case POLYGON:
            return deserializePolygon(map);
        case MULTI_POLYGON:
            return deserializeMultiPolygon(map);
        case GEOMETRY_COLLECTION:
            return deserializeGeometryCollection(map);
        default:
            throw Lang.makeThrow("Invalid geometry type: " + typeName);
        }
    }

    /**
     * @param map
     * @return
     */
    private Geometry deserializeGeometryCollection(NutMap map) {
        Object[] geometries = map.getArray(Field.GEOMETRIES.getValue(), Object.class);
        Geometry[] geom = new Geometry[geometries.length];
        for (int i = 0; i != geometries.length; ++i) {
            geom[i] = deserializeGeometry(geometries[i]);
        }
        return getDefaultGeometryFactory().createGeometryCollection(geom);
    }

    /**
     * @param map
     * @return
     */
    private Geometry deserializeMultiPolygon(NutMap map) {
        double[][][][] coordinates = map.getArray(Field.COORDINATES.getValue(), double[][][].class);
        Polygon[] polygons = new Polygon[coordinates.length];
        for (int i = 0; i != coordinates.length; ++i) {
            polygons[i] = deserializeLinearRings(coordinates[i]);
        }
        return getDefaultGeometryFactory().createMultiPolygon(polygons);
    }

    /**
     * @param map
     * @return
     */
    private Geometry deserializePolygon(NutMap map) {
        return deserializeLinearRings(map.getArray(Field.COORDINATES.getValue(), double[][].class));
    }

    private Polygon deserializeLinearRings(double[][][] node) {
        LinearRing shell = deserializeLinearRing(node[0]);
        LinearRing[] holes = new LinearRing[node.length - 1];
        for (int i = 1; i < node.length; ++i) {
            holes[i - 1] = deserializeLinearRing(node[i]);
        }
        return getDefaultGeometryFactory().createPolygon(shell, holes);
    }

    private LinearRing deserializeLinearRing(double[][] node) {
        return getDefaultGeometryFactory().createLinearRing(deserializeCoordinates(node));
    }

    /**
     * @param map
     * @return
     */
    private Geometry deserializeMultiLineString(NutMap map) {
        LineString[] lineStrings = lineStringsFromJson(map.getArray(Field.COORDINATES.getValue(), double[][].class));
        return getDefaultGeometryFactory().createMultiLineString(lineStrings);
    }

    /**
     * @param array
     * @return
     */
    private LineString[] lineStringsFromJson(double[][][] array) {
        LineString[] strings = new LineString[array.length];
        for (int i = 0; i != array.length; ++i) {
            Coordinate[] coordinates = deserializeCoordinates(array[i]);
            strings[i] = getDefaultGeometryFactory().createLineString(coordinates);
        }
        return strings;
    }

    /**
     * @param map
     * @return
     */
    private LineString deserializeLineString(NutMap map) {
        Coordinate[] coords = deserializeCoordinates(map.getArray(Field.COORDINATES.getValue(), double[].class));
        return getDefaultGeometryFactory().createLineString(coords);
    }

    /**
     * @param map
     * @return
     */
    private Geometry deserializeMultiPoint(NutMap map) {
        Coordinate[] coords = deserializeCoordinates(map.getArray(Field.COORDINATES.getValue(), double[].class));
        return getDefaultGeometryFactory().createMultiPointFromCoords(coords);
    }

    /**
     * @param map
     * @return
     */
    private Coordinate[] deserializeCoordinates(double[][] arr) {
        Coordinate[] points = new Coordinate[arr.length];
        for (int i = 0; i != arr.length; ++i) {
            points[i] = deserializeCoordinate(arr[i]);
        }
        return points;
    }

    /**
     * @param map
     * @return
     */
    private Point deserializePoint(NutMap map) {
        return getDefaultGeometryFactory().createPoint(deserializeCoordinate(map.getArray(Field.COORDINATES.getValue(), double.class)));
    }

    private Coordinate deserializeCoordinate(Double[] arr) {
        if (arr.length < 2) {
            throw Lang.makeThrow(String.format("Invalid number of ordinates: %d", arr.length));
        } else {
            if (arr.length < 3) {
                return new Coordinate(arr[0], arr[1]);
            } else {
                return new Coordinate(arr[0], arr[1], arr[3]);
            }
        }
    }

    /**
     * @param arr
     * @return
     */
    private Coordinate deserializeCoordinate(double[] arr) {
        if (arr.length < 2) {
            throw Lang.makeThrow(String.format("Invalid number of ordinates: %d", arr.length));
        } else {
            if (arr.length < 3) {
                return new Coordinate(arr[0], arr[1]);
            } else {
                return new Coordinate(arr[0], arr[1], arr[3]);
            }
        }
    }

    private static GeometryFactory getDefaultGeometryFactory() {
        return new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), DEFAULT_SRID);
    }

}
