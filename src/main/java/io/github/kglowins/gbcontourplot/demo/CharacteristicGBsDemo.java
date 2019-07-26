package io.github.kglowins.gbcontourplot.demo;

import io.github.kglowins.gbcontourplot.ContourPlot;


import static io.github.kglowins.gbcontourplot.graphics.PlotUtils.getDashedStroke;
import static java.awt.Color.GRAY;
import static java.awt.Color.WHITE;

public class CharacteristicGBsDemo {

    public static ContourPlot createPlot() {

        return new ContourPlot(-1, 1, -1, 1)
            .setBottomMargin(20)
            .setTopMargin(20)
            .setLeftMargin(20)
            .setRightMargin(20)
            .setContourWidth(500)
            .setContourHeight(500)
            .setBackgroundAndClear(WHITE)
            //TODO .addSpots()
            //.addZones()
            .addCircularMargin()
            .addCubicAxes(GRAY, getDashedStroke(1.5f));
    }
}
