/*
 * This file is inherited from Apache Calcite and modifed by ST-Lab under apache license.
 * You can find the original code from
 *
 * https://github.com/apache/calcite
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.urbcomp.start.db.function.coordtransform;

import org.locationtech.jts.geom.*;
import org.urbcomp.start.db.model.point.GPSPoint;
import org.urbcomp.start.db.model.point.SpatialPoint;
import org.urbcomp.start.db.model.roadnetwork.RoadNetwork;
import org.urbcomp.start.db.model.roadnetwork.RoadSegment;
import org.urbcomp.start.db.model.trajectory.Trajectory;
import org.urbcomp.start.db.util.GeometryFactoryUtils;

import java.util.List;
import java.util.stream.Collectors;
    // 抽象类，将各种空间对象转换到指定坐标系下
public abstract class AbstractCoordTransformer {
    public Point pointTransform(Point point) {
        GeometryFactory geometryFactory = GeometryFactoryUtils.defaultGeometryFactory();
        double[] lngLat = transform(point.getX(), point.getY());
        return geometryFactory.createPoint(new Coordinate(lngLat[0], lngLat[1]));
    }

    public LineString lineStringTransform(LineString lineString) {
        GeometryFactory geometryFactory = GeometryFactoryUtils.defaultGeometryFactory();
        Coordinate[] coordinates = lineString.getCoordinates();
        Coordinate[] res = new Coordinate[coordinates.length];
        for (int i = 0; i < coordinates.length; i++) {
            double[] lngLat = transform(coordinates[i].getX(), coordinates[i].getY());
            res[i] = new Coordinate(lngLat[0], lngLat[1]);
        }
        return geometryFactory.createLineString(res);
    }

    public Polygon polygonTransform(Polygon polygon) {
        GeometryFactory geometryFactory = GeometryFactoryUtils.defaultGeometryFactory();
        LinearRing shell = geometryFactory.createLinearRing(
            lineStringTransform(polygon.getExteriorRing()).getCoordinateSequence()
        );
        LinearRing[] holes = new LinearRing[polygon.getNumInteriorRing()];
        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
            LinearRing linearRing = polygon.getInteriorRingN(i);
            LineString lineString = lineStringTransform(linearRing);
            holes[i] = geometryFactory.createLinearRing(lineString.getCoordinateSequence());
        }
        return geometryFactory.createPolygon(shell, holes);
    }

    public MultiPoint multiPointTransform(MultiPoint multiPoint) {
        GeometryFactory geometryFactory = GeometryFactoryUtils.defaultGeometryFactory();
        Point[] points = new Point[multiPoint.getNumGeometries()];
        for (int i = 0; i < multiPoint.getNumGeometries(); i++) {
            Point point = (Point) multiPoint.getGeometryN(i);
            points[i] = pointTransform(point);
        }
        return geometryFactory.createMultiPoint(points);
    }

    public MultiLineString multiLineStringTransform(MultiLineString multiLineString) {
        GeometryFactory geometryFactory = GeometryFactoryUtils.defaultGeometryFactory();
        LineString[] lineStrings = new LineString[multiLineString.getNumGeometries()];
        for (int i = 0; i < multiLineString.getNumGeometries(); i++) {
            LineString lineString = (LineString) multiLineString.getGeometryN(i);
            lineStrings[i] = lineStringTransform(lineString);
        }
        return geometryFactory.createMultiLineString(lineStrings);
    }

    public MultiPolygon multiPolygonTransform(MultiPolygon mPolygon) {
        GeometryFactory geometryFactory = GeometryFactoryUtils.defaultGeometryFactory();
        Polygon[] polygons = new Polygon[mPolygon.getNumGeometries()];
        for (int i = 0; i < mPolygon.getNumGeometries(); i++) {
            Polygon polygon = (Polygon) mPolygon.getGeometryN(i);
            polygons[i] = polygonTransform(polygon);
        }
        return geometryFactory.createMultiPolygon(polygons);
    }

    public Geometry geometryTransform(Geometry geometry) {
        if (geometry instanceof Point) {
            return pointTransform((Point) geometry);
        } else if (geometry instanceof LineString) {
            return lineStringTransform((LineString) geometry);
        } else if (geometry instanceof Polygon) {
            return polygonTransform((Polygon) geometry);
        } else if (geometry instanceof MultiPoint) {
            return multiPointTransform((MultiPoint) geometry);
        } else if (geometry instanceof MultiLineString) {
            return multiLineStringTransform((MultiLineString) geometry);
        } else if (geometry instanceof MultiPolygon) {
            return multiPolygonTransform((MultiPolygon) geometry);
        } else if (geometry instanceof GeometryCollection) {
            return geometryCollectionTransform((GeometryCollection) geometry);
        }
        return null;
    }

    public GeometryCollection geometryCollectionTransform(GeometryCollection geometryCollection) {
        GeometryFactory geometryFactory = GeometryFactoryUtils.defaultGeometryFactory();
        Geometry[] geometries = new Geometry[geometryCollection.getNumGeometries()];
        for (int i = 0; i < geometryCollection.getNumGeometries(); i++) {
            Geometry geometry = geometryCollection.getGeometryN(i);
            geometries[i] = geometryTransform(geometry);
        }
        return geometryFactory.createGeometryCollection(geometries);
    }

    public Trajectory trajectoryTransform(Trajectory trajectory) {
        List<GPSPoint> points = trajectory.getGPSPointList().stream().map(o -> {
            double[] coords = transform(o.getLng(), o.getLat());
            return new GPSPoint(o.getTime(), coords[0], coords[1]);
        }).collect(Collectors.toList());
        return new Trajectory(trajectory.getTid(), trajectory.getOid(), points);
    }

    public RoadSegment roadSegmentTransform(RoadSegment rs) {
        List<SpatialPoint> points = rs.getPoints().stream().map(o -> {
            double[] coords = transform(o.getLng(), o.getLat());
            return new SpatialPoint(coords[0], coords[1]);
        }).collect(Collectors.toList());
        return new RoadSegment(
            rs.getRoadSegmentId(),
            rs.getStartNode().getNodeId(),
            rs.getEndNode().getNodeId(),
            points
        ).setDirection(rs.getDirection())
            .setSpeedLimit(rs.getSpeedLimit())
            .setLevel(rs.getLevel())
            .setLengthInMeter(rs.getLengthInMeter());
    }

    public RoadNetwork roadNetworkTransform(RoadNetwork rn) {
        List<RoadSegment> roadSegments = rn.getRoadSegments()
            .stream()
            .map(this::roadSegmentTransform)
            .collect(Collectors.toList());
        return new RoadNetwork(roadSegments);
    }

    protected abstract double[] transform(double lng, double lat);
}
