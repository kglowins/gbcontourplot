package io.github.kglowins.gbcontourplot.graphics;

import lombok.Value;
import lombok.experimental.Accessors;

@Value(staticConstructor = "of")
@Accessors(fluent = true)
public class LineEnds {
    double x1;
    double y1;
    double x2;
    double y2;
}
