package io.github.kglowins.gbcontourplot.demo;

import io.github.kglowins.gbcontourplot.ContourPlot;
import io.github.kglowins.gbcontourplot.colorbar.ColorBarBuilder;
import io.github.kglowins.gbcontourplot.colormappers.ColorMapper;
import io.github.kglowins.gbcontourplot.colormappers.JetColorMapper;
import io.github.kglowins.gbcontourplot.graphics.ColoredPolygon;
import io.github.kglowins.gbcontourplot.graphics.LineEnds;
import io.github.kglowins.gbcontourplot.grid.Function2DValue;
import io.github.kglowins.gbcontourplot.grid.Grid2DInterpolator;
import io.github.kglowins.gbcontourplot.grid.Grid2DValues;
import org.apache.commons.math3.util.FastMath;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import static io.github.kglowins.gbcontourplot.colorbar.ColorBarLocation.RIGHT;
import static io.github.kglowins.gbcontourplot.demo.DataPointsUtils.readDataPoints;
import static io.github.kglowins.gbcontourplot.graphics.PlotUtils.insideCubicSST;
import static io.github.kglowins.gbcontourplot.graphics.RegionCropStyle.EXCLUSIVE;
import static io.github.kglowins.gbcontourplot.graphics.RegionCropStyle.INCLUSIVE;
import static java.awt.Color.GRAY;
import static java.awt.Color.WHITE;
import static java.util.Arrays.asList;

public class ZrO2DemoProvider {

    private static final String DATA_POINTS_PATH = "datapoints/ZrO2_%s.dist";
    private static final String ZRO2_1450_1 = String.format(DATA_POINTS_PATH, "1450_1");
    private static final String ZRO2_1450_2 = String.format(DATA_POINTS_PATH, "1450_2");
    private static final String ZRO2_1450_3 = String.format(DATA_POINTS_PATH, "1450_3");
    private static final String ZRO2_1450_COMBINED = String.format(DATA_POINTS_PATH, "1450_combined");
    private static final String ZRO2_1500_1 = String.format(DATA_POINTS_PATH, "1500_1");
    private static final String ZRO2_1500_2 = String.format(DATA_POINTS_PATH, "1500_2");
    private static final String ZRO2_1500_3 = String.format(DATA_POINTS_PATH, "1500_3");
    private static final String ZRO2_1500_COMBINED = String.format(DATA_POINTS_PATH, "1500_combined");

    public static JPanel createSubplotsGrid() {
        JPanel gridPanel = new JPanel(new GridLayout(2, 4));

        ContourPlot subplot1 = createSubplot(ZRO2_1450_1);
        ContourPlot subplot2 = createSubplot(ZRO2_1450_2);
        ContourPlot subplot3 = createSubplot(ZRO2_1450_3);
        ContourPlot subplot4 = createSubplot(ZRO2_1450_COMBINED);
        ContourPlot subplot5 = createSubplot(ZRO2_1500_1);
        ContourPlot subplot6 = createSubplot(ZRO2_1500_2);
        ContourPlot subplot7 = createSubplot(ZRO2_1500_3);
        ContourPlot subplot8 = createSubplot(ZRO2_1500_COMBINED);

        gridPanel.add(subplot1);
        gridPanel.add(subplot2);
        gridPanel.add(subplot3);
        gridPanel.add(subplot4);
        gridPanel.add(subplot5);
        gridPanel.add(subplot6);
        gridPanel.add(subplot7);
        gridPanel.add(subplot8);

        return gridPanel;
    }

    private static ContourPlot createSubplot(String resourceName) {

        List<Function2DValue> dataPoints = readDataPoints(resourceName);
        Grid2DInterpolator interpolator = Grid2DInterpolator.from(dataPoints).withMaxNearest(7);
        Grid2DValues gridValues = interpolator.interpolateOnGrid(0, FastMath.tan(FastMath.PI / 8), 0, FastMath.tan(FastMath.PI / 8), 100, 100);
        ColorMapper colorMapper = new JetColorMapper();
        List<Double> isoLevels = asList(0.88, 0.95, 1.02, 1.09, 1.16, 1.23);
        List<LineEnds> isoLines = gridValues.toIsoLines(isoLevels);
        List<ColoredPolygon> isoBands = gridValues.toIsoBands(isoLevels, colorMapper, 0.859, 1.273);

        ContourPlot contourPlot = new ContourPlot(0, FastMath.tan(FastMath.PI / 8),
            0, FastMath.tan(FastMath.PI / 8))
            .setBottomMargin(10)
            .setTopMargin(10)
            .setLeftMargin(10)
            .setRightMargin(85)
            .setContourWidth(200)
            .setContourHeight(200)
            .setBackgroundAndClear(WHITE)
            .addIsoBands(isoBands, insideCubicSST(), INCLUSIVE)
            .addIsoLines(isoLines, GRAY, new BasicStroke(0.5f), insideCubicSST(), EXCLUSIVE)
            .cropCubicSST()
            .addCubicSST();

        ColorBarBuilder colorBarBuilder = new ColorBarBuilder()
            .grid2DValues(gridValues)
            .isoLevels(isoLevels)
            .setRange(0.859, 1.273)
            .colorMapper(colorMapper)
            .colorBarLocation(RIGHT)
            .left(225)
            .bottom(10)
            .width(20)
            .height(200)
            .font(new Font("DejaVu Sans Condensed", Font.PLAIN, 18))
            .floatingPointTemplate("%.2f")
            .barLabelSpacing(5);

        contourPlot.add(colorBarBuilder.build());

        return contourPlot;
    }
}
