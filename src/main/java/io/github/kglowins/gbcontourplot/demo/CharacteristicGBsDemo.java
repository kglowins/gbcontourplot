package io.github.kglowins.gbcontourplot.demo;

import io.github.kglowins.gbparameters.gbcd.GbcdCharacteristicGbFinder;
import io.github.kglowins.gbparameters.gbcd.GbcdCharacteristicGbs;
import io.github.kglowins.gbparameters.gbcd.GbcdGbLocation;
import io.github.kglowins.gbcontourplot.ContourPlot;
import io.github.kglowins.gbparameters.enums.PointGroup;
import io.github.kglowins.gbparameters.representation.AxisAngle;
import io.github.kglowins.gbparameters.representation.Matrix3x3;
import io.github.kglowins.gbparameters.representation.UnitVector;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

import static io.github.kglowins.gbcontourplot.graphics.PlotUtils.getDashedStroke;
import static java.awt.Color.GRAY;
import static java.awt.Color.WHITE;
import static java.util.stream.Collectors.toList;

public class CharacteristicGBsDemo {

    public static ContourPlot createPlot() {

        UnitVector v = new UnitVector();
        v.set(0,0,1);
        AxisAngle aa = new AxisAngle();
        aa.set(v, Math.toRadians(21.0));
        Matrix3x3 M = new Matrix3x3();
        M.set(aa);

        GbcdCharacteristicGbFinder finder = new GbcdCharacteristicGbFinder();
        GbcdCharacteristicGbs characteristicGbs = finder.find(M, PointGroup._6MMM);

        List<Point2D> twistGbs = characteristicGbs.getTwist().stream().map(GbcdGbLocation::coords).collect(toList());
        List<Point2D> symmetricGbs = characteristicGbs.getSymmetric().stream().map(GbcdGbLocation::coords).collect(toList());
        List<List<Point2D>> tiltGbs = characteristicGbs.getTilt().stream().map(GbcdGbLocation::zone).collect(toList());
        List<List<Point2D>> tilt180Gbs = characteristicGbs.getTilt180().stream().map(GbcdGbLocation::zone).collect(toList());
      //  finder.find2(M, PointGroup.M3M);//
        return new ContourPlot(-1, 1, -1, 1)
                .setBottomMargin(20)
                .setTopMargin(20)
                .setLeftMargin(20)
                .setRightMargin(20)
                .setContourWidth(500)
                .setContourHeight(500)
                .setBackgroundAndClear(WHITE)
                .addHexagonalAxes(GRAY, getDashedStroke(1.5f))
                .addZones(tilt180Gbs, Color.pink, new BasicStroke(8))
                .addZones(tiltGbs, Color.gray, new BasicStroke(1.5f))
                .addSpots(twistGbs, Color.red, 12)
                .addSpots(symmetricGbs, Color.black, 25, new BasicStroke(3));
    }
}
