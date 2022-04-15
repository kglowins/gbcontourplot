package io.github.kglowins.gbcontourplot.demo;

import io.github.kglowins.gbcontourplot.ContourPlot;
import io.github.kglowins.gbcontourplot.colorbar.ColorBarBuilder;
import io.github.kglowins.gbcontourplot.colormappers.ColorMapTable;
import io.github.kglowins.gbcontourplot.colormappers.ColorMapper;
import io.github.kglowins.gbcontourplot.colormappers.JetColorMapper;
import io.github.kglowins.gbcontourplot.colormappers.TableBasedColorMapper;
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

import static io.github.kglowins.gbcontourplot.colorbar.ColorBarLocation.BOTTOM;
import static io.github.kglowins.gbcontourplot.colorbar.ColorBarLocation.RIGHT;
import static io.github.kglowins.gbcontourplot.demo.DataPointsUtils.readDataPoints;
import static io.github.kglowins.gbcontourplot.graphics.PlotUtils.*;
import static io.github.kglowins.gbcontourplot.graphics.RegionCropStyle.EXCLUSIVE;
import static io.github.kglowins.gbcontourplot.graphics.RegionCropStyle.INCLUSIVE;
import static java.awt.Color.*;
import static java.awt.Color.BLACK;
import static java.lang.Math.PI;
import static java.lang.Math.tan;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class ZrO2GBCDDemo2021 {

    private static final String DATA_POINTS_PATH = "/Users/kglowins/codes/my/faryna/gbcd/%s/%s";

    //samp08-09_res_3_7
    public static final List<String> H2_1400 = List.of(
            "sigma25a.dist",
            "sigma13a.dist",
            "sigma17a.dist",
            "sigma5.dist",
         //   "sigma29a.dist",
            "sigma3.dist",
            "sigma11.dist",

            "_s9.dist",
            "s19.dist",
            "s27a.dist",
       //     "110_20.dist",

            "s33c.dist"

    );


    public static final List<String> H2_1400_ERR = List.of(
            "sigma25a_err.dist",
            "sigma13a_err.dist",
            "sigma17a_err.dist",
            "sigma5_err.dist",
         //   "sigma29a_err.dist",
            "sigma3_err.dist",
            "sigma11_err.dist",

            "_s9_err.dist",
            "s19_err.dist",
            "s27a_err.dist",

        //    "110_20_err.dist"

            "s33c_err.dist"
    );

    public static final List<String> H2_1450 = List.of(
            "s25a.dist",
            "s13a.dist",
            "s17a.dist",
            "s5.dist",
            "s3.dist",
            "s11.dist"
    );

    public static final List<String> H2_1450_ERR = List.of(
            "s25a_err.dist",
            "s13a_err.dist",
            "s17a_err.dist",
            "s5_err.dist",
            "s3_err.dist",
            "s11_err.dist"

    );

    public static final List<String> H20_1450 = List.of(
          //  "s25a.dist",
            "s13a.dist",
            "s17a.dist",
            "s5.dist",
            "s3.dist",
            "s11.dist"
    );

    public static final List<String> H2_1400_8B = List.of(
            //"s25a.dist",
         //   "s13a.dist",
            "s17a.dist",
          //  "s5.dist",
          //  "s3.dist",
            "s11.dist"
    );
    public static final List<String> H2_1400_8C = List.of(
       //     "s25a.dist",
        //    "s13a.dist",
            "s17a.dist",
        //    "s5.dist",
         //   "s3.dist",
            "s11.dist"
    );
    public static final List<String> H2_1400_9B = List.of(
           // "s25a.dist",
        //    "s13a.dist",
            "s17a.dist",
          //  "s5.dist",
         //   "s3.dist",
            "s11.dist"
    );

    public static final List<String> H2_1400_8B_ERR = List.of(
       //     "s25a_err.dist",
        //    "s13a_err.dist",
            "s17a_err.dist",
         //   "s5_err.dist",
        //    "s3_err.dist",
            "s11_err.dist"
    );
    public static final List<String> H2_1400_8C_ERR = List.of(
        //    "s25a_err.dist",
        //    "s13a_err.dist",
            "s17a_err.dist",
        //    "s5_err.dist",
        //    "s3_err.dist",
            "s11_err.dist"
    );
    public static final List<String> H2_1400_9B_ERR = List.of(
         //   "s25a_err.dist",
         //   "s13a_err.dist",
            "s17a_err.dist",
        //    "s5_err.dist",
        //    "s3_err.dist",
            "s11_err.dist"
    );

    public static final List<String> H20_1450_ERR = List.of(
          //  "s25a_err.dist",
            "s13a_err.dist",
            "s17a_err.dist",
            "s5_err.dist",
            "s3_err.dist",
            "s11_err.dist"

    );

    public static JPanel createPlotsPanel(int startIdx, int endIdx, String dir, List<String> distributionFiles,
                                          List<String> errorFiles,
                                          List<Double> labelLevels) {
        JPanel gridPanel = new JPanel(new GridLayout(2, 4));
        for (int idx = startIdx; idx < endIdx; idx++) {
            String distributionPath = String.format(DATA_POINTS_PATH, dir, distributionFiles.get(idx));
            String errorPath = String.format(DATA_POINTS_PATH, dir, errorFiles.get(idx));
            ContourPlot subplot = createDistributionPlot(distributionPath, labelLevels);
            ContourPlot subplot2 = createErrorPlot(errorPath, labelLevels);

            subplot.toRasterFile("png", String.format("/Users/kglowins/codes/my/faryna/contours/%s_%s.png", dir, distributionFiles.get(idx)));
            subplot.toEps(String.format("/Users/kglowins/codes/my/faryna/contours/%s_%s.eps", dir, distributionFiles.get(idx)));

            subplot2.toRasterFile("png", String.format("/Users/kglowins/codes/my/faryna/contours/%s_%s.png", dir, errorFiles.get(idx)));
            subplot2.toEps(String.format("/Users/kglowins/codes/my/faryna/contours/%s_%s.eps", dir, errorFiles.get(idx)));

            gridPanel.add(subplot);
            gridPanel.add(subplot2);
        }
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

    private static ContourPlot createDistributionPlot(String filePath, List<Double> labelLevels) {

        List<Function2DValue> dataPoints = getDataPointsFromFile(filePath);
        Grid2DInterpolator interpolator = Grid2DInterpolator.from(dataPoints).withMaxNearest(7);
        Grid2DValues gridValues = interpolator.interpolateOnGrid(-1, 1, -1, 1, 100, 100);
        ColorMapper colorMapper = new JetColorMapper();
        List<Double> isoLevels = asList(0.66,1.,3.,5.,7.,10.,13.,16.,20.,25.,30.);
        List<LineEnds> isoLines = gridValues.toIsoLines(6);
        List<ColoredPolygon> isoBands = gridValues.toIsoBands(6, colorMapper);

        ContourPlot contourPlot = new ContourPlot(-1, 1, -1, 1)
                .setBottomMargin(75)
                .setTopMargin(20)
                .setLeftMargin(20)
                .setRightMargin(20)
                .setContourWidth(270)
                .setContourHeight(270)
                .setBackgroundAndClear(WHITE)
                .addIsoBands(isoBands, insideCircle(1), INCLUSIVE)
                .addIsoLines(isoLines, DARK_GRAY, new BasicStroke(0.5f), insideCircle(1), EXCLUSIVE)
                .addCircularMargin()
                .addDashedCircumference()
                .addCubicAxes(BLACK, getDashedStroke(1.5f));

        ColorBarBuilder colorBarBuilder = new ColorBarBuilder()
                .grid2DValues(gridValues)
                .setAutoIsoLevels(6)//.isoLevels(isoLevels)
              //  .setRange(0.2,32.02)
                //.labelLevels(asList(3., 4., 5., 6., 7.))
                .colorMapper(colorMapper)
                .colorBarLocation(BOTTOM)
                .left(20)
                .bottom(40)
                .width(270)
                .height(25)
                .font(new Font("Times New Roman", Font.PLAIN, 28))
                .floatingPointTemplate("%.1f")
                .continuous(true)
                .barLabelSpacing(0);
        if (filePath.contains("samp08-09") && filePath.contains("s33c.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.5d,1d,1.5d,2d,2.5d,3d))
                    .floatingPointTemplate("%.1f");
        }
        if (filePath.contains("samp08-09") && filePath.contains("_s9.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.5d,1d,1.5d))
                    .floatingPointTemplate("%.1f");
        }
        if (filePath.contains("samp08-09") && filePath.contains("s19.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.5d,1d,1.5d,2d))
                    .floatingPointTemplate("%.1f");
        }
        if (filePath.contains("samp08-09") && filePath.contains("s27a.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.5d,1d,1.5d,2d))
                    .floatingPointTemplate("%.1f");
        }
        if (filePath.contains("samp08-09") && filePath.contains("110_20.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.5d,1d,1.5d,2d))
                    .floatingPointTemplate("%.1f");
        }

        if (filePath.contains("samp08-09") && filePath.contains("sigma25a.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(1d, 2d, 3d, 4d, 5d, 6d))
                    .floatingPointTemplate("%.0f");
        }
        if (filePath.contains("samp08-09") && filePath.contains("sigma13a.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(1d, 2d, 3d, 4d))
                    .floatingPointTemplate("%.0f");
        }
        if (filePath.contains("samp08-09") && filePath.contains("sigma17a.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(1d, 2d, 3d, 4d, 5d))
                    .floatingPointTemplate("%.0f");
        }
        if (filePath.contains("samp08-09") && filePath.contains("sigma5.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(1d, 2d, 3d, 4d))
                    .floatingPointTemplate("%.0f");
        }
        if (filePath.contains("samp08-09") && filePath.contains("sigma3.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(1d, 2d, 3d, 4d, 5d))
                    .floatingPointTemplate("%.0f");
        }
        if (filePath.contains("samp08-09") && filePath.contains("sigma11.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList( 2d, 4d, 6d, 8d, 10d))
                    .floatingPointTemplate("%.0f");
        }





        if (filePath.contains("samp10-13") && filePath.contains("s25a.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(1d, 1.5d, 2d, 2.5d))
                    .floatingPointTemplates(asList("%.0f","%.1f","%.0f","%.1f"));
        }
        if (filePath.contains("samp10-13") && filePath.contains("s13a.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.5d, 1d, 1.5d, 2d))
                    .floatingPointTemplates(asList("%.1f","%.0f","%.1f","%.0f"));
        }
        if (filePath.contains("samp10-13") && filePath.contains("s17a.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.5, 1d, 1.5d, 2d, 2.5d, 3d))
                    .floatingPointTemplates(asList("%.1f","%.0f","%.1f","%.0f","%.1f","%.0f"));
        }
        if (filePath.contains("samp10-13") && filePath.contains("s5.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(1d, 2d, 3d, 4d,5d,6d))
                    .floatingPointTemplate("%.0f");
        }
        if (filePath.contains("samp10-13") && filePath.contains("s3.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(1d, 2d, 3d, 4d, 5d,6d,7d))
                    .floatingPointTemplate("%.0f");
        }
        if (filePath.contains("samp10-13") && filePath.contains("s11.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(1d, 2d, 3d, 4d))
                    .floatingPointTemplate("%.0f");
        }



        if (filePath.contains("samp14-16") && filePath.contains("s13a.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList( 1d, 2d, 3d, 4d))
                    .floatingPointTemplate("%.0f");
        }
        if (filePath.contains("samp14-16") && filePath.contains("s17a.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList( 1d, 2d, 3d, 4d))
                    .floatingPointTemplate("%.0f");
        }
        if (filePath.contains("samp14-16") && filePath.contains("s5.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(  2d, 3d, 4d, 5d))
                    .floatingPointTemplate("%.0f");
        }

        if (filePath.contains("samp14-16") && filePath.contains("s3.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(1d, 2d, 3d, 4d, 5d,6d,7d))
                    .floatingPointTemplate("%.0f");
        }
        if (filePath.contains("samp14-16") && filePath.contains("s11.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(2d, 4d, 6d, 8d,10d))
                    .floatingPointTemplate("%.0f");
        }

        if (filePath.contains("samp08b") && filePath.contains("s17a.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(1d, 2d, 3d, 4d))
                    .floatingPointTemplate("%.0f");
        }
        if (filePath.contains("samp08b") && filePath.contains("s11.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(1d, 3d, 5d, 7d, 9d))
                    .floatingPointTemplate("%.0f");
        }

        if (filePath.contains("samp08c") && filePath.contains("s17a.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(1d, 3d, 5d, 7d))
                    .floatingPointTemplate("%.0f");
        }
        if (filePath.contains("samp08c") && filePath.contains("s11.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(1d, 3d, 5d, 7d, 9d, 11d))
                    .floatingPointTemplate("%.0f");
        }

        if (filePath.contains("samp09b") && filePath.contains("s17a.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(1d, 3d, 5d, 7d))
                    .floatingPointTemplate("%.0f");
        }
        if (filePath.contains("samp09b") && filePath.contains("s11.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(1d, 4d, 7d, 10d, 13d))
                    .floatingPointTemplate("%.0f");
        }

        contourPlot.add(colorBarBuilder.build());

        return contourPlot;
    }


    private static ContourPlot createErrorPlot(String filePath, List<Double> labelLevels) {

        List<Function2DValue> dataPoints = getDataPointsFromFile(filePath);
        Grid2DInterpolator interpolator = Grid2DInterpolator.from(dataPoints).withMaxNearest(7);
        Grid2DValues gridValues = interpolator.interpolateOnGrid(-1, 1, -1, 1, 100, 100);
        ColorMapper colorMapper = new TableBasedColorMapper(ColorMapTable.BONE.name());

        int noIsoLines = 3;
       /* if ((filePath.contains("samp08-09") && filePath.contains("sigma25a_err.dist"))
            || (filePath.contains("samp08-09") && filePath.contains("sigma3_err.dist"))
            || (filePath.contains("samp10-13") && filePath.contains("s11_err.dist"))
                || (filePath.contains("samp10-13") && filePath.contains("s17a_err.dist"))
        ) {
            noIsoLines = 4;
        }*/

        List<LineEnds> isoLines = gridValues.toIsoLines(noIsoLines);
        List<ColoredPolygon> isoBands = gridValues.toIsoBands(noIsoLines, colorMapper);

        ContourPlot contourPlot = new ContourPlot(-1, 1, -1, 1)
                .setBottomMargin(70)
                .setTopMargin(20)
                .setLeftMargin(20)
                .setRightMargin(20)
                .setContourWidth(200)
                .setContourHeight(200)
                .setBackgroundAndClear(WHITE)
                .addIsoBands(isoBands, insideCircle(1), INCLUSIVE)
                .addIsoLines(isoLines, DARK_GRAY, new BasicStroke(0.5f), insideCircle(1), EXCLUSIVE)
                .addCircularMargin()
                .addDashedCircumference()
                .addCubicAxes(BLACK, getDashedStroke(1.5f));

        ColorBarBuilder colorBarBuilder = new ColorBarBuilder()
                .grid2DValues(gridValues)
                .setAutoIsoLevels(noIsoLines)
                .colorMapper(colorMapper)
                .colorBarLocation(BOTTOM)
                .left(20)
                .bottom(40)
                .width(200)
                .height(20)
                .font(new Font("Times New Roman", Font.PLAIN, 28))
                .continuous(true)
                .floatingPointTemplate("%.2f")
                .barLabelSpacing(0);

        if (filePath.contains("samp08-09") && filePath.contains("110_20_err.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.2d,0.25d,0.3d))
                    .floatingPointTemplate("%.2f");
        }
        if (filePath.contains("samp08-09") && filePath.contains("s33c_err.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.2d,0.3d,0.4d))
                    .floatingPointTemplate("%.2f");
        }
        if (filePath.contains("samp08-09") && filePath.contains("_s9_err.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.2d,0.25d,0.3d))
                    .floatingPointTemplate("%.2f");
        }
        if (filePath.contains("samp08-09") && filePath.contains("s19_err.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.2d,0.25d,0.3d))
                    .floatingPointTemplate("%.2f");
        }
        if (filePath.contains("samp08-09") && filePath.contains("s27a_err.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.2d,0.25d,0.3d))
                    .floatingPointTemplate("%.2f");
        }


        if ((filePath.contains("samp08-09") && filePath.contains("sigma25a_err.dist"))
                || (filePath.contains("samp08-09") && filePath.contains("sigma3_err.dist"))
        ) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.2d, 0.3d, 0.4d, 0.5d))
                    .floatingPointTemplate("%.1f");
        }

        if ((filePath.contains("samp08-09") && filePath.contains("sigma13a_err.dist"))
                || (filePath.contains("samp08-09") && filePath.contains("sigma5_err.dist"))
        ) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.2d, 0.3d, 0.4d))
                    .floatingPointTemplate("%.1f");
        }

        if (filePath.contains("samp08-09") && filePath.contains("sigma17a_err.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.3d, 0.4d, 0.5d))
                    .floatingPointTemplate("%.1f");
        }

        if (filePath.contains("samp08-09") && filePath.contains("sigma11_err.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.2d, 0.4d, 0.6d))
                    .floatingPointTemplate("%.1f");
        }



        if (filePath.contains("samp10-13") && filePath.contains("s25a_err.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.3d, 0.4d, 0.5d))
                    .floatingPointTemplate("%.1f");
        }
        if (filePath.contains("samp10-13") && filePath.contains("s13a_err.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.3d, 0.4d, 0.5d))
                    .floatingPointTemplate("%.1f");
        }
        if (filePath.contains("samp10-13") && filePath.contains("s17a_err.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.3d, 0.4d, 0.5d, 0.6d))
                    .floatingPointTemplate("%.1f");
        }
        if (filePath.contains("samp10-13") && filePath.contains("s5_err.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.4d, 0.6d, 0.8d))
                    .floatingPointTemplate("%.1f");
        }
        if (filePath.contains("samp10-13") && filePath.contains("s3_err.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.4d, 0.6d, 0.8d))
                    .floatingPointTemplate("%.1f");
        }
        if (filePath.contains("samp10-13") && filePath.contains("s11_err.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.3d, 0.4d, 0.5d, 0.6d))
                    .floatingPointTemplate("%.1f");
        }



        if (filePath.contains("samp14-16") && filePath.contains("s13a_err.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.3d, 0.5d, 0.7d))
                    .floatingPointTemplate("%.1f");
        }
        if (filePath.contains("samp14-16") && filePath.contains("s17a_err.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.4d, 0.5d, 0.6d, 0.7d))
                    .floatingPointTemplate("%.1f");
        }
        if (filePath.contains("samp14-16") && filePath.contains("s5_err.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.5d, 0.7d, 0.9d))
                    .floatingPointTemplate("%.1f");
        }
        if (filePath.contains("samp14-16") && filePath.contains("s3_err.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.5d, 0.7d, 0.9d))
                    .floatingPointTemplate("%.1f");
        }
        if (filePath.contains("samp14-16") && filePath.contains("s11_err.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.4d, 0.8d, 1.2d))
                    .floatingPointTemplates(asList("%.1f", "%.1f", "%.1f"));
        }

        if (filePath.contains("samp08b") && filePath.contains("s17a_err.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList(0.4d, 0.6d, 0.8d, 1d))
                    .floatingPointTemplate("%.1f");
        }
        if (filePath.contains("samp08b") && filePath.contains("s11_err.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList( 0.5d, 1.0d, 1.5d))
                    .floatingPointTemplate("%.1f");
        }

        if (filePath.contains("samp08c") && filePath.contains("s17a_err.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList( 0.4d, 0.8d, 1.2d))
                    .floatingPointTemplate("%.1f");
        }
        if (filePath.contains("samp08c") && filePath.contains("s11_err.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList( 0.5d, 1.0d, 1.5d))
                    .floatingPointTemplate("%.1f");
        }

        if (filePath.contains("samp09b") && filePath.contains("s17a_err.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList( 0.5d, 0.8d, 1.1d))
                    .floatingPointTemplate("%.1f");
        }
        if (filePath.contains("samp09b") && filePath.contains("s11_err.dist")) {
            colorBarBuilder = colorBarBuilder
                    .isoLevels(asList( 0.4d, 0.8d, 1.2d))
                    .floatingPointTemplate("%.1f");
        }

        contourPlot.add(colorBarBuilder.build());

        return contourPlot;
    }
}
