package io.github.kglowins.gbcontourplot.demo;

import io.github.kglowins.gbcontourplot.ContourPlot;
import io.github.kglowins.gbcontourplot.colorbar.ColorBarBuilder;
import io.github.kglowins.gbcontourplot.colormappers.ColorMapTable;
import io.github.kglowins.gbcontourplot.colormappers.ColorMapper;
import io.github.kglowins.gbcontourplot.colormappers.TableBasedColorMapper;
import io.github.kglowins.gbcontourplot.graphics.ColoredPolygon;
import io.github.kglowins.gbcontourplot.graphics.LineEnds;
import io.github.kglowins.gbcontourplot.grid.Function2DValue;
import io.github.kglowins.gbcontourplot.grid.Grid2DInterpolator;
import io.github.kglowins.gbcontourplot.grid.Grid2DValues;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import static io.github.kglowins.gbcontourplot.colorbar.ColorBarLocation.BOTTOM;
import static io.github.kglowins.gbcontourplot.demo.DataPointsUtils.readDataPoints;
import static io.github.kglowins.gbcontourplot.graphics.PlotUtils.getDashedStroke;
import static io.github.kglowins.gbcontourplot.graphics.PlotUtils.insideCircle;
import static io.github.kglowins.gbcontourplot.graphics.RegionCropStyle.EXCLUSIVE;
import static io.github.kglowins.gbcontourplot.graphics.RegionCropStyle.INCLUSIVE;
import static java.awt.Color.BLACK;
import static java.awt.Color.DARK_GRAY;
import static java.awt.Color.WHITE;
import static java.util.Arrays.asList;

public class TiGbcdDemo {

    private static final String DATA_POINTS_PATH = "datapoints/Ti_gbcd%s.dist";
    private static final String DISTRIBUTION_DATA = String.format(DATA_POINTS_PATH, "");
    private static final String ERROR_DATA = String.format(DATA_POINTS_PATH, "_error");

    public static JPanel createPlots() {
        JPanel gridPanel = new JPanel(new GridLayout(1, 2));

        ContourPlot subplot1 = createDistributionPlot();
        ContourPlot subplot2 = createErrorPlot();

        gridPanel.add(subplot1);
        gridPanel.add(subplot2);

        return gridPanel;
    }

    private static ContourPlot createDistributionPlot() {

        List<Function2DValue> dataPoints = readDataPoints(DISTRIBUTION_DATA);
        Grid2DInterpolator interpolator = Grid2DInterpolator.from(dataPoints).withMaxNearest(7);
        Grid2DValues gridValues = interpolator.interpolateOnGrid(-1, 1, -1, 1, 100, 100);
        ColorMapper colorMapper = new TableBasedColorMapper(ColorMapTable.DARK_RAINBOW.name());
        List<Double> isoLevels = asList(3.2, 4.2, 5.2, 6., 6.5, 7.4);
        List<LineEnds> isoLines = gridValues.toIsoLines(isoLevels);
        List<ColoredPolygon> isoBands = gridValues.toIsoBands(isoLevels, colorMapper);

        ContourPlot contourPlot = new ContourPlot(-1, 1, -1, 1)
            .setBottomMargin(100)
            .setTopMargin(20)
            .setLeftMargin(20)
            .setRightMargin(20)
            .setContourWidth(500)
            .setContourHeight(500)
            .setBackgroundAndClear(WHITE)
            .addIsoBands(isoBands, insideCircle(1), INCLUSIVE)
            .addIsoLines(isoLines, DARK_GRAY, new BasicStroke(0.5f), insideCircle(1), EXCLUSIVE)
            .addCircularMargin()
            .addDashedCircumference()
            .addHexagonalAxes(BLACK, getDashedStroke(1.5f));

        ColorBarBuilder colorBarBuilder = new ColorBarBuilder()
            .grid2DValues(gridValues)
            .isoLevels(isoLevels)
            .setAutoRange()
            .labelLevels(asList(3., 4., 5., 6., 7.))
            .colorMapper(colorMapper)
            .colorBarLocation(BOTTOM)
            .left(20)
            .bottom(50)
            .width(500)
            .height(30)
            .font(new Font("DejaVu Sans Condensed", Font.PLAIN, 32))
            .floatingPointTemplate("%.0f")
            .barLabelSpacing(0)
            .continuous(true);

        contourPlot.add(colorBarBuilder.build());

        return contourPlot;
    }


    private static ContourPlot createErrorPlot() {

        List<Function2DValue> dataPoints = readDataPoints(ERROR_DATA);
        Grid2DInterpolator interpolator = Grid2DInterpolator.from(dataPoints).withMaxNearest(7);
        Grid2DValues gridValues = interpolator.interpolateOnGrid(-1, 1, -1, 1, 100, 100);
        ColorMapper colorMapper = new TableBasedColorMapper(ColorMapTable.GRAY_YELLOW.name());
        List<LineEnds> isoLines = gridValues.toIsoLines(4);
        List<ColoredPolygon> isoBands = gridValues.toIsoBands(4, colorMapper);

        ContourPlot contourPlot = new ContourPlot(-1, 1, -1, 1)
            .setBottomMargin(100)
            .setTopMargin(20)
            .setLeftMargin(20)
            .setRightMargin(20)
            .setContourWidth(400)
            .setContourHeight(400)
            .setBackgroundAndClear(WHITE)
            .addIsoBands(isoBands, insideCircle(1), INCLUSIVE)
            .addIsoLines(isoLines, DARK_GRAY, new BasicStroke(0.5f), insideCircle(1), EXCLUSIVE)
            .addCircularMargin()
            .addDashedCircumference()
            .addHexagonalAxes(BLACK, getDashedStroke(1.5f));

        ColorBarBuilder colorBarBuilder = new ColorBarBuilder()
            .grid2DValues(gridValues)
            .setAutoIsoLevels(4)
            .labelLevels(asList(0.5, 0.6, 0.7, 0.8))
            .colorMapper(colorMapper)
            .colorBarLocation(BOTTOM)
            .left(20)
            .bottom(50)
            .width(400)
            .height(30)
            .font(new Font("DejaVu Sans Condensed", Font.PLAIN, 32))
            .floatingPointTemplate("%.1f")
            .barLabelSpacing(0)
            .continuous(true);

        contourPlot.add(colorBarBuilder.build());

        return contourPlot;
    }
}
