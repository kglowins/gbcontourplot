package io.github.kglowins.gbcontourplot;

import de.erichseifert.vectorgraphics2d.Document;
import de.erichseifert.vectorgraphics2d.Processor;
import de.erichseifert.vectorgraphics2d.Processors;
import de.erichseifert.vectorgraphics2d.VectorGraphics2D;
import de.erichseifert.vectorgraphics2d.intermediate.CommandSequence;
import de.erichseifert.vectorgraphics2d.util.PageSize;
import io.github.kglowins.gbcontourplot.graphics.ColoredPolygon;
import io.github.kglowins.gbcontourplot.graphics.Coordinates2D;
import io.github.kglowins.gbcontourplot.graphics.LineEnds;
import io.github.kglowins.gbcontourplot.graphics.RegionCropStyle;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static io.github.kglowins.gbcontourplot.graphics.PlotUtils.drawCurve;
import static java.lang.Math.PI;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;
import static java.util.Objects.nonNull;
import static java.util.stream.IntStream.rangeClosed;

@Slf4j
public class ContourPlot extends JPanel {

    @Getter
    private int topMargin = 0;
    @Getter
    private int bottomMargin = 0;
    @Getter
    private int leftMargin = 0;
    @Getter
    private int rightMargin = 0;
    @Getter
    private int contourWidth = 600;
    @Getter
    private int contourHeight = 600;

    private double contourMinX;
    private double contourMaxX;
    private double contourMinY;
    private double contourMaxY;

    private List<Consumer<Graphics2D>> plotElements;

    public ContourPlot(double contourMinX, double contourMaxX, double contourMinY, double contourMaxY) {
        this.contourMinX = contourMinX;
        this.contourMaxX = contourMaxX;
        this.contourMinY = contourMinY;
        this.contourMaxY = contourMaxY;
        plotElements = new LinkedList<>();
    }

    public ContourPlot setTopMargin(int topMargin) {
        this.topMargin = topMargin;
        updateDimensions();
        return this;
    }

    public ContourPlot setBottomMargin(int bottomMargin) {
        this.bottomMargin = bottomMargin;
        updateDimensions();
        return this;
    }

    public ContourPlot setLeftMargin(int leftMargin) {
        this.leftMargin = leftMargin;
        updateDimensions();
        return this;
    }

    public ContourPlot setRightMargin(int rightMargin) {
        this.rightMargin = rightMargin;
        updateDimensions();
        return this;
    }

    public ContourPlot setContourWidth(int contourWidth) {
        this.contourWidth = contourWidth;
        updateDimensions();
        return this;
    }

    public ContourPlot setContourHeight(int contourHeight) {
        this.contourHeight = contourHeight;
        updateDimensions();
        return this;
    }

    private void updateDimensions() {
        int totalWidth = leftMargin + contourWidth + rightMargin;
        int totalHeight = bottomMargin + contourHeight + topMargin;
        Dimension preferredDimension = new Dimension(totalWidth, totalHeight);
        setMinimumSize(preferredDimension);
        setMaximumSize(preferredDimension);
        setPreferredSize(preferredDimension);
    }

    public void clear() {
        plotElements = new LinkedList<>();
    }

    public ContourPlot add(Consumer<Graphics2D> plotElement) {
        plotElements.add(plotElement);
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        BufferedImage bufferedImage = new BufferedImage(leftMargin + contourWidth + rightMargin,
            bottomMargin + contourHeight + topMargin, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) g;
        Graphics2D g2dImage = bufferedImage.createGraphics();
        g2dImage.setTransform(getBaseTransform());
        g2dImage.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        plotElements.forEach(e -> e.accept(g2dImage));
        g2d.drawImage(bufferedImage, null, 0, 0);
    }

    private AffineTransform getBaseTransform() {
        AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -(topMargin + bottomMargin + contourHeight));
        return tx;
    }

    public void toVectorFile(String format, PageSize pageSize, String path) {
        Graphics2D vg2d = new VectorGraphics2D();
        vg2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        vg2d.setTransform(getBaseTransform());
        plotElements.forEach(e -> e.accept(vg2d));
        CommandSequence commands = ((VectorGraphics2D) vg2d).getCommands();
        Processor processor = Processors.get(format);
        Document document = processor.getDocument(commands, pageSize);
        try {
            document.writeTo(new FileOutputStream(path));
        } catch (IOException e) {
            log.error("Failed to export ContourPlot to {}", format, e);
        }
    }

    public void toRasterFile(String format, String path) {
        BufferedImage bufferedImage = new BufferedImage(leftMargin + contourWidth + rightMargin,
            topMargin + contourHeight + bottomMargin, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setTransform(getBaseTransform());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        plotElements.forEach(e -> e.accept(g2d));
        g2d.dispose();
        try {
            ImageIO.write(bufferedImage, format, new File(path));
        } catch (IOException e) {
            log.error("Failed to export ContourPlot to {}", format, e);
        }
    }

    public ContourPlot setBackgroundAndClear(Color color) {
        plotElements.add(g2d -> {
            g2d.setBackground(color);
            int totalWidth = leftMargin + contourWidth + rightMargin;
            int totalHeight = bottomMargin + contourHeight + topMargin;
            g2d.clearRect(0, 0, totalWidth, totalHeight);
        });
        return this;
    }

    public ContourPlot addIsoBands(List<ColoredPolygon> polygons) {
        plotElements.add(isoBandsPlotter(polygons));
        return this;
    }

    public Consumer<Graphics2D> isoBandsPlotter(List<ColoredPolygon> polygons) {
        return isoBandsPlotter(polygons, null, null);
    }

    public ContourPlot addIsoBands(List<ColoredPolygon> polygons,
                                   BiFunction<Double, Double, Boolean> regionFunction,
                                   RegionCropStyle regionCropStyle) {
        plotElements.add(isoBandsPlotter(polygons, regionFunction, regionCropStyle));
        return this;
    }

    public Consumer<Graphics2D> isoBandsPlotter(List<ColoredPolygon> polygons,
                                                BiFunction<Double, Double, Boolean> regionFunction,
                                                RegionCropStyle regionCropStyle) {
        return g2d -> polygons.forEach(polygon -> {
            g2d.setColor(polygon.getColor());
            g2d.setStroke(new BasicStroke());
            Polygon p = new Polygon();

            if (nonNull(regionFunction) && nonNull(regionCropStyle)) {
                boolean allOutside = true;
                boolean oneOutside = false;

                for (Coordinates2D coords : polygon.getPolygon()) {
                    if (regionFunction.apply(coords.x(), coords.y())) {
                        allOutside = false;
                    } else {
                        oneOutside = true;
                    }
                }
                if ((regionCropStyle == RegionCropStyle.INCLUSIVE && allOutside)
                    || (regionCropStyle == RegionCropStyle.EXCLUSIVE && oneOutside)) {
                    return;
                }
            }

            polygon.getPolygon().forEach(coords -> p.addPoint(
                leftMargin + Long.valueOf(round((coords.x() - contourMinX) / (contourMaxX - contourMinX) * contourWidth)).intValue(),
                bottomMargin + Long.valueOf(round((coords.y() - contourMinY) / (contourMaxY - contourMinY) * contourHeight)).intValue()
            ));
            g2d.drawPolygon(p);
            g2d.fillPolygon(p);
        });
    }

    public ContourPlot addIsoLines(List<LineEnds> lineEnds, Color color, Stroke stroke) {
        plotElements.add(isoLinesPlotter(lineEnds, color, stroke));
        return this;
    }

    public Consumer<Graphics2D> isoLinesPlotter(List<LineEnds> lineEnds, Color color, Stroke stroke) {
        return isoLinesPlotter(lineEnds, color, stroke, null, null);
    }

    public ContourPlot addIsoLines(List<LineEnds> lineEnds, Color color, Stroke stroke,
                                   BiFunction<Double, Double, Boolean> regionFunction,
                                   RegionCropStyle regionCropStyle) {
        plotElements.add(isoLinesPlotter(lineEnds, color, stroke, regionFunction, regionCropStyle));
        return this;
    }

    public Consumer<Graphics2D> isoLinesPlotter(List<LineEnds> lineEnds, Color color, Stroke stroke,
                                                BiFunction<Double, Double, Boolean> regionFunction,
                                                RegionCropStyle regionCropStyle) {
        return g2d -> {
            g2d.setColor(color);
            g2d.setStroke(stroke);
            lineEnds.forEach(le -> {
                if (nonNull(regionFunction) && nonNull(regionCropStyle)) {

                    boolean isEnd1InRegion = regionFunction.apply(le.x1(), le.y1());
                    boolean isEnd2InRegion = regionFunction.apply(le.x2(), le.y2());

                    if (regionCropStyle == RegionCropStyle.EXCLUSIVE
                        && (!isEnd1InRegion || !isEnd2InRegion)) {
                        return;
                    }

                    if (regionCropStyle == RegionCropStyle.INCLUSIVE
                        && !isEnd1InRegion && !isEnd2InRegion) {
                        return;
                    }
                }

                int x1 = leftMargin
                    + Long.valueOf(round((le.x1() - contourMinX) / (contourMaxX - contourMinX) * contourWidth)).intValue();
                int x2 = leftMargin
                    + Long.valueOf(round((le.x2() - contourMinX) / (contourMaxX - contourMinX) * contourWidth)).intValue();
                int y1 = bottomMargin
                    + Long.valueOf(round((le.y1() - contourMinY) / (contourMaxY - contourMinY) * contourHeight)).intValue();
                int y2 = bottomMargin
                    + Long.valueOf(round((le.y2() - contourMinY) / (contourMaxY - contourMinY) * contourHeight)).intValue();

                g2d.drawLine(x1, y1, x2, y2);
            });
        };
    }

    public ContourPlot addCircularMargin() {
        plotElements.add(g2d -> {
            g2d.setColor(Color.WHITE);
            int lineWidth = 32;
            g2d.setStroke(new BasicStroke((float) 2 * lineWidth));
            g2d.drawOval(leftMargin - lineWidth, bottomMargin - lineWidth,
                contourWidth + 2 * lineWidth, contourHeight + 2 * lineWidth);
        });
        return this;
    }

    public ContourPlot addCircularMarginSST() {
        plotElements.add(g2d -> {
            g2d.setColor(Color.WHITE);
            int lineWidth = 32;
            g2d.setStroke(new BasicStroke((float) 2 * lineWidth));
            g2d.drawOval(leftMargin - contourWidth - lineWidth,
                bottomMargin - contourHeight - lineWidth,
                2 * contourWidth + 2 * lineWidth, 2 * contourHeight + 2 * lineWidth);
        });
        return this;
    }

    public ContourPlot addDashedCircumference() {
        plotElements.add(g2d -> {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 12.5f, new float[]{5.0f, 7.5f}, 0.0f));
            g2d.drawOval(leftMargin, bottomMargin, contourWidth, contourHeight);
        });
        return this;
    }

    public ContourPlot addHexagonalSST() {
        plotElements.add(g2d -> {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawLine(leftMargin, bottomMargin, leftMargin + contourWidth, bottomMargin);
            g2d.drawLine(leftMargin, bottomMargin,
                leftMargin + (int) (contourWidth * cos(PI / 6)),
                bottomMargin + (int) (contourHeight * sin(PI / 6)));
            g2d.drawArc(leftMargin - contourWidth, bottomMargin - contourHeight,
                2 * contourWidth, 2 * contourHeight, 0, -30);
        });
        return this;
    }

    //TODO copied from gbtoolbox-legacy
    public ContourPlot addCubicSST() {
        plotElements.add(g2d -> {
            int numberOfSamplingPoints = 32;

            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2f));

            Line2D axis1 = new Line2D.Double(leftMargin, bottomMargin,
                leftMargin + contourWidth, bottomMargin);
            Line2D axis2 = new Line2D.Double(leftMargin, bottomMargin,
                leftMargin +  0.883663 * contourWidth, bottomMargin + 0.883663 * contourHeight);

            g2d.draw(axis1);
            g2d.draw(axis2);

            double[] t = new double[numberOfSamplingPoints + 1];
            double dt = 0.25 * PI / numberOfSamplingPoints;
            rangeClosed(0, numberOfSamplingPoints).forEach(i -> t[i] = -0.25 * PI + i * dt);

            Point2D[] points = new Point2D[numberOfSamplingPoints + 1];
            rangeClosed(0, numberOfSamplingPoints).forEach(i ->
                points[i] = new Point2D.Double(
                    leftMargin + (contourWidth / 0.41421356) * tan(atan(1 / cos(t[i])) * 0.5) * cos(t[i]),
                    bottomMargin - (contourHeight / 0.41421356) * tan(atan(1 / cos(t[i])) * 0.5) * sin(t[i])
                )
            );
            drawCurve(g2d, points);
        });
        return this;
    }

    public ContourPlot cropHexagonalSST() {
        plotElements.add(g2d -> {
            g2d.setColor(Color.WHITE);
            Polygon upperArea = new Polygon();
            upperArea.addPoint(leftMargin, bottomMargin);
            upperArea.addPoint(leftMargin + (int)(contourWidth* cos(PI / 6)),
                bottomMargin + (int)(contourHeight* sin(PI / 6)));
            upperArea.addPoint(leftMargin + contourWidth, bottomMargin + contourHeight);
            upperArea.addPoint(leftMargin, bottomMargin + contourHeight);
            g2d.fillPolygon(upperArea);
        });
        return this;
    }

    public ContourPlot cropCubicSST() {
        plotElements.add(g2d -> {
            g2d.setColor(Color.WHITE);
            Polygon upperTriangle = new Polygon();
            upperTriangle.addPoint(leftMargin, bottomMargin);
            upperTriangle.addPoint(leftMargin + contourWidth, bottomMargin + contourHeight);
            upperTriangle.addPoint(leftMargin, bottomMargin + contourHeight);
            g2d.fillPolygon(upperTriangle);
            Polygon rhsArea = new Polygon();
            rhsArea.addPoint(leftMargin + contourWidth + 1, bottomMargin + contourHeight);
            int numberOfSamplingPoints = 32;
            double dt = 0.25 * PI / numberOfSamplingPoints;
            rangeClosed(0, numberOfSamplingPoints).forEach(i -> {
                double ti = -0.25 * PI + i * dt;
                rhsArea.addPoint(
                    leftMargin + (int) round((contourWidth / 0.41421356) * tan(atan(1 / cos(ti)) * 0.5) * cos(ti)),
                    bottomMargin - (int) round((contourHeight / 0.41421356) * tan(atan(1 / cos(ti)) * 0.5) * sin(ti))
                );
            });
            rhsArea.addPoint(leftMargin + contourWidth + 1, bottomMargin);

            g2d.fillPolygon(rhsArea);
        });
        return this;
    }

    //TODO copied from gbtoolbox-legacy
    public ContourPlot addHexagonalAxes() {
        plotElements.add(g2d -> {
            Line2D axis1 = new Line2D.Double(leftMargin, bottomMargin + contourHeight / 2, leftMargin + contourWidth, bottomMargin + contourHeight / 2);
            Line2D axis2 = new Line2D.Double(leftMargin + contourWidth / 2, bottomMargin, leftMargin + contourWidth / 2, bottomMargin + contourHeight);

            double radius = contourHeight / 2.; //assumes height=width
            Line2D axis3 = new Line2D.Double(leftMargin + radius + radius * cos(PI / 6),
                bottomMargin + radius + radius * sin(PI / 6),
                leftMargin + radius + radius * cos(PI + PI / 6),
                bottomMargin + radius + radius * sin(PI + PI / 6));

            Line2D axis4 = new Line2D.Double(leftMargin + radius + radius* cos(2 * PI / 6),
                bottomMargin + radius + radius * sin(2 * PI / 6),
                leftMargin + radius + radius * cos(PI + 2 * PI / 6),
                bottomMargin + radius + radius * sin(PI + 2 * PI / 6));

            Line2D axis5 = new Line2D.Double(leftMargin + radius + radius* cos(5 * PI / 6),
                bottomMargin + radius + radius * sin(5 * PI / 6),
                leftMargin + radius + radius * cos(PI + 5 * PI / 6),
                bottomMargin + radius + radius * sin(PI + 5 * PI / 6));

            Line2D axis6 = new Line2D.Double(leftMargin + radius + radius* cos(4 * PI / 6),
                bottomMargin + radius + radius * sin(4 * PI / 6),
                leftMargin + radius + radius * cos(PI + 4 * PI / 6),
                bottomMargin + radius + radius * sin(PI + 4 * PI / 6));

            BasicStroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 12.5f, new float[]{5.0f, 7.5f}, 0.0f);
            g2d.setStroke(dashed);

            g2d.draw(axis1);
            g2d.draw(axis2);
            g2d.draw(axis3);
            g2d.draw(axis4);
            g2d.draw(axis5);
            g2d.draw(axis6);
        });
        return this;
    }

    //TODO comes from gbtoolbox-legacy
    public ContourPlot addCubicAxes() {
        plotElements.add(g2d -> {

            int numberOfSamplingPoints = 64;

            Ellipse2D circle = new Ellipse2D.Double(leftMargin, bottomMargin, contourWidth, contourHeight);

            g2d.setColor(Color.BLACK);
            BasicStroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 12.5f, new float[]{5.0f, 7.5f}, 0.0f);
            g2d.setStroke(dashed);
            g2d.draw(circle);

            double radius = contourHeight / 2.;
            Line2D axis1 = new Line2D.Double(leftMargin, bottomMargin + radius, leftMargin + 2 * radius, bottomMargin + radius);
            Line2D axis2 = new Line2D.Double(leftMargin + radius, bottomMargin, leftMargin + radius, bottomMargin + 2 * radius);

            Line2D axis3 = new Line2D.Double(leftMargin + (int) round(radius * (1d - 0.5 * sqrt(2))),
                bottomMargin + (int) round(radius * (1 - 0.5 * sqrt(2))),
                leftMargin + radius + (int) round(radius * 0.5 * sqrt(2)),
                bottomMargin + radius + (int) round(radius * 0.5 * sqrt(2)));

            Line2D axis4 = new Line2D.Double(leftMargin + (int) round(radius * (1d - 0.5 * sqrt(2))),
                bottomMargin + radius + (int) round(radius * 0.5 * sqrt(2)),
                leftMargin + radius + (int) round(radius * 0.5 * sqrt(2)),
                bottomMargin + (int) round(radius * (1 - 0.5 * sqrt(2))));

            g2d.draw(axis1);
            g2d.draw(axis2);
            g2d.draw(axis3);
            g2d.draw(axis4);

            final double[] t = new double[numberOfSamplingPoints + 1];

            final double dt = PI / numberOfSamplingPoints;
            for(int i = 0; i <= numberOfSamplingPoints; ++i) t[i] = -0.5 * PI + i * dt;

            Point2D[] pts = new Point2D[numberOfSamplingPoints + 1];
            for (int i = 0; i <= numberOfSamplingPoints; ++i) {
                pts[i] = new Point2D.Double(leftMargin + radius * (1 + (tan(atan(1 / cos(t[i])) * 0.5) * cos(t[i]))),
                    bottomMargin + radius * (1 + (tan(atan(1 / cos(t[i])) * 0.5) * sin(t[i]))));
            }
            drawCurve(g2d, pts);

            pts = new Point2D[numberOfSamplingPoints + 1];
            for (int i = 0; i <= numberOfSamplingPoints; ++i) {
                pts[i] = new Point2D.Double(leftMargin + radius * (1 - (tan(atan(1 / cos(t[i])) * 0.5) * cos(t[i]))),
                    bottomMargin + radius * (1 + (tan(atan(1 / cos(t[i])) * 0.5) * sin(t[i]))));
            }
            drawCurve(g2d, pts);

            pts = new Point2D[numberOfSamplingPoints + 1];
            for (int i = 0; i <= numberOfSamplingPoints; ++i) {
                pts[i] = new Point2D.Double(leftMargin + radius * (1 + (tan(atan(1 / cos(t[i])) * 0.5) * sin(t[i]))),
                    bottomMargin + radius * (1 + (tan(atan(1 / cos(t[i])) * 0.5) * cos(t[i]))));
            }
            drawCurve(g2d, pts);

            pts = new Point2D[numberOfSamplingPoints + 1];
            for (int i = 0; i <= numberOfSamplingPoints; ++i) {
                pts[i] = new Point2D.Double(leftMargin + radius * (1 + (tan(atan(1 / cos(t[i])) * 0.5) * sin(t[i]))),
                    bottomMargin + radius * (1 - (tan(atan(1 / cos(t[i])) * 0.5) * cos(t[i]))));
            }
            drawCurve(g2d, pts);
        });
        return this;
    }
}
