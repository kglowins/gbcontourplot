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
import static io.github.kglowins.gbcontourplot.graphics.PlotUtils.insideCircle;
import static io.github.kglowins.gbcontourplot.graphics.RegionCropStyle.EXCLUSIVE;
import static io.github.kglowins.gbcontourplot.graphics.RegionCropStyle.INCLUSIVE;
import static java.awt.Color.DARK_GRAY;
import static java.awt.Color.WHITE;

public class TiGbpdDemo {

    private static final String DATA_POINTS = "datapoints/Ti_gbpd.dist";

    public static JPanel createPlots() {
        JPanel gridPanel = new JPanel(new GridLayout(1, 3));

        ContourPlot subplot1 = createSubplot(ColorMapTable.PARULA.name(), 5);
        ContourPlot subplot2 = createSubplot(ColorMapTable.MAGMA.name(), 4);
        ContourPlot subplot3 = createSubplot(ColorMapTable.PLASMA.name(), 6);

        gridPanel.add(subplot1);
        gridPanel.add(subplot2);
        gridPanel.add(subplot3);

        return gridPanel;
    }

    private static ContourPlot createSubplot(String colorMapTable, int numberOfIsoLevels) {

        List<Function2DValue> dataPoints = readDataPoints(DATA_POINTS);
        Grid2DInterpolator interpolator = Grid2DInterpolator.from(dataPoints).withMaxNearest(7);
        Grid2DValues gridValues = interpolator.interpolateOnGrid(0, 1, 0, 1, 50, 50);
        ColorMapper colorMapper = new TableBasedColorMapper(colorMapTable);
        List<LineEnds> isoLines = gridValues.toIsoLines(numberOfIsoLevels);
        List<ColoredPolygon> isoBands = gridValues.toIsoBands(numberOfIsoLevels, colorMapper);

        ContourPlot contourPlot = new ContourPlot(0, 1, 0, 1)
            .setBottomMargin(100)
            .setTopMargin(20)
            .setLeftMargin(20)
            .setRightMargin(20)
            .setContourWidth(300)
            .setContourHeight(300)
            .setBackgroundAndClear(WHITE)
            .addIsoBands(isoBands, insideCircle(1), INCLUSIVE)
            .addIsoLines(isoLines, DARK_GRAY, new BasicStroke(0.5f), insideCircle(1), EXCLUSIVE)
            .addCircularMarginSST()
            .cropHexagonalSST()
            .addHexagonalSST();


        ColorBarBuilder colorBarBuilder = new ColorBarBuilder()
            .grid2DValues(gridValues)
            .setAutoIsoLevels(numberOfIsoLevels)
            .colorMapper(colorMapper)
            .colorBarLocation(BOTTOM)
            .left(20)
            .bottom(50)
            .width(300)
            .height(30)
            .font(new Font("DejaVu Sans Condensed", Font.PLAIN, 18))
            .floatingPointTemplate("%.2f")
            .barLabelSpacing(0);

        contourPlot.add(colorBarBuilder.build());

        return contourPlot;
    }
}
