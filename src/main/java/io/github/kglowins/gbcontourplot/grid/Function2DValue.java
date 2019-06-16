package io.github.kglowins.gbcontourplot.grid;

import lombok.Value;
import lombok.experimental.Accessors;

@Value(staticConstructor = "of")
@Accessors(fluent = true)
public class Function2DValue {
    private double x;
    private double y;
    private double f;
}
