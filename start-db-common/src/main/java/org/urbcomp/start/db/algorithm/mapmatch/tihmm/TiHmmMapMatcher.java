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

package org.urbcomp.start.db.algorithm.mapmatch.tihmm;

import org.urbcomp.start.db.algorithm.mapmatch.tihmm.inner.HmmProbabilities;
import org.urbcomp.start.db.algorithm.mapmatch.tihmm.inner.SequenceState;
import org.urbcomp.start.db.algorithm.mapmatch.tihmm.inner.TiViterbi;
import org.urbcomp.start.db.algorithm.mapmatch.tihmm.inner.TimeStep;
import org.urbcomp.start.db.algorithm.shortestpath.AbstractShortestPath;
import org.urbcomp.start.db.exception.AlgorithmExecuteException;
import org.urbcomp.start.db.model.point.CandidatePoint;
import org.urbcomp.start.db.model.point.GPSPoint;
import org.urbcomp.start.db.model.point.MapMatchedPoint;
import org.urbcomp.start.db.model.roadnetwork.Path;
import org.urbcomp.start.db.model.roadnetwork.RoadNetwork;
import org.urbcomp.start.db.model.trajectory.MapMatchedTrajectory;
import org.urbcomp.start.db.model.trajectory.Trajectory;
import org.urbcomp.start.db.util.GeoFunctions;

import java.util.ArrayList;
import java.util.List;

public class TiHmmMapMatcher {
    /**
     * emission P用正态分布函数来模拟，sigma为正态分布的概率函数参数
     */
    private static final double measurementErrorSigma = 50.0;
    /**
     * transition p 的指数概率函数参数
     */
    private static final double transitionProbabilityBeta = 2.0;

    /**
     * 路网
     */
    protected final RoadNetwork roadNetwork;

    protected final AbstractShortestPath pathAlgo;

    public TiHmmMapMatcher(RoadNetwork roadNetwork, AbstractShortestPath pathAlgo) {
        this.roadNetwork = roadNetwork;
        this.pathAlgo = pathAlgo;
    }

    /**
     * 实现抽象类的map match 方法
     *
     * @param traj 原始轨迹
     * @return map match后的轨迹
     */
    public MapMatchedTrajectory mapMatch(Trajectory traj) throws AlgorithmExecuteException {
        List<SequenceState> seq = this.computeViterbiSequence(traj.getGPSPointList());
        assert traj.getGPSPointList().size() == seq.size();
        List<MapMatchedPoint> mapMatchedPointList = new ArrayList<>(seq.size());
        for (SequenceState ss : seq) {
            CandidatePoint candiPt = null;
            if (ss.getState() != null) {
                candiPt = ss.getState();
            }
            mapMatchedPointList.add(new MapMatchedPoint(ss.getObservation(), candiPt));
        }
        return new MapMatchedTrajectory(traj.getTid(), traj.getOid(), mapMatchedPointList);
    }

    /**
     * 建立一个time step
     *
     * @param pt 原始轨迹点
     * @return timestep
     */
    private TimeStep createTimeStep(GPSPoint pt) {
        TimeStep timeStep = null;
        List<CandidatePoint> candidates = CandidatePoint.getCandidatePoint(
            pt,
            roadNetwork,
            measurementErrorSigma
        );
        if (!candidates.isEmpty()) {
            timeStep = new TimeStep(pt, candidates);
        }
        return timeStep;
    }

    /**
     * 计算一个 Viterbi sequence
     *
     * @param ptList 原始轨迹ptList
     * @return 保存了每一步step的所有状态
     */
    private List<SequenceState> computeViterbiSequence(List<GPSPoint> ptList)
        throws AlgorithmExecuteException {
        List<SequenceState> seq = new ArrayList<>();
        final HmmProbabilities probabilities = new HmmProbabilities(
            measurementErrorSigma,
            transitionProbabilityBeta
        );
        TiViterbi viterbi = new TiViterbi();
        TimeStep preTimeStep = null;
        int idx = 0;
        int nbPoints = ptList.size();
        while (idx < nbPoints) {
            TimeStep timeStep = this.createTimeStep(ptList.get(idx));
            if (timeStep == null) {
                seq.addAll(viterbi.computeMostLikelySequence());
                seq.add(new SequenceState(null, ptList.get(idx)));
                viterbi = new TiViterbi();
                preTimeStep = null;
            } else {
                this.computeEmissionProbabilities(timeStep, probabilities);
                if (preTimeStep == null) {
                    viterbi.startWithInitialObservation(
                        timeStep.getObservation(),
                        timeStep.getCandidates(),
                        timeStep.getEmissionLogProbabilities()
                    );
                } else {
                    this.computeTransitionProbabilities(preTimeStep, timeStep, probabilities);
                    viterbi.nextStep(
                        timeStep.getObservation(),
                        timeStep.getCandidates(),
                        timeStep.getEmissionLogProbabilities(),
                        timeStep.getTransitionLogProbabilities()
                    );
                }
                if (viterbi.isBroken) {
                    seq.addAll(viterbi.computeMostLikelySequence());
                    viterbi = new TiViterbi();
                    viterbi.startWithInitialObservation(
                        timeStep.getObservation(),
                        timeStep.getCandidates(),
                        timeStep.getEmissionLogProbabilities()
                    );
                }
                preTimeStep = timeStep;
            }
            idx += 1;
        }
        if (seq.size() < nbPoints) {
            seq.addAll(viterbi.computeMostLikelySequence());
        }
        return seq;
    }

    /**
     * 根据time step和概率分布函数计算emission P
     *
     * @param timeStep    timeStep
     * @param probability 建立好的概率分布函数
     */
    private void computeEmissionProbabilities(TimeStep timeStep, HmmProbabilities probability) {
        for (CandidatePoint candiPt : timeStep.getCandidates()) {
            final double dist = candiPt.getErrorDistanceInMeter();
            timeStep.addEmissionLogProbability(candiPt, probability.emissionLogProbability(dist));
        }
    }

    /**
     * 计算之前timeStep到当前timeStep的概率
     *
     * @param prevTimeStep  之前的timestep
     * @param timeStep      当前的timestep
     * @param probabilities 建立好的概率分布函数
     */
    protected void computeTransitionProbabilities(
        TimeStep prevTimeStep,
        TimeStep timeStep,
        HmmProbabilities probabilities
    ) throws AlgorithmExecuteException {
        final double linearDist = GeoFunctions.getDistanceInM(
            prevTimeStep.getObservation(),
            timeStep.getObservation()
        );
        for (CandidatePoint preCandiPt : prevTimeStep.getCandidates()) {
            for (CandidatePoint curCandiPt : timeStep.getCandidates()) {
                Path path = pathAlgo.findShortestPath(preCandiPt, curCandiPt);
                if (path.getLengthInMeter() != Double.MAX_VALUE) {
                    timeStep.addTransitionLogProbability(
                        preCandiPt,
                        curCandiPt,
                        probabilities.transitionLogProbability(path.getLengthInMeter(), linearDist)
                    );
                }
            }
        }
    }
}
