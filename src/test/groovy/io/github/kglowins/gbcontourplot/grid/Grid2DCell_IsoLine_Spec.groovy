package io.github.kglowins.gbcontourplot.grid


import groovy.util.logging.Slf4j
import io.github.kglowins.gbcontourplot.graphics.LineEnds
import io.github.kglowins.gbcontourplot.grid.Function2DValue
import io.github.kglowins.gbcontourplot.grid.Grid2DCell
import spock.lang.Specification
import spock.lang.Unroll

@Slf4j
class Grid2DCell_IsoLine_Spec extends Specification {

    private static final double EPSILON = 1e-6;

    @Unroll
    def "should recognize cell as case #expectedCase and generate correct lines"(cell, isoLevel, expectedCase, expectedLines) {
        when:
        def caseIndex = cell.getIsoLineCase(isoLevel)
        def lines = cell.toLineEnds(isoLevel)
        log.debug("caseIndex = {} (expected = {})", caseIndex, expectedCase)
        log.debug("lines = {}", lines)

        then:
        caseIndex == expectedCase
        areLineSetsEquivalent(lines, expectedLines)

        where:
        cell                 | isoLevel | expectedCase | expectedLines
        testCell(0, 0, 0, 0) | 1        | 0            | [ ]
        testCell(0, 0, 0, 1) | 0.5      | 1            | [LineEnds.of(0, 0.5, 0.5, 0) ]
        testCell(0, 0, 4, 2) | 3        | 2            | [ LineEnds.of(0.5, 0, 1, 0.25) ]
        testCell(0, 1, 5, 6) | 4        | 3            | [ LineEnds.of(0, 1/3, 1, 0.25) ]
        testCell(3, 7, 2, 3) | 5        | 4            | [ LineEnds.of(0.5, 1, 1, 0.6) ]
        testCell(2, 8, 3, 6) | 4        | 5            | [ LineEnds.of(0, 0.5, 1/3, 1), LineEnds.of(2/3, 0, 1, 0.2)]
        testCell(0, 7, 9, 1) | 2        | 6            | [ LineEnds.of(1/8, 0, 2/7, 1) ]
        testCell(0, 6, 4, 7) | 1        | 7            | [ LineEnds.of(0, 6/7, 1/6, 1) ]
        testCell(9, 0, 0, 0) | 8        | 8            | [ LineEnds.of(0, 8/9, 1/9, 1) ]
        testCell(1, 0, 0, 1) | 0.73     | 9            | [ LineEnds.of(0.27, 0, 0.27, 1)]
        testCell(3, 1, 3, 0) | 1.5      | 10           | [ LineEnds.of(0, 0.5, 0.5, 0), LineEnds.of(0.75, 1, 1, 0.75) ]
        testCell(1, 0, 1, 1) | 0.14     | 11           | [ LineEnds.of(0.86, 1, 1, 0.86) ]
        testCell(2, 7, 0, 1) | 1.5      | 12           | [ LineEnds.of(0, 0.5, 1, 3/14) ]
        testCell(9, 9, 7, 8) | 7.01     | 13           | [ LineEnds.of(0.99, 0, 1, 0.005) ]
        testCell(1, 2, 2, 0) | 0.06     | 14           | [ LineEnds.of(0, 0.06, 0.03, 0) ]
        testCell(0, 0, 0, 0) | -1       | 15           | [ ]
    }

    private static def testCell(topLeft, topRight, bottomRight, bottomLeft) {
        new Grid2DCell(
                Function2DValue.of(0, 1, topLeft),
                Function2DValue.of(1, 1, topRight),
                Function2DValue.of(1, 0, bottomRight),
                Function2DValue.of(0, 0, bottomLeft)
        )
    }

    private static def areLineSetsEquivalent(List<LineEnds> list1, List<LineEnds> list2) {
          lineSetContainsAll(list2, list1) && lineSetContainsAll(list1, list2)
    }

    private static def lineSetContainsAll(List<LineEnds> list1, List<LineEnds> list2) {
        for (it in list2) {
            if (!lineSetContains(list1, it)) {
                return false
            }
        }
        true
    }

    private static def lineSetContains(List<LineEnds> list, LineEnds line) {
        for (it in list) {
            if (areLinesEquivalent(it, line)) {
                return true
            }
        }
        false
    }

    private static def areLinesEquivalent(LineEnds line1, LineEnds line2) {
        ((areEndsEquivalent(line1.x1, line1.y1, line2.x1, line2.y1) && areEndsEquivalent(line1.x2, line1.y2, line2.x2, line2.y2))
                || (areEndsEquivalent(line1.x1, line1.y1, line2.x2, line2.y2) && areEndsEquivalent(line1.x2, line1.y2, line2.x1, line2.y1)))
    }

    private static def areEndsEquivalent(double x1, double y1, double x2, double y2) {
        areClose(x1, x2) && areClose(y1, y2)
    }

    private static def areClose(double d1, double d2) {
        Math.abs(d1 - d2) < EPSILON
    }
}
