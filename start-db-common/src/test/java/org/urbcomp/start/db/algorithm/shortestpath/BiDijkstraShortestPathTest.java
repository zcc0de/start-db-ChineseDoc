/*
 * Copyright 2022 ST-Lab
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 */

package org.urbcomp.start.db.algorithm.shortestpath;

import org.junit.Before;
import org.junit.Test;
import org.urbcomp.start.db.exception.AlgorithmExecuteException;
import org.urbcomp.start.db.model.point.SpatialPoint;
import org.urbcomp.start.db.model.roadnetwork.Path;
import org.urbcomp.start.db.model.roadnetwork.RoadNetwork;
import org.urbcomp.start.db.model.sample.ModelGenerator;
import org.urbcomp.start.db.util.CoordTransformUtils;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class BiDijkstraShortestPathTest {

    private static BiDijkstraShortestPath algo;

    @Before
    public void setUp() {
        RoadNetwork roadNetwork = ModelGenerator.generateRoadNetwork();
        algo = new BiDijkstraShortestPath(roadNetwork);
    }

    @Test
    public void findShortestPathWithSameStartAndEndPoint() throws AlgorithmExecuteException {
        SpatialPoint startPoint = new SpatialPoint(108.88, 34.16);
        Path path = algo.findShortestPath(startPoint, startPoint);
        List<SpatialPoint> points = path.getPoints();
        assertEquals(points.size(), 2);
        assertEquals(points.get(0), points.get(1));
    }

    @Test
    public void findShortestPathInSameRoadSegment() throws AlgorithmExecuteException {
        SpatialPoint startPoint = new SpatialPoint(108.869833, 34.175913);
        SpatialPoint endPoint = new SpatialPoint(108.869933, 34.175813);
        Path path = algo.findShortestPath(startPoint, endPoint);
        assertEquals(1, path.getRoadSegmentIds().size());
    }

    @Test
    public void findShortestPathNormal() throws AlgorithmExecuteException {
        SpatialPoint startPoint = new SpatialPoint(108.869833, 34.175913);
        SpatialPoint endPoint = new SpatialPoint(108.880433, 34.172418);
        Path path = algo.findShortestPath(startPoint, endPoint);
        assertEquals(26, path.getRoadSegmentIds().size());
    }

    /**
     * for visualization
     * @param path original path in gcj02
     * @return transformed path in wgs84
     */
    private Path TransformCoordsToWGS84(Path path) {
        List<SpatialPoint> wgs84Points = path.getPoints().stream().map(o -> {
            double[] coords = CoordTransformUtils.gcj02Towgs84(o.getLng(), o.getLat());
            return new SpatialPoint(coords[0], coords[1]);
        }).collect(Collectors.toList());
        return new Path(path.getLengthInMeter(), wgs84Points, path.getRoadSegmentIds());
    }
}
