package io.github.kglowins.gbcontourplot.graphics;

import java.util.Objects;

public final class LineEnds {
    private final double x1;
    private final double y1;
    private final double x2;
    private final double y2;

    public LineEnds(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public static LineEnds of(double x1, double y1, double x2, double y2) {
        return new LineEnds(x1, y1, x2, y2);
    }

    public double x1() {
        return x1;
    }

    public double y1() {
        return y1;
    }

    public double x2() {
        return x2;
    }

    public double y2() {
        return y2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LineEnds lineEnds = (LineEnds) o;
        return Double.compare(lineEnds.x1, x1) == 0 &&
            Double.compare(lineEnds.y1, y1) == 0 &&
            Double.compare(lineEnds.x2, x2) == 0 &&
            Double.compare(lineEnds.y2, y2) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x1, y1, x2, y2);
    }

    @Override
    public String toString() {
        return "LineEnds {" + x1 + ", " + y1 + "} -> {" + x2 + ", " + y2 + '}';
    }
}
