package io.github.kglowins.gbcontourplot.grid;

import io.github.kglowins.gbcontourplot.graphics.ColoredPolygon;
import io.github.kglowins.gbcontourplot.graphics.LineEnds;
import io.github.kglowins.gbcontourplot.colormappers.ColorMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static io.github.kglowins.gbcontourplot.graphics.PlotUtils.getIsoBandColor;
import static java.util.Comparator.naturalOrder;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

@RequiredArgsConstructor
@Slf4j
public class Grid2DValues {
    private final double xMin;
    private final double yMin;
    private final double xCell;
    private final double yCell;
    private final double[][] values;

    @Getter
    private double fMin;
    @Getter
    private double fMax;

    public List<Grid2DCell> toCells() {
        long startMillis = Instant.now().toEpochMilli();

        List<Grid2DCell> cells = new ArrayList<>();

        int xCells = values.length;
        int yCells = values[0].length;

        fMin = Arrays.stream(values).flatMapToDouble(Arrays::stream).min().getAsDouble();
        fMax = Arrays.stream(values).flatMapToDouble(Arrays::stream).max().getAsDouble();

        range(0, xCells - 1).forEach(xVertex ->
                range(0, yCells - 1).forEach(yVertex -> {
                    cells.add(new Grid2DCell(
                            f2dFromVertexIndexes(xVertex, yVertex + 1),
                            f2dFromVertexIndexes(xVertex + 1, yVertex + 1),
                            f2dFromVertexIndexes(xVertex + 1, yVertex),
                            f2dFromVertexIndexes(xVertex, yVertex)
                    ));
                })
        );

        long finishMillis = Instant.now().toEpochMilli();
        long elapsedMillis = finishMillis - startMillis;
        log.debug("toCells took {} millis.", elapsedMillis);
        return cells;
    }

    public List<LineEnds> toIsoLines(int numberOfIsoLines) {
        fMin = Arrays.stream(values).flatMapToDouble(Arrays::stream).min().getAsDouble();
        fMax = Arrays.stream(values).flatMapToDouble(Arrays::stream).max().getAsDouble();
        double bandWidth = (fMax - fMin) / (numberOfIsoLines + 1);
        List<Double> isoLevels = range(0, numberOfIsoLines).boxed()
            .map(index -> fMin + (index + 1) * bandWidth)
            .collect(toList());
        log.debug("isolev = {}", isoLevels);
        return toIsoLines(isoLevels);
    }

    public List<LineEnds> toIsoLines(List<Double> isoLevels) {
        List<Grid2DCell> cells = toCells();
        return isoLevels.stream()
            .flatMap(isoLevel -> isoLinesOfOneLevel(isoLevel, cells))
            .collect(toList());
    }

    public List<Double> getAutoIsoLevels(int numberOfIsoLines) {
        double bandWidth = (fMax - fMin) / (numberOfIsoLines + 1);
        log.debug("fmin = {} fmax = {}", fMin, fMax);
        List<Double> isoLevels = range(0, numberOfIsoLines).boxed()
            .map(index -> fMin + (index + 1) * bandWidth)
            .collect(toList());
        log.debug("{}", isoLevels);
        return isoLevels;
    }

    public List<ColoredPolygon> toIsoBands(int numberOfIsoLines, ColorMapper colorMapper) {
        return toIsoBands(getAutoIsoLevels(numberOfIsoLines), colorMapper, fMin, fMax);
    }

    public List<ColoredPolygon> toIsoBands(List<Double> isoLevels, ColorMapper colorMapper) {
        return toIsoBands(isoLevels, colorMapper, fMin, fMax);
    }

    public List<ColoredPolygon> toIsoBands(List<Double> isoLevels, ColorMapper colorMapper,
                                           double rangeMin, double rangeMax) {
        List<Grid2DCell> cells = toCells();

        double minIsoLevel = isoLevels.stream().mapToDouble(Double::doubleValue).min().getAsDouble();
        double maxIsoLevel = isoLevels.stream().mapToDouble(Double::doubleValue).max().getAsDouble();

        if (rangeMax < rangeMin) {
            throw new IllegalArgumentException("rangeMax must be > rangeMin");
        }
        // Below constraints assume that the "color" range can only be wider than the actual data range
        if (rangeMin > minIsoLevel) {
            throw new IllegalArgumentException("rangeMin must be <= min(IsoLevels)");
        }
        if (rangeMax < maxIsoLevel) {
            throw new IllegalArgumentException("rangeMax must be >= max(IsoLevels)");
        }

        List<Double> bandsLevels = new ArrayList<>(isoLevels);
        bandsLevels.add(Double.MIN_VALUE);
        bandsLevels.add(Double.MAX_VALUE);
        bandsLevels.sort(naturalOrder());

        List<Double> scaledlevels = getScaledLevelsForColors(isoLevels, rangeMin, rangeMax);

        return range(0, isoLevels.size() + 1)
            .boxed()
            .flatMap(index -> {
                Color isoBandColor = getIsoBandColor(colorMapper, scaledlevels, index);
                return isoBandsBetweenTwoLevels(
                    bandsLevels.get(index), bandsLevels.get(index + 1),
                    cells,
                    isoBandColor);
            })
            .collect(toList());
    }

    private Stream<ColoredPolygon> isoBandsBetweenTwoLevels(double lowerLevel, double upperLevel,
                                                            List<Grid2DCell> cells, Color color) {
        return cells.stream()
            .flatMap(cell -> cell.toPolygons(lowerLevel, upperLevel).stream())
            .map(polygon -> new ColoredPolygon(polygon, color));
    }

    private Stream<LineEnds> isoLinesOfOneLevel(double isoLevel, List<Grid2DCell> cells) {
        return cells.stream().flatMap(cell -> cell.toLineEnds(isoLevel).stream());
    }

    private Function2DValue f2dFromVertexIndexes(int xIndex, int yIndex) {
        return Function2DValue.of(xMin + xIndex * xCell, yMin + yIndex * yCell, values[xIndex][yIndex]);
    }

    private List<Double> getScaledLevelsForColors(List<Double> isoLevels, double rangeMin, double rangeMax) {
        List<Double> levelsForColors = new ArrayList<>(isoLevels);
        levelsForColors.add(rangeMin);
        levelsForColors.add(rangeMax);
        levelsForColors.sort(naturalOrder());
        return levelsForColors.stream()
            .map(value -> (value - rangeMin) / (rangeMax - rangeMin))
            .collect(toList());
    }
}
