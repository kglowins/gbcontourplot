package io.github.kglowins.gbcontourplot.grid;

import java.util.Objects;

public class Function2DValue {
    private final double x;
    private final double y;
    private final double f;

    public Function2DValue(double x, double y, double f) {
        this.x = x;
        this.y = y;
        this.f = f;
    }

    public static Function2DValue of(double x, double y, double f) {
        return new Function2DValue(x, y, f);
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double f() {
        return f;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Function2DValue that = (Function2DValue) o;
        return Double.compare(that.x, x) == 0 &&
            Double.compare(that.y, y) == 0 &&
            Double.compare(that.f, f) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, f);
    }

    @Override
    public String toString() {
        return "Function2DValue(" + x + ", " + y + ") = " + f;
    }
}
