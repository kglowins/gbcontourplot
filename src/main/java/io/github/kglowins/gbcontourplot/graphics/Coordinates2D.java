package io.github.kglowins.gbcontourplot.graphics;

import lombok.Value;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Value(staticConstructor = "of")
public class Coordinates2D {
    double x;
    double y;
}
