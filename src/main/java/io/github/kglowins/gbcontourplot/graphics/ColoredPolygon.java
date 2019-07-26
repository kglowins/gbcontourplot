package io.github.kglowins.gbcontourplot.graphics;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.awt.Color;
import java.util.List;

@Value
@AllArgsConstructor
public class ColoredPolygon {
    List<Coordinates2D> polygon;
    Color color;
}
