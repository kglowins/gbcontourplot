package io.github.kglowins.gbcontourplot.graphics;

import io.github.kglowins.gbcontourplot.graphics.Coordinates2D;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.awt.Color;
import java.util.List;

@Value
@AllArgsConstructor
public class ColoredPolygon {
    private List<Coordinates2D> polygon;
    private Color color;
}
