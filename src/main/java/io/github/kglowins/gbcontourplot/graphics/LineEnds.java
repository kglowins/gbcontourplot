package io.github.kglowins.gbcontourplot.graphics;

import lombok.Value;
import lombok.experimental.Accessors;

@Value(staticConstructor = "of")
@Accessors(fluent = true)
public class LineEnds {
    private double x1;
    private double y1;
    private double x2;
    private double y2;
}
