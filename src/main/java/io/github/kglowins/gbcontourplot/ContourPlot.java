package io.github.kglowins.gbcontourplot;

import io.github.kglowins.gbparams.gbcd.SymmetryAxis;
import io.github.kglowins.gbcontourplot.graphics.ColoredPolygon;
import io.github.kglowins.gbcontourplot.graphics.Coordinates2D;
import io.github.kglowins.gbcontourplot.graphics.LineEnds;
import io.github.kglowins.gbcontourplot.graphics.RegionCropStyle;
import io.github.kglowins.gbparams.utils.SaferMath;
import net.sf.epsgraphics.ColorMode;
import net.sf.epsgraphics.EpsGraphics;
import org.apache.commons.math3.util.FastMath;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class ContourPlot extends JPanel {
    private static final Logger log = LoggerFactory.getLogger(ContourPlot.class);

    private int topMargin = 0;
    private int bottomMargin = 0;
    private int leftMargin = 0;
    private int rightMargin = 0;
    private int contourWidth = 600;
    private int contourHeight = 600;

    private final double contourMinX;
    private final double contourMaxX;
    private final double contourMinY;
    private final double contourMaxY;

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

    public void toEps(String path) {
        try {
            EpsGraphics g = new EpsGraphics("ContourPlot.toEps",
                    new FileOutputStream(path),
                    0, 0,
                    leftMargin + contourWidth + rightMargin,
                    bottomMargin + contourHeight + topMargin,
                    ColorMode.COLOR_RGB);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setTransform(getBaseTransform());
            plotElements.forEach(e -> e.accept(g));
            g.close();
        } catch (IOException e) {
            log.error("Failed to export ContourPlot to EPS", e);
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
    public ContourPlot addHexagonalAxes(Color color, BasicStroke stroke) {
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

            g2d.setColor(color);
            g2d.setStroke(stroke);

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
    public ContourPlot addCubicAxes(Color color, BasicStroke stroke) {
        plotElements.add(g2d -> {

            int numberOfSamplingPoints = 64;

            Ellipse2D circle = new Ellipse2D.Double(leftMargin, bottomMargin, contourWidth, contourHeight);

            g2d.setColor(color);
            g2d.setStroke(stroke);
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

    public ContourPlot addSpots(List<Point2D> coordinates, Color color, int size) {
        plotElements.add(g2d -> {
            g2d.setColor(color);
            for (Point2D coords : coordinates) {
                g2d.fillOval(
                        leftMargin + (int)(0.5 * contourWidth * (1. + coords.getX()) ) - size / 2,
                        bottomMargin + (int)(0.5 * contourHeight * (1. + coords.getY())) - size / 2,
                        size, size);
            }
        });
        return this;
    }

    public ContourPlot addSpots(List<Point2D> coordinates, Color color, int size, Stroke stroke) {
        plotElements.add(g2d -> {
            g2d.setColor(color);
            g2d.setStroke(stroke);
            for (Point2D coords : coordinates) {
                g2d.drawOval(
                        leftMargin + (int)(0.5 * contourWidth * (1. + coords.getX()) ) - size / 2,
                        bottomMargin + (int)(0.5 * contourHeight * (1. + coords.getY())) - size / 2,
                        size, size);
            }
        });
        return this;
    }

    public ContourPlot addZones(List<List<Point2D>> zones, Color color, Stroke stroke) {
        plotElements.add(g2d -> {
            g2d.setColor(color);
            g2d.setStroke(stroke);
            for (List<Point2D> zone : zones) {
                drawCurve(g2d, zone.stream().map(p -> new Point2D.Double(
                        leftMargin + (int)(0.5 * contourWidth * (1. + p.getX())),
                        bottomMargin + (int)(0.5 * contourHeight * (1. + p.getY()))
                )).toArray(Point2D[]::new));
            }
        });
        return this;
    }

    public ContourPlot addSymmetryAxes(List<SymmetryAxis> symmetryAxes, int size, Color color) {
        plotElements.add(g2d -> {
            for (SymmetryAxis symmetryAxis : symmetryAxes) {
                double theta = FastMath.atan2(symmetryAxis.getAxis().y(), symmetryAxis.getAxis().x());
                double phi = SaferMath.acos(symmetryAxis.getAxis().z());
                double r = FastMath.tan(0.5 * phi);
                int rX = leftMargin + (int)(0.5 * contourWidth * (1. + r * FastMath.cos(theta)));
                int rY = bottomMargin + (int)(0.5 * contourHeight * (1. + r * FastMath.sin(theta)));

                g2d.setColor(color);
                if (symmetryAxis.getMultiplicity() == 2) {
                    boolean isOverlap = isOverlap(symmetryAxes, symmetryAxis);
                    if (!isOverlap) {
                        g2d.fillOval(rX - size / 4, rY - size / 2, size / 2, size);
                    }

                } else if(symmetryAxis.getMultiplicity() == 3) {
                    boolean isOverlap = isOverlap(symmetryAxes, symmetryAxis);
                    double radius = 0.625 * size;
                    if (!isOverlap) {
                        g2d.fillPolygon(
                                new int[]{rX - (int)(radius * cos(PI / 2. + PI / 3.)),
                                        rX - (int)(radius * cos(PI / 2. + PI )),
                                        rX - (int)(radius * cos(PI / 2. + 5.* PI / 3.))

                                },
                                new int[]{rY - (int)(radius * sin(PI / 2. + PI / 3.)),
                                        rY - (int)(radius * sin(PI / 2. + PI)),
                                        rY - (int)(radius * sin(PI / 2. + 5.* PI / 3.))},
                                3);
                    }

                } else if(symmetryAxis.getMultiplicity() == 4) {
                    boolean isOverlap = isOverlap(symmetryAxes, symmetryAxis);
                    if (!isOverlap) {
                        g2d.fillRect(rX - (int)(11./32.*size), rY - - (int)(11./32.*size), (int)(22./32.*size), (int)(22./32.*size));
                    }

                } else if(symmetryAxis.getMultiplicity() == 6) {
                    double rad = 24./32.*size;
                    boolean isOverlap = isOverlap(symmetryAxes, symmetryAxis);
                    if (!isOverlap)
                        g2d.fillPolygon(
                                new int[]{rX - (int)(rad* cos(0.)),
                                        rX - (int)(rad* cos(PI / 3.)),
                                        rX - (int)(rad* cos(PI * 2. / 3.)),
                                        rX - (int)(rad* cos(PI )),
                                        rX - (int)(rad* cos(PI * 4. / 3.)),
                                        rX - (int)(rad* cos(PI * 5. / 3.)),
                                },
                                new int[]{rY - (int)(rad * sin(0.)),
                                        rY - (int)(rad* sin(PI / 3.)),
                                        rY - (int)(rad* sin(PI * 2. / 3.)),
                                        rY - (int)(rad* sin(PI )),
                                        rY - (int)(rad* sin(PI * 4. / 3.)),
                                        rY - (int)(rad* sin(PI * 5. / 3.))},
                                6);

                } else if(symmetryAxis.getMultiplicity() == 8) {
                    double rad = 25./32.*size;
                    boolean isOverlap = isOverlap(symmetryAxes, symmetryAxis);
                    if (!isOverlap)
                        g2d.fillPolygon(
                                new int[]{(int)(rX - rad* cos(0d)),
                                        (int)(rX - rad* cos(PI / 4d)),
                                        (int)(rX - rad* cos(PI / 4d * 2d)),
                                        (int)(rX - rad* cos(PI / 4d * 3d)),
                                        (int)(rX - rad* cos(PI / 4d * 4d)),
                                        (int)(rX - rad* cos(PI / 4d * 5d)),
                                        (int)(rX - rad* cos(PI / 4d * 6d)),
                                        (int)(rX - rad* cos(PI / 4d * 7d))

                                },
                                new int[]{(int)(rY - rad* sin(0d)),
                                        (int)(rY - rad* sin(PI / 4d )),
                                        (int)(rY - rad* sin(PI / 4d * 2d)),
                                        (int)(rY - rad* sin(PI / 4d * 3d)),
                                        (int)(rY - rad* sin(PI / 4d * 4d)),
                                        (int)(rY - rad* sin(PI / 4d * 5d)),
                                        (int)(rY - rad* sin(PI / 4d * 6d)),
                                        (int)(rY - rad* sin(PI / 4d * 7d)),
                                }, 8);

                } else if(symmetryAxis.getMultiplicity() == 12) {
                    double rad = 42./32.*size;
                    boolean isOverlap = isOverlap(symmetryAxes, symmetryAxis);
                    if (!isOverlap)
                        g2d.fillPolygon(
                                new int[]{(int)(rX - rad* cos(0d)),
                                        (int)(rX - rad* cos(PI / 6d)),
                                        (int)(rX - rad* cos(PI * 2d / 6d)),
                                        (int)(rX - rad* cos(PI * 3d / 6d)),
                                        (int)(rX - rad* cos(PI * 4d / 6d)),
                                        (int)(rX - rad* cos(PI * 5d / 6d)),
                                        (int)(rX - rad* cos(PI )),
                                        (int)(rX - rad* cos(PI * 7d / 6d)),
                                        (int)(rX - rad* cos(PI * 8d / 6d)),
                                        (int)(rX - rad* cos(PI * 9d / 6d)),
                                        (int)(rX - rad* cos(PI * 10d / 6d)),
                                        (int)(rX - rad* cos(PI * 11d / 6d)),
                                },
                                new int[]{(int)(rY - rad* sin(0.)),
                                        (int)(rY - rad* sin(PI / 6.)),
                                        (int)(rY - rad* sin(PI * 2. / 6.)),
                                        (int)(rY - rad* sin(PI * 3. / 6.)),
                                        (int)(rY - rad* sin(PI * 4. / 6.)),
                                        (int)(rY - rad* sin(PI * 5. / 6.)),
                                        (int)(rY - rad* sin(PI )),
                                        (int)(rY - rad* sin(PI * 7. / 6.)),
                                        (int)(rY - rad* sin(PI * 8. / 6.)),
                                        (int)(rY - rad* sin(PI * 9. / 6.)),
                                        (int)(rY - rad* sin(PI * 10. / 6.)),
                                        (int)(rY - rad* sin(PI * 11. / 6.))
                                }, 12);


                } else {
                    log.warn("Unsupported {}-fold symmetry axis won't be displayed", symmetryAxis.getMultiplicity());
                }
            }

            g2d.setColor(color);
            g2d.fillOval(leftMargin + contourWidth / 2 - size / 4, bottomMargin + contourHeight / 2 - size / 4, size / 2, size / 2);
            g2d.setColor(Color.white);
            g2d.fillOval(leftMargin + contourWidth / 2 - size / 4 + 3, bottomMargin + contourHeight / 2 - size / 4 + 3, size / 2 - 6, size / 2 - 6);
        });
        return this;
    }

    private boolean isOverlap(List<SymmetryAxis> symmetryAxes, SymmetryAxis axis1) {
        final double EPSILON = 0.01;
        for (SymmetryAxis axis2 : symmetryAxes)
            if (Math.abs(axis2.getAxis().dot(axis1.getAxis()) - 1.) < EPSILON
                    && axis2.getMultiplicity() > axis1.getMultiplicity()) {
                return true;
            }
        return false;
    }
}
