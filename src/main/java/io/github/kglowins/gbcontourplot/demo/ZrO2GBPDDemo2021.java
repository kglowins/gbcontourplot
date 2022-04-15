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

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static io.github.kglowins.gbcontourplot.colorbar.ColorBarLocation.RIGHT;
import static io.github.kglowins.gbcontourplot.graphics.PlotUtils.insideCubicSST;
import static io.github.kglowins.gbcontourplot.graphics.RegionCropStyle.EXCLUSIVE;
import static io.github.kglowins.gbcontourplot.graphics.RegionCropStyle.INCLUSIVE;
import static java.awt.Color.GRAY;
import static java.awt.Color.WHITE;
import static java.lang.Math.PI;
import static java.lang.Math.tan;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class ZrO2GBPDDemo2021 {

    private static final String DATA_POINTS_PATH = "/Users/kglowins/codes/my/faryna/gbpd_2/%s";

    public static final List<String> H2_1400 = List.of(
            "08_bottom_1400C 2h pomiar 1.dist",
            "08_center_1400C 2h pomiar 1.dist",
            "08_top_1400C 2h pomiar 1.dist",
            "09_bottom_1400C 2h pomiar 2.dist",
            "09_center_1400C 2h pomiar 2.dist",
            "08-09.dist"
    );
    public static final List<String> H2_1450 = List.of(
            "10_1450C 2h pomiar 1.dist",
            "11_1450C 2h pomiar 2.dist",
            "12_1450C 2h pomiar 3.dist",
            "13_1450C 2h pomiar 4.dist",
            "10-13.dist"
    );
    public static final List<String> H2_1475 = List.of(
            "32_1475_2h_01.dist",
            "33_1475_2h_02.dist",
            "34_1475_2h_03.dist",
            "32-34.dist"
    );
    public static final List<String> H2_1500 = List.of(
            "01_ANG_1500_2h.dist",
            "02_bottom_1500C 2h pomiar 4.dist",
            "02_top_1500C 2h pomiar 4.dist",
            "03_bottom_1500C 2h pomiar 3.dist",
            "03_top_1500C 2h pomiar 3.dist",
 //           "04_1500C 2h pomiar 1.dist",
            "01-04__.dist"
    );
    public static final List<String> H2_1525 = List.of(

            "27-31.dist"
    );
    public static final List<String> H2_1550 = List.of(
            "19 - 1550 2h pomiar 1.dist",
            "20 - 1550 2h pomiar 2.dist",
            "21 - 1550 2h pomiar 3.dist",
            "19-21.dist"
    );
    public static final List<String> H20_1450 = List.of(
            "14_1450C 20h pomiar 1.dist",
            "15_bottom_1450C 20h pomiar 2.dist",
            "15_top_1450C 20h pomiar 2.dist",
            "16_bottom_1450C 20h pomiar 3.dist",
            "16_top_1450C 20h pomiar 3.dist",
            "14-16.dist"
    );
    public static final List<String> H20_1475 = List.of(
            "37b.dist",
            "37t.dist",
            "37.dist"
    );
    public static final List<String> H20_1500 = List.of(
            "17 1500 20h pomiar 2 Zirconia.dist",
            "18 1500or1550 20h pomiar 3.dist",
            "17-18-35-36.dist"
    );
    public static final List<String> H20_1525 = List.of(
            "22 - 1525 20h 01.dist",
            "23 - 1525 20h 02.dist",
            "24 - 1525 20h 03.dist",
            "25 - 1525 20h 04.dist",
            "26 - 1525 20h 05.dist",
            "22 - 26.dist"
    );
    public static final List<String> H20_1550 = List.of(
            "05__bottom_1550C 20h pomiar 1.dist",
            "05__top_1550C 20h pomiar 1.dist",
            "06__bottom_1550C 20h pomiar 2.dist",
            "06__top_1550C 20h pomiar 2.dist",
            "07__1550C 20h pomiar 3.dist",
            "05-07.dist"
    );

    public static final List<String> H20_1575 = List.of(

            "38-42.dist"
    );

    public static final List<String> H20_1600 = List.of(
            "43.dist",
            "44.dist",
            "45.dist",
            "46.dist",
             "46b.dist",
            "47.dist",
            "48.dist",
            "43-48.dist"
    );

    public static JPanel createPlotsPanel(List<String> fileNames, List<Double> labelLevels, boolean longerScale) {
        JPanel gridPanel = new JPanel(new GridLayout(2, 4));

        fileNames.forEach(fn -> {
            String filePath = String.format(DATA_POINTS_PATH, fn);
            ContourPlot subplot = createSubplot(filePath, labelLevels, longerScale);
            subplot.toRasterFile("png", String.format("/Users/kglowins/codes/my/faryna/contours/%s.png", fn));
            subplot.toEps(String.format("/Users/kglowins/codes/my/faryna/contours/%s.eps", fn));
            gridPanel.add(subplot);
        });
        return gridPanel;
    }

    private static List<Function2DValue> getDataPointsFromFile(String filePath) {
        try {
            List<Function2DValue> dataPoints = Files.lines(Paths.get(filePath))
                    .skip(5)
                    .map(line -> {
                        String[] words = line.trim().split("\\s+");
                        double x = Double.parseDouble(words[0]);
                        double y = Double.parseDouble(words[1]);
                        double f = Double.parseDouble(words[4]);
                        return Function2DValue.of(x, y, f);
                    })
                    .collect(toList());
            return dataPoints;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static ContourPlot createSubplot(String filePath, List<Double> labelLevels, boolean longerScale) {

        List<Function2DValue> dataPoints = getDataPointsFromFile(filePath);
        Grid2DInterpolator interpolator = Grid2DInterpolator.from(dataPoints).withMaxNearest(7);
        Grid2DValues gridValues = interpolator.interpolateOnGrid(0, tan(PI / 8), 0, tan(PI / 8), 100, 100);
        ColorMapper colorMapper = new JetColorMapper();
        List<Double> isoLevels = asList(0.8, 0.85, 0.9, 0.95, 1., 1.05, 1.1, 1.15, 1.2, 1.3, 1.4, 1.45, 1.6);
        List<LineEnds> isoLines = gridValues.toIsoLines(isoLevels);
        List<ColoredPolygon> isoBands = gridValues.toIsoBands(isoLevels, colorMapper, 0.75, 1.65);
        if (longerScale) {
            isoLevels = asList(0.8, 0.9, 1., 1.1, 1.2, 1.3, 1.4, 1.6, 1.8, 1.9);
            isoLines = gridValues.toIsoLines(isoLevels);
            isoBands = gridValues.toIsoBands(isoLevels, colorMapper, 0.75, 1.95);
        }

        String fp = "%.2f";
        double maxLevel = 1.65;

        if (filePath.contains("38-42.dist") ) {
            isoLevels = asList(0.8, 0.9, 1.,  1.1,  1.2, 1.3, 1.4, 1.45, 1.6);
            labelLevels =asList(0.8, 0.9 , 1.,  1.1,  1.2, 1.3, 1.4, 1.45, 1.6);
            // isoLevels=asList( 0.85,0.95,1.05,1.15);
            // labelLevels = asList( 0.85,0.95,1.05,1.15);
            fp = "%.1f";

            isoLines = gridValues.toIsoLines(isoLevels);
            isoBands = gridValues.toIsoBands(isoLevels, colorMapper, 0.75, 1.85);
        }
        if (filePath.contains("43-48.dist") ) {
            maxLevel = 1.75;
            isoLevels = asList(0.8,  0.9, 1.,   1.1,  1.2, 1.3, 1.4,  1.47);
            labelLevels =asList(0.8,  0.9, 1.,  1.1,  1.2, 1.3, 1.4,  1.5);
            // isoLevels=asList( 0.85,0.95,1.05,1.15);
            // labelLevels = asList( 0.85,0.95,1.05,1.15);
            fp = "%.1f";

            isoLines = gridValues.toIsoLines(isoLevels);
            isoBands = gridValues.toIsoBands(isoLevels, colorMapper, 0.75, 1.75);
        }


        if (filePath.contains("37.dist") ) {
            isoLevels = asList(0.8, 0.85, 0.9, 0.95, 1., 1.05,  1.1, 1.15, 1.2, 1.3, 1.4, 1.45, 1.6);
            labelLevels =asList(0.8, 0.85, 0.9, 0.95, 1., 1.05, 1.1, 1.15, 1.2, 1.3, 1.4, 1.45, 1.6);
                    // isoLevels=asList( 0.85,0.95,1.05,1.15);
           // labelLevels = asList( 0.85,0.95,1.05,1.15);
            //fp = "%.1f";

            isoLines = gridValues.toIsoLines(isoLevels);
            isoBands = gridValues.toIsoBands(isoLevels, colorMapper, 0.75, 1.85);

        }

        ContourPlot contourPlot = new ContourPlot(0, tan(PI / 8),
            0, tan(PI / 8))
            .setBottomMargin(40)
            .setTopMargin(55)
            .setLeftMargin(20)
            .setRightMargin(120)
            .setContourWidth(300)
            .setContourHeight(300)
            .setBackgroundAndClear(WHITE)
            .addIsoBands(isoBands, insideCubicSST(), INCLUSIVE)
            .addIsoLines(isoLines, GRAY, new BasicStroke(0.5f), insideCubicSST(), EXCLUSIVE)
            .cropCubicSST()
            .addCubicSST();

        if (longerScale) {
            maxLevel = 1.95;
        }

        if (filePath.contains("26 - 1525 20h 05.dist") ) {
            labelLevels = asList(0.8, 0.9, 1., 1.1, 1.2, 1.3);
            fp = "%.1f";
        }
        if (filePath.contains("17-18-35-36.dist")) {
            maxLevel = 1.75;
            labelLevels = asList(0.9, 1., 1.1, 1.2, 1.3, 1.4, 1.45);
            fp = "%.1f";
        }
        if (filePath.contains("05-07.dist")) {
            fp = "%.1f";
        }


        ColorBarBuilder colorBarBuilder = new ColorBarBuilder()
                .grid2DValues(gridValues)
                .isoLevels(isoLevels)
                .labelLevels(labelLevels)
                .setRange(0.75, maxLevel)
                .colorMapper(colorMapper)
                .colorBarLocation(RIGHT)
                .left(340)
                .bottom(40)
                .width(30)
                .height(300)
                .font(new Font("Times New Roman", Font.PLAIN, 28))
                .floatingPointTemplate(fp)
                .barLabelSpacing(10)
                .continuous(true)
                .barBorderWidth(2f);

        contourPlot.add(colorBarBuilder.build());
        contourPlot.add(g -> {
            g.setFont(new Font("Times New Roman", Font.PLAIN, 28));
            g.scale(1, -1);
            g.drawString("[MRD]", 338, -355);
        });

        if (filePath.contains("22 - 1525 20h 01.dist") || filePath.contains("08-09.dist")) {
            contourPlot.add(g -> {
         //       g.setFont(new Font("Times New Roman", Font.PLAIN, 28));
              //  g.scale(1, -1);
                g.drawString("(111)", 257, -313);
                g.drawString("(001)", 3, -10);
                g.drawString("(101)", 280, -10);
            });
        }
        return contourPlot;
    }
}
