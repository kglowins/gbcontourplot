package io.github.kglowins.gbcontourplot.graphics;

import lombok.Value;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Value(staticConstructor = "of")
public class Coordinates2D {
    private double x;
    private double y;
}
