package io.github.kglowins.gbcontourplot.demo;

import io.github.kglowins.gbparameters.gbcd.GbcdSymmetries;
import io.github.kglowins.gbparameters.gbcd.GbcdSymmetriesFinder;
import io.github.kglowins.gbcontourplot.ContourPlot;
import io.github.kglowins.gbparameters.enums.PointGroup;
import io.github.kglowins.gbparameters.representation.AxisAngle;
import io.github.kglowins.gbparameters.representation.Matrix3x3;
import io.github.kglowins.gbparameters.representation.UnitVector;

import java.awt.*;

import static io.github.kglowins.gbcontourplot.graphics.PlotUtils.getDashedStroke;
import static java.awt.Color.*;

public class GbcdSymmetriesDemo {

    public static ContourPlot createPlot() {

        UnitVector v = new UnitVector();
        v.set(1,1,1);
        AxisAngle aa = new AxisAngle();
        aa.set(v, Math.toRadians(60.0));
        Matrix3x3 M = new Matrix3x3();
        M.set(aa);

        GbcdSymmetriesFinder finder = new GbcdSymmetriesFinder();
        GbcdSymmetries gbcdSymmetries = finder.find(M, PointGroup.M3M);

       return new ContourPlot(-1, 1, -1, 1)
                .setBottomMargin(20)
                .setTopMargin(20)
                .setLeftMargin(20)
                .setRightMargin(20)
                .setContourWidth(500)
                .setContourHeight(500)
                .setBackgroundAndClear(WHITE)
                .addCubicAxes(GRAY, getDashedStroke(1.5f))
                .addZones(gbcdSymmetries.mirrorLines(), new Color(0.5f,0.5f,0.5f,0.5f), new BasicStroke(2.5f))
                .addSymmetryAxes(gbcdSymmetries.symmetryAxes(), 32, new Color(0.5f,0.5f,0.5f,0.5f));
    }
}
