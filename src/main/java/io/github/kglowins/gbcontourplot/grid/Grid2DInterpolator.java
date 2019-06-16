package io.github.kglowins.gbcontourplot.grid;

import lombok.extern.slf4j.Slf4j;
import net.sf.javaml.core.kdtree.KDTree;
import net.sf.javaml.core.kdtree.KeyDuplicateException;
import net.sf.javaml.core.kdtree.KeySizeException;

import java.time.Instant;
import java.util.Collection;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.IntStream.rangeClosed;

@Slf4j
public class Grid2DInterpolator {

    private static final double EPS_TO_AVOID_DIV_BY_0 = 1e-10;

    private int maxNearest = 6;
    private KDTree kdTree;

    private Grid2DInterpolator(Collection<Function2DValue> dataPoints) {
        if (isNull(dataPoints) || dataPoints.isEmpty()) {
            throw new IllegalArgumentException("dataPoints isEmpty or null");
        }
        logDataPointsRanges(dataPoints);
        kdTree = createKdTree(dataPoints);
    }

    private void logDataPointsRanges(Collection<Function2DValue> dataPoints) {
        double xMin = dataPoints.stream().mapToDouble(Function2DValue::x).min().getAsDouble();
        double xMax = dataPoints.stream().mapToDouble(Function2DValue::x).max().getAsDouble();
        double yMin = dataPoints.stream().mapToDouble(Function2DValue::y).min().getAsDouble();
        double yMax = dataPoints.stream().mapToDouble(Function2DValue::y).max().getAsDouble();
        double fMin = dataPoints.stream().mapToDouble(Function2DValue::f).min().getAsDouble();
        double fMax = dataPoints.stream().mapToDouble(Function2DValue::f).max().getAsDouble();
        log.debug("dataPoints ranges: xMin = {}, xMax = {}, yMin = {}, yMax = {}, fMin = {}, fMax = {}",
                xMin, xMax, yMin, yMax, fMin, fMax);
    }

    public static Grid2DInterpolator from(Collection<Function2DValue> dataPoints) {
        return new Grid2DInterpolator(dataPoints);
    }

    public Grid2DInterpolator withMaxNearest(int maxNearest) {
        this.maxNearest = maxNearest;
        return this;
    }

    public Grid2DValues interpolateOnGrid(double xMin, double xMax,
                                          double yMin, double yMax,
                                          int xCells, int yCells) {

        validateGridParams(xMin, xMax, yMin, yMax, xCells, yCells);

        long startMilli = Instant.now().toEpochMilli();

        double[][] values = new double[1 + xCells][1 + yCells];
        double xCell = (xMax - xMin) / (double) xCells;
        double yCell = (yMax - yMin) / (double) yCells;
        rangeClosed(0, xCells).forEach(xVertex -> {
            double xCoord = xMin + xCell * (double) xVertex;
            rangeClosed(0, yCells).forEach(yVertex -> {
                double yCoord = yMin + yCell * (double) yVertex;
                values[xVertex][yVertex] = interpolateAt(xCoord, yCoord);
            });
        });

        long finishMilli = Instant.now().toEpochMilli();
        long elapsedMilli = finishMilli - startMilli;
        log.debug("interpolateOnGrid took {} millis.", elapsedMilli);

        return new Grid2DValues(xMin, yMin, xCell, yCell, values);
    }

    private static void validateGridParams(double xMin, double xMax, double yMin, double yMax, int xCells, int yCells) {
        if (xMin > xMax) {
            throw new IllegalArgumentException("xMin > xMax");
        }
        if (yMin > yMax) {
            throw new IllegalArgumentException("yMin > yMax");
        }
        if (xCells < 1) {
            throw new IllegalArgumentException("xCells < 1");
        }
        if (yCells < 1) {
            throw new IllegalArgumentException("yCells < 1");
        }
    }

    public double interpolateAt(double x, double y) {
        try {
            Object[] nearest = kdTree.nearest(key2D(x, y), maxNearest);
            double nominator = 0;
            double denominator = 0;

            for (Object o : nearest) {
                if (nonNull(o)) {
                    Function2DValue nearDataPoint = (Function2DValue) o;
                    double distanceSq = distanceSq(x, y, nearDataPoint);
                    double inverseDistanceSq = 1 / (distanceSq + EPS_TO_AVOID_DIV_BY_0);

                    nominator += inverseDistanceSq * nearDataPoint.f();
                    denominator += inverseDistanceSq;
                }
            }
            return nominator / denominator;

        } catch (KeySizeException e) {
            log.error("Error when estimating at ({}, {})", x, y, e);
            throw new IllegalArgumentException(e);
        }
    }

    private KDTree createKdTree(Collection<Function2DValue> dataPoints) {
        KDTree kdTree = new KDTree(2);
        dataPoints.forEach(dataPoint -> {
            try {
                kdTree.insert(key2D(dataPoint.x(), dataPoint.y()), dataPoint);
            } catch (KeySizeException | KeyDuplicateException e) {
                log.error("Failed to insert {} to kdTree", dataPoint, e);
            }
        });
        return kdTree;
    }

    private double[] key2D(double x, double y) {
        return new double[]{x, y};
    }

    private double distanceSq(double x, double y, Function2DValue nearDataPoint)
    {
        double dx = nearDataPoint.x() - x;
        double dy = nearDataPoint.y() - y;
        return dx * dx + dy * dy;
    }
}
