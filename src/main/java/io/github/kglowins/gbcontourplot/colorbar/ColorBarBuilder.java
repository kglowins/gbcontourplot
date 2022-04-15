package io.github.kglowins.gbcontourplot.colorbar;

import io.github.kglowins.gbcontourplot.colormappers.ColorMapper;
import io.github.kglowins.gbcontourplot.grid.Grid2DValues;
import net.sf.javaml.core.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import static io.github.kglowins.gbcontourplot.colorbar.ColorBarLocation.BOTTOM;
import static io.github.kglowins.gbcontourplot.colorbar.ColorBarLocation.RIGHT;
import static io.github.kglowins.gbcontourplot.graphics.PlotUtils.getIsoBandColor;
import static java.util.Comparator.naturalOrder;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;

public class ColorBarBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(ColorBarBuilder.class);

    // required
    private List<Double> isoLevels;
    private Double rangeMin;
    private Double rangeMax;
    private ColorMapper colorMapper;
    private Grid2DValues grid2DValues;
    private ColorBarLocation colorBarLocation;
    private Integer left;
    private Integer bottom;
    private Integer width;
    private Integer height;

    // optional
    private Font font;
    private String floatingPointTemplate = "%.1f";
    private List<String> floatingPointTemplates = null;
    private int barLabelSpacing = 10;
    private float barBorderWidth = 1.5f;
    private List<Double> labelLevels;
    private boolean continuous = false;

    public ColorBarBuilder setAutoIsoLevels(int numberOfIsolevels) {
        requireNonNull(grid2DValues, "grid2DValues needs to be set prior setAutoIsoLevels");
        isoLevels = grid2DValues.getAutoIsoLevels(numberOfIsolevels);
        return setAutoRange();
    }

    public ColorBarBuilder setAutoRange() {
        rangeMin = grid2DValues.getFMin();
        rangeMax = grid2DValues.getFMax();
        return this;
    }

    public ColorBarBuilder setRange(double min, double max) {
        rangeMin = min;
        rangeMax = max;
        return this;
    }

    public ColorBarBuilder barBorderWidth(float width) {
        barBorderWidth = width;
        return this;
    }

    public ColorBarBuilder grid2DValues(Grid2DValues grid2DValues) {
        this.grid2DValues = grid2DValues;
        return this;
    }

    public ColorBarBuilder colorMapper(ColorMapper colorMapper) {
        this.colorMapper = colorMapper;
        return this;
    }

    public ColorBarBuilder colorBarLocation(ColorBarLocation colorBarLocation) {
        this.colorBarLocation = colorBarLocation;
        return this;
    }

    public ColorBarBuilder isoLevels(List<Double> isoLevels) {
        this.isoLevels = isoLevels;
        return this;
    }

    public ColorBarBuilder left(Integer left) {
        this.left = left;
        return this;
    }

    public ColorBarBuilder bottom(Integer bottom) {
        this.bottom = bottom;
        return this;
    }

    public ColorBarBuilder width(Integer width) {
        this.width = width;
        return this;
    }

    public ColorBarBuilder height(Integer height) {
        this.height = height;
        return this;
    }

    public ColorBarBuilder font(Font font) {
        this.font = font;
        return this;
    }

    public ColorBarBuilder labelLevels(List<Double> labelLevels) {
        this.labelLevels = labelLevels;
        return this;
    }

    public ColorBarBuilder floatingPointTemplate(String floatingPointTemplate) {
        this.floatingPointTemplate = floatingPointTemplate;
        return this;
    }

    public ColorBarBuilder floatingPointTemplates(List<String> floatingPointTemplates) {
        this.floatingPointTemplates = floatingPointTemplates;
        return this;
    }

    public ColorBarBuilder barLabelSpacing(int barLabelSpacing) {
        this.barLabelSpacing = barLabelSpacing;
        return this;
    }

    public ColorBarBuilder continuous(boolean continuous) {
        this.continuous = continuous;
        return this;
    }

    public Consumer<Graphics2D> build() {
        checkIfRequiredParamsProvided();
        checkIfFloatingPointTemplatesCorrect();

        return g2d -> {
            if (nonNull(font)) {
                g2d.setFont(font);
            }

            Pair<List<Double>, List<Double>> barLevelsWithColorLevels = continuous
                ? scaleBarLevels(getContinuousIsoLevels(grid2DValues.getFMin(),
                    grid2DValues.getFMax()), grid2DValues.getFMin(), grid2DValues.getFMax(), rangeMin, rangeMax)
                : scaleBarLevels(isoLevels, grid2DValues.getFMin(), grid2DValues.getFMax(), rangeMin, rangeMax);

            List<Double> barLevels = barLevelsWithColorLevels.x();
            List<Double> barColorLevels = barLevelsWithColorLevels.y();

            List<Double> scaledLabelLevels = nonNull(labelLevels)
                ? scaleLabelLevels(labelLevels, grid2DValues.getFMin(), grid2DValues.getFMax())
                : scaleLabelLevels(isoLevels, grid2DValues.getFMin(), grid2DValues.getFMax());

            if (colorBarLocation == RIGHT) {
                range(0, barLevels.size() - 1)
                    .boxed()
                    .forEach(index -> {
                            // draw bar rectangles
                            g2d.setColor(getIsoBandColor(colorMapper, barColorLevels, index));
                            g2d.setStroke(new BasicStroke());
                            g2d.drawRect(left,
                                bottom + (int) Math.round(barLevels.get(index) * height),
                                width,
                                (int) Math.round((barLevels.get(index + 1) - barLevels.get(index)) * height));
                            g2d.fillRect(left,
                                bottom + (int) Math.round(barLevels.get(index) * height),
                                width,
                                (int) Math.round((barLevels.get(index + 1) - barLevels.get(index)) * height));
                        });
                range(0, scaledLabelLevels.size())
                    .boxed()
                    .forEach(index -> {
                        // display labels
                        g2d.setColor(Color.BLACK);
                        int labelShift = g2d.getFontMetrics().getHeight() / 2;

                        // TODO find a way for avoiding mirror-reflected strings
                        // better than the below scaling and negative sign for y coordinates
                        g2d.scale(1, -1);
                        String fpTemplate = floatingPointTemplate;
                        if (nonNull(floatingPointTemplates)) {
                            fpTemplate = floatingPointTemplates.get(index);
                        }
                        g2d.drawString(String.format((Locale) null, fpTemplate, grid2DValues.getFMin() + (grid2DValues.getFMax() - grid2DValues.getFMin()) * scaledLabelLevels.get(index)),
                            left + width + barLabelSpacing,
                            -(bottom + (int) Math.round(scaledLabelLevels.get(index) * height) - labelShift)
                        );

                        g2d.setStroke(new BasicStroke(barBorderWidth));
                        g2d.drawLine(left + width, -(bottom + (int) Math.round(scaledLabelLevels.get(index) * height) - 1),
                            left + width - width/2, -(bottom + (int) Math.round(scaledLabelLevels.get(index) * height) - 1));

                        g2d.scale(1, -1);
                    });
            } else {
                range(0, barLevels.size() - 1)
                    .boxed()
                    .forEach(index -> {
                            // draw bar rectangles
                            g2d.setColor(getIsoBandColor(colorMapper, barColorLevels, index));
                            g2d.setStroke(new BasicStroke());
                            g2d.drawRect(
                                left + (int) Math.round(barLevels.get(index) * width),
                                bottom,
                                (int) Math.round((barLevels.get(index + 1) - barLevels.get(index)) * width),
                                height

                            );
                            g2d.fillRect(left + (int) Math.round(barLevels.get(index) * width),
                                bottom,
                                (int) Math.round((barLevels.get(index + 1) - barLevels.get(index)) * width),
                                height);
                        });
                range(0, scaledLabelLevels.size())
                    .boxed()
                    .forEach(index -> {

                        // display labels
                        String fpTemplate = floatingPointTemplate;
                        if (nonNull(floatingPointTemplates)) {
                            fpTemplate = floatingPointTemplates.get(index);
                        }

                                g2d.setColor(Color.BLACK);
                                int labelYShift = g2d.getFontMetrics().getHeight();

                                // TODO (as above) find a way for avoiding mirror-reflected strings
                                // better than the below scaling and negative sign for y coordinates
                                g2d.scale(1, -1);
                                String label = String.format((Locale) null, fpTemplate, grid2DValues.getFMin() + (grid2DValues.getFMax() - grid2DValues.getFMin()) * scaledLabelLevels.get(index));
                                int labelXShift = g2d.getFontMetrics().stringWidth(label) / 2;
                                g2d.drawString(label, left + (int) Math.round(scaledLabelLevels.get(index) * width) - labelXShift,
                                    -(bottom - barLabelSpacing - labelYShift)
                                );
                                g2d.setStroke(new BasicStroke(barBorderWidth));
                                g2d.drawLine(left + (int) Math.round(scaledLabelLevels.get(index) * width) - 1, -bottom ,
                                    left + (int) Math.round(scaledLabelLevels.get(index) * width) - 1, -(bottom + height / 2));
                                g2d.scale(1, -1);

                    });
            }

            // bar border
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(barBorderWidth));
            g2d.drawRect(left, bottom, width, height);
        };
    }

    private void checkIfRequiredParamsProvided() {
        requireNonNull(isoLevels);
        requireNonNull(rangeMin);
        requireNonNull(rangeMax);
        requireNonNull(colorMapper);
        requireNonNull(grid2DValues);
        requireNonNull(colorBarLocation);
        requireNonNull(left);
        requireNonNull(bottom);
        requireNonNull(width);
        requireNonNull(height);
    }

    private void checkIfFloatingPointTemplatesCorrect() {
        if (nonNull(floatingPointTemplates)) {
            if (!(floatingPointTemplates.size() == isoLevels.size())) {
                LOG.error("Numer of floating point templates inconsistent with number of isolevels");
                throw new IllegalStateException();
            }
        }
    }

    private static List<Double> scaleLabelLevels(List<Double> labelLevels, double dataMin, double dataMax) {
        List<Double> filteredLabelLevels = filterValuesFromDataRange(labelLevels, dataMin, dataMax);
        filteredLabelLevels.sort(naturalOrder());
        return filteredLabelLevels.stream()
            .map(value -> (value - dataMin) / (dataMax - dataMin))
            .collect(toList());
    }

    private static List<Double> filterValuesFromDataRange(List<Double> levels,
                                                          double dataMin, double dataMax) {
        return levels.stream()
            .filter(level -> level <= dataMax)
            .filter(level -> level >= dataMin)
            .collect(toList());
    }

    private Pair<List<Double>, List<Double>> scaleBarLevels(List<Double> isoLevels,
                                                            double dataMin, double dataMax,
                                                            double rangeMin, double rangeMax) {

        int firstInDataRange = getFirstInDataRange(isoLevels, dataMin);
        int lastInDataRange = getLastInDataRange(isoLevels, dataMax);

        List<Double> barLevels = filterValuesFromDataRange(isoLevels, dataMin, dataMax);


        List<Double> barColorLevels = new ArrayList<>(barLevels);
        barLevels.add(dataMin);
        barLevels.add(dataMax);
        barLevels.sort(naturalOrder());

        if (firstInDataRange == 0) {
            barColorLevels.add(rangeMin);
        } else {
            barColorLevels.add(isoLevels.get(firstInDataRange - 1));
        }

        if (lastInDataRange == isoLevels.size() - 1) {
            barColorLevels.add(rangeMax);
        } else {
            barColorLevels.add(isoLevels.get(lastInDataRange + 1));
        }

        barColorLevels.sort(naturalOrder());

        return new Pair<>(
            barLevels.stream().map(value -> (value - dataMin) / (dataMax - dataMin)).collect(toList()),
            barColorLevels.stream().map(value -> (value - rangeMin) / (rangeMax - rangeMin)).collect(toList())
        );
    }

    private Integer getFirstInDataRange(List<Double> isoLevels, double dataMin) {
        for (int i = 0; i < isoLevels.size(); i++) {
            if (isoLevels.get(i) > dataMin) {
                return i;
            }
        }
        return null;
    }

    private Integer getLastInDataRange(List<Double> isoLevels, double dataMax) {
        for (int i = isoLevels.size() - 1; i >= 0; i--) {
            if (isoLevels.get(i) < dataMax) {
                return i;
            }
        }
        return null;
    }

    private List<Double> getContinuousIsoLevels(double min, double max) {
        int dimension = colorBarLocation == BOTTOM ? width : height;
        int numberOfLevels = dimension / 4;
        return rangeClosed(0, numberOfLevels)
            .boxed()
            .map(i -> min + i * (max - min) / numberOfLevels)
            .collect(toList());
    }
}
