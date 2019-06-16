package io.github.kglowins.gbcontourplot.graphics;

import io.github.kglowins.gbcontourplot.colormappers.ColorMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.FastMath;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.function.BiFunction;

@Slf4j
public class PlotUtils {

    private PlotUtils() {
    }

    public static Color getIsoBandColor(ColorMapper colorMapper, List<Double> scaledLevels, Integer index) {
        double middle = 0.5 * (scaledLevels.get(index) + scaledLevels.get(index + 1));
        log.debug("{} {}", middle, (1 - middle) * scaledLevels.get(index) + middle * scaledLevels.get(index + 1) );
        return colorMapper.map(middle);
        //return colorMapper.map(
          //  (1 - middle) * scaledLevels.get(index) + middle * scaledLevels.get(index + 1));
    }

    public static BiFunction<Double, Double, Boolean> insideCircle(double r) {
        return (x, y) -> x * x + y * y <= r * r;
    }

    public static BiFunction<Double, Double, Boolean> insideCubicSST() {
        return (x, y) -> {
            double cosOfAtan = FastMath.cos(FastMath.atan2(y, x));
            return x <= cosOfAtan * FastMath.tan(0.5 * FastMath.atan(1 / cosOfAtan))
                && y > 0 && y <= x;
        };
    }

    public static BiFunction<Double, Double, Boolean> insideHexagonalSST() {
        return (x, y) -> true;
    }

    //TODO copied from gbtoolbox-legacy
    public static void drawCurve(Graphics2D g2d, Point2D[] points) {
        int numberOfPoints = points.length;
        Path2D curve = new Path2D.Double();
        int index = 0;
        if (numberOfPoints > 0) {
            curve.moveTo(points[index].getX(), points[index].getY());

            while (index + 3 < numberOfPoints) {
                curve.curveTo(
                    points[index + 1].getX(), points[index + 1].getY(),
                    points[index + 2].getX(), points[index + 2].getY(),
                    points[index + 3].getX(), points[index + 3].getY());
                index += 3;
            }

            if (index == numberOfPoints - 2) {
                curve.lineTo(points[index + 1].getX(), points[index + 1].getY());
            } else if (index == numberOfPoints - 3) {
                curve.quadTo(
                    points[index + 1].getX(), points[index + 1].getY(),
                    points[index + 2].getX(), points[index + 2].getY());
            }
            g2d.draw(curve);
        }
    }
}
