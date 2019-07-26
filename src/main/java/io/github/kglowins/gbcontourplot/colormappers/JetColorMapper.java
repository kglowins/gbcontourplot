package io.github.kglowins.gbcontourplot.colormappers;


import java.awt.Color;

public class JetColorMapper implements ColorMapper {

    private boolean inverse;

    public JetColorMapper() {
        this.inverse = false;
    }

    public JetColorMapper(boolean inverse) {
        this.inverse = inverse;
    }

    @Override
    public Color map(double value) {
        return inverse ? new Color(red(1 - value), green(1 - value), blue(1 - value))
         : new Color(red(value), green(value), blue(value));
    }

    private float castDoubleToFloat(double d) {
        float f = (float) d;
        if (f > 1f) return 1f;
        if (f < 0f) return 0f;
        return f;
    }

    private float red(double value) {
        if (value <= 0.375) {
            return 0f;
        } else if (value > 0.375 && value <= 0.625) {
            return castDoubleToFloat(-1.5 + 4 * value);
        } else if (value > 0.625 && value <= 0.875) {
            return 1f;
        } else {
            return castDoubleToFloat(-3 * value + 3.625);
        }
    }

    private float green(double value) {
        if (value <= 0.125) {
            return 0f;
        } else if (value > 0.125 && value <= 0.375) {
            return castDoubleToFloat(4 * value - 0.5);
        } else if (value > 0.375 && value <= 0.625) {
            return 1f;
        } else if (value > 0.625 && value <= 0.875) {
            return castDoubleToFloat(3.5 - 4 * value);
        } else {
            return 0f;
        }
    }

    private float blue(double value) {
        if (value <= 0.125) {
            return castDoubleToFloat(0.625 + 3 * value);
        } else if (value > 0.125 && value <= 0.375) {
            return 1f;
        } else if (value > 0.375 && value <= 0.625) {
            return castDoubleToFloat(2.5 - 4 * value);
        } else {
            return 0f;
        }
    }
}
