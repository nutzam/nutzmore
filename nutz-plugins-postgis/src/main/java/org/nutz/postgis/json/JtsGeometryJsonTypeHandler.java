package org.nutz.postgis.json;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.nutz.json.JsonFormat;
import org.nutz.json.JsonRender;
import org.nutz.json.JsonTypeHandler;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.util.NutMap;
import org.postgis.jts.JtsGeometry;

/**
 * 
 * @author Kerbores(kerbores@gmail.com)
 *
 */
public class JtsGeometryJsonTypeHandler extends JsonTypeHandler {

    /**
     * 是否为JtsGeometry
     * 
     * @param mirror
     *            对象镜像
     * @return 是否为JtsGeometry
     */
    boolean isJtsGeometry(Mirror<?> mirror) {
        return mirror.getType() == JtsGeometry.class;
    }

    /**
     * @param mirror
     * @param obj
     * @return
     * @see org.nutz.json.JsonTypeHandler#supportFromJson(org.nutz.lang.Mirror,
     *      java.lang.Object)
     */
    @Override
    public boolean supportFromJson(Mirror<?> mirror, Object obj) {
        return isJtsGeometry(mirror);
    }

    /**
     * @param mirror
     * @param obj
     * @param jf
     * @return
     * @see org.nutz.json.JsonTypeHandler#supportToJson(org.nutz.lang.Mirror,
     *      java.lang.Object, org.nutz.json.JsonFormat)
     */
    @Override
    public boolean supportToJson(Mirror<?> mirror, Object obj, JsonFormat jf) {
        return isJtsGeometry(mirror);
    }

    /**
     * @param mirror
     * @param currentObj
     * @param r
     * @param jf
     * @throws IOException
     * @see org.nutz.json.JsonTypeHandler#toJson(org.nutz.lang.Mirror,
     *      java.lang.Object, org.nutz.json.JsonRender,
     *      org.nutz.json.JsonFormat)
     */
    @Override
    public void toJson(Mirror<?> mirror, Object currentObj, JsonRender render, JsonFormat jf) throws IOException {
        if (currentObj == null) {
            return;
        }
        serialize(((JtsGeometry) currentObj).getGeometry(), render);
    }

    private void serialize(Geometry geometry, JsonRender render) {
        String className = geometry.getClass().getName();
        switch (className) {
        case "org.locationtech.jts.geom.Point":
            rend(render, Type.POINT.getValue(), point2Array((Point) geometry));
            break;
        case "org.locationtech.jts.geom.MultiPoint":
            rend(render, Type.MULTI_POINT.getValue(), multiPoint2Array((MultiPoint) geometry));
            break;
        case "org.locationtech.jts.geom.LineString":
            rend(render, Type.LINE_STRING.getValue(), lineString2Array((LineString) geometry));
            break;
        case "org.locationtech.jts.geom.MultiLineString":
            rend(render, Type.MULTI_LINE_STRING.getValue(), multiLineString2Array((MultiLineString) geometry));
            break;
        case "org.locationtech.jts.geom.Polygon":
            rend(render, Type.POLYGON.getValue(), polygon2Array((Polygon) geometry));
            break;
        case "org.locationtech.jts.geom.MultiPolygon":
            rend(render, Type.MULTI_POLYGON.getValue(), multiPolygon2Array((MultiPolygon) geometry));
            break;
        case "org.locationtech.jts.geom.GeometryCollection":
            rend(render, Type.GEOMETRY_COLLECTION.getValue(), geometryCollection2Array((GeometryCollection) geometry));
            break;
        default:
            throw Lang.makeThrow(String.format("Geometry type %s is not supported.",
                                               geometry.getClass().getName()));
        }
    }

    public Object[] geometryCollection2Array(org.locationtech.jts.geom.GeometryCollection geometryCollection) {
        Object[] array = new Object[geometryCollection.getNumGeometries()];
        for (int i = 0; i != geometryCollection.getNumGeometries(); ++i) {
            array[i] = geometry2Array(geometryCollection.getGeometryN(i));
        }
        return array;
    }

    public Object[] geometry2Array(Geometry geometry) {
        switch (geometry.getClass().getName()) {
        case "org.locationtech.jts.geom.Point":
            return point2Array((Point) geometry);
        case "org.locationtech.jts.geom.MultiPoint":
            return multiPoint2Array((MultiPoint) geometry);
        case "org.locationtech.jts.geom.LineString":
            return lineString2Array((LineString) geometry);
        case "org.locationtech.jts.geom.MultiLineString":
            return multiLineString2Array((MultiLineString) geometry);
        case "org.locationtech.jts.geom.Polygon":
            return polygon2Array((Polygon) geometry);
        case "org.locationtech.jts.geom.MultiPolygon":
            return multiPolygon2Array((MultiPolygon) geometry);
        case "org.locationtech.jts.geom.GeometryCollection":
            return geometryCollection2Array((GeometryCollection) geometry);
        default:
            throw Lang.makeThrow(String.format("Geometry type %s is not supported.",
                                               geometry.getClass().getName()));
        }
    }

    private Double[][][][] multiPolygon2Array(MultiPolygon multiPolygon) {
        List<Polygon> polygons = Lang.list();
        for (int i = 0; i < multiPolygon.getNumGeometries(); ++i) {
            polygons.add((Polygon) multiPolygon.getGeometryN(i));
        }
        return Lang.collection2array(polygons.stream().map(this::polygon2Array).collect(Collectors.toList()));
    }

    private Double[][][] multiLineString2Array(MultiLineString multiLineString) {
        List<LineString> lineStrings = Lang.list();
        for (int i = 0; i < multiLineString.getNumGeometries(); ++i) {
            lineStrings.add((LineString) multiLineString.getGeometryN(i));
        }
        return Lang.collection2array(lineStrings.stream().map(this::lineString2Array).collect(Collectors.toList()));
    }

    private Double[][][] polygon2Array(Polygon polygon) {
        List<LineString> lineStrings = Lang.list(polygon.getExteriorRing());
        for (int i = 0; i < polygon.getNumInteriorRing(); ++i) {
            lineStrings.add(polygon.getInteriorRingN(i));
        }
        return Lang.collection2array(lineStrings.stream().map(this::lineString2Array).collect(Collectors.toList()));
    }

    private Double[][] lineString2Array(LineString lineString) {
        Double[][] array = new Double[lineString.getCoordinateSequence().size()][2];
        for (int i = 0; i < lineString.getCoordinateSequence().size(); ++i) {
            array[i] = coordinate2Array(lineString.getCoordinateSequence().getCoordinate(i));
        }
        return array;
    }

    private Double[][] multiPoint2Array(MultiPoint multiPoint) {
        Double[][] array = new Double[multiPoint.getNumGeometries()][2];
        for (int i = 0; i < multiPoint.getNumGeometries(); ++i) {
            array[i] = point2Array((Point) multiPoint.getGeometryN(i));
        }
        return array;
    }

    public void rend(JsonRender render, String type, Double[] arr) {
        try {
            render.render(NutMap.NEW()
                                .addv(Field.TYPE.getValue(), type)
                                .addv(Field.COORDINATES.getValue(), arr));
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

    public void rend(JsonRender render, String type, Double[][] arr) {
        try {
            render.render(NutMap.NEW()
                                .addv(Field.TYPE.getValue(), type)
                                .addv(Field.COORDINATES.getValue(), arr));
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

    public void rend(JsonRender render, String type, Double[][][] arr) {
        try {
            render.render(NutMap.NEW()
                                .addv(Field.TYPE.getValue(), type)
                                .addv(Field.COORDINATES.getValue(), arr));
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

    public void rend(JsonRender render, String type, Double[][][][] arr) {
        try {
            render.render(NutMap.NEW()
                                .addv(Field.TYPE.getValue(), type)
                                .addv(Field.COORDINATES.getValue(), arr));
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

    /**
     * @param render
     * @param type
     * @param coordinates
     */
    private void rend(JsonRender render, String type, Object[] coordinates) {
        try {
            render.render(NutMap.NEW()
                                .addv(Field.TYPE.getValue(), type)
                                .addv(Field.COORDINATES.getValue(), coordinates));
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

    /**
     * point转换成数组
     * 
     * @param point
     *            点
     * @return 数组
     */
    private Double[] point2Array(Point point) {
        return Lang.array(point.getX(), point.getY());
    }

    /**
     * coordinate转换成数组
     * 
     * @param coordinate
     *            coordinate
     * @return 数组
     */
    private Double[] coordinate2Array(Coordinate coordinate) {
        if (coordinate.getZ() > 0) {
            return Lang.array(coordinate.getX(), coordinate.getY(), coordinate.getZ());
        }
        return Lang.array(coordinate.getX(), coordinate.getY());
    }

}
