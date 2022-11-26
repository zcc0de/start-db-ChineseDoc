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

import org.urbcomp.start.db.model.roadnetwork.RoadNetwork;
import org.urbcomp.start.db.util.GeoFunctions;

public class AStarShortestPath extends AbstractShortestPath {
    public AStarShortestPath(RoadNetwork roadNetwork, double searchDistance) {
        super(
            roadNetwork,
            searchDistance,
            new org.jgrapht.alg.shortestpath.AStarShortestPath<>(
                roadNetwork.getDirectedRoadGraph(),
                GeoFunctions::getDistanceInM
            )
        );
    }

    public AStarShortestPath(RoadNetwork roadNetwork) {
        super(
            roadNetwork,
            new org.jgrapht.alg.shortestpath.AStarShortestPath<>(
                roadNetwork.getDirectedRoadGraph(),
                GeoFunctions::getDistanceInM
            )
        );
    }
}
