package io.github.kglowins.gbcontourplot.colormappers;


import lombok.extern.slf4j.Slf4j;

import java.awt.Color;

import static java.util.stream.IntStream.range;

@Slf4j
public class TableBasedColorMapper implements ColorMapper {

    private double[][] rgbValues;

    public TableBasedColorMapper(String colorMapTableName) {
        rgbValues = deepCopy(ColorMapTable.valueOf(colorMapTableName).getRgbValues());
    }

    public TableBasedColorMapper(String colorMapTableName, boolean invert) {
        rgbValues = invert ? invertedDeepCopy(ColorMapTable.valueOf(colorMapTableName).getRgbValues())
            : deepCopy(ColorMapTable.valueOf(colorMapTableName).getRgbValues());
    }

    @Override
    public Color map(double value) {
        int index = Math.min(rgbValues.length - 1, (int) Math.round(value * rgbValues.length));
        return new Color((float) rgbValues[index][0], (float) rgbValues[index][1], (float) rgbValues[index][2]);
    }

    private double[][] deepCopy(double[][] a) {
        double[][] copy = new double[a.length][];
        range(0, a.length).forEach(i -> {
            copy[i] = new double[a[i].length];
            range(0, a[i].length).forEach(j -> copy[i][j] = a[i][j]);
        });
        return copy;
    }

    private double[][] invertedDeepCopy(double[][] a) {
        double[][] copy = new double[a.length][];
        range(0, a.length).forEach(i -> {
            copy[i] = new double[a[a.length - 1 - i].length];
            range(0, a[a.length - 1 - i].length).forEach(j -> copy[i][j] = a[a.length - 1 - i][j]);
        });
        return copy;
    }
}
