package io.github.kglowins.gbcontourplot.graphics;

import java.awt.Color;
import java.util.List;
import java.util.Objects;

public final class ColoredPolygon {
    private final List<Coordinates2D> polygon;
    private final Color color;

    public ColoredPolygon(List<Coordinates2D> polygon, Color color) {
        this.polygon = List.copyOf(polygon);
        this.color = color;
    }

    public List<Coordinates2D> getPolygon() {
        return polygon;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ColoredPolygon that = (ColoredPolygon) o;
        return polygon.equals(that.polygon) &&
            color.equals(that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(polygon, color);
    }

    @Override
    public String toString() {
        return "ColoredPolygon{" +
            "polygon=" + polygon +
            ", color=" + color +
            '}';
    }
}
