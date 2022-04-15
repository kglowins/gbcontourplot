package io.github.kglowins.gbcontourplot.graphics;

import java.util.Objects;

public final class Coordinates2D {
    private final double x;
    private final double y;

    public Coordinates2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Coordinates2D of(double x, double y) {
        return new Coordinates2D(x, y);
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Coordinates2D coords = (Coordinates2D) o;
        return Double.compare(coords.x, x) == 0 && Double.compare(coords.y, y) == 0;
    }

    @Override
    public String toString() {
        return "Coordinates2D {" + x + ", " + y + '}';
    }
}
