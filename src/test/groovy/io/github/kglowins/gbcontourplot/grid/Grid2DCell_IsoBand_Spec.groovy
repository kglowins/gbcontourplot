package io.github.kglowins.gbcontourplot.grid


import groovy.util.logging.Slf4j
import io.github.kglowins.gbcontourplot.graphics.Coordinates2D
import io.github.kglowins.gbcontourplot.graphics.LineEnds
import io.github.kglowins.gbcontourplot.grid.Function2DValue
import io.github.kglowins.gbcontourplot.grid.Grid2DCell
import spock.lang.Specification
import spock.lang.Unroll

@Slf4j
class Grid2DCell_IsoBand_Spec extends Specification {

    private static final double EPSILON = 1e-6;

    @Unroll
    def "should recognize cell as case #expectCase and generate polygon"(cell, loLvl, upLvl,
                                                                         expectCase, expectedPolygons) {
        when:
        def caseIndex = cell.getIsoBandCase(loLvl, upLvl)
        def polygons = cell.toPolygons(loLvl, upLvl)
        log.debug("caseIndex = {} (expected = {})", caseIndex, expectCase)
        log.debug("polygons = {}", polygons)

        then:
        caseIndex == expectCase
        arePolygonSetsEquivalent(polygons, expectedPolygons)

        where:
        cell                     | loLvl | upLvl | expectCase | expectedPolygons
        testCell(0, 0, 0, 0)     | 1     | 2     | 0          | []
        testCell(3, 3, 3, 3)     | 1     | 2     | 80         | []

        // single triangles
        testCell(0, 0, 0, 2)     | 1     | 3     | 1          | [[p(0, 0.5), p(0.5, 0), p(0, 0)]]
        testCell(4, 4, 4, 2)     | 1     | 3     | 79         | [[p(0, 0.5), p(0.5, 0), p(0, 0)]]

        testCell(0, 0, 2, 0)     | 1     | 3     | 3          | [[p(1, 0.5), p(1, 0), p(0.5, 0)]]
        testCell(4, 4, 2, 4)     | 1     | 3     | 77         | [[p(1, 0.5), p(1, 0), p(0.5, 0)]]

        testCell(0, 2, 0, 0)     | 1     | 3     | 9          | [[p(0.5, 1), p(1, 1), p(1, 0.5)]]
        testCell(4, 2, 4, 4)     | 1     | 3     | 71         | [[p(0.5, 1), p(1, 1), p(1, 0.5)]]

        testCell(2, 0, 0, 0)     | 1     | 3     | 27         | [[p(0, 1), p(0.5, 1), p(0, 0.5)]]
        testCell(2, 4, 4, 4)     | 1     | 3     | 53         | [[p(0, 1), p(0.5, 1), p(0, 0.5)]]

        // single trapezoid
        testCell(1, 1, 1, 4)     | 2     | 3     | 2          | [[p(0, 2 / 3), p(2 / 3, 0), p(1 / 3, 0), p(0, 1 / 3)]]
        testCell(4, 4, 4, 1)     | 2     | 3     | 78         | [[p(0, 2 / 3), p(2 / 3, 0), p(1 / 3, 0), p(0, 1 / 3)]]

        testCell(1, 1, 4, 1)     | 2     | 3     | 6          | [[p(1, 2 / 3), p(1, 1 / 3), p(2 / 3, 0), p(1 / 3, 0)]]
        testCell(4, 4, 1, 4)     | 2     | 3     | 74         | [[p(1, 2 / 3), p(1, 1 / 3), p(2 / 3, 0), p(1 / 3, 0)]]

        testCell(1, 4, 1, 1)     | 2     | 3     | 18         | [[p(1 / 3, 1), p(2 / 3, 1), p(1, 2 / 3), p(1, 1 / 3)]]
        testCell(4, 1, 4, 4)     | 2     | 3     | 62         | [[p(1 / 3, 1), p(2 / 3, 1), p(1, 2 / 3), p(1, 1 / 3)]]

        testCell(4, 1, 1, 1)     | 2     | 3     | 54         | [[p(1 / 3, 1), p(2 / 3, 1), p(0, 1 / 3), p(0, 2 / 3)]]
        testCell(1, 4, 4, 4)     | 2     | 3     | 26         | [[p(1 / 3, 1), p(2 / 3, 1), p(0, 1 / 3), p(0, 2 / 3)]]

        //square
        testCell(2, 2, 2, 2)     | 1     | 3     | 40         | [[p(0, 1), p(1, 1), p(1, 0), p(0, 0)]]

        //rectangles
        testCell(1, 0, 3, 6)     | 2     | 7     | 4          | [[p(0, 0.8), p(1, 1 / 3), p(1, 0), p(0, 0)]]
        testCell(8, 9, 3, 6)     | 2     | 7     | 76         | [[p(0, 0.5), p(1, 2 / 3), p(1, 0), p(0, 0)]]

        testCell(3, 3, 1, 1)     | 2     | 4     | 36         | [[p(0, 1), p(1, 1), p(1, 0.5), p(0, 0.5)]]
        testCell(3, 3, 5, 5)     | 2     | 4     | 44         | [[p(0, 1), p(1, 1), p(1, 0.5), p(0, 0.5)]]

        testCell(1, 3, 3, 1)     | 2     | 4     | 12         | [[p(0.5, 1), p(1, 1), p(1, 0), p(0.5, 0)]]
        testCell(5, 3, 3, 5)     | 2     | 4     | 68         | [[p(0.5, 1), p(1, 1), p(1, 0), p(0.5, 0)]]

        testCell(3, 1, 1, 3)     | 2     | 4     | 28         | [[p(0, 1), p(0.5, 1), p(0.5, 0), p(0, 0)]]
        testCell(3, 5, 5, 3)     | 2     | 4     | 52         | [[p(0, 1), p(0.5, 1), p(0.5, 0), p(0, 0)]]

        testCell(5, 5, 1, 1)     | 2     | 3     | 72         | [[p(0, 0.5), p(1, 0.5), p(1, 0.25), p(0, 0.25)]]
        testCell(1, 1, 5, 5)     | 2     | 3     | 8          | [[p(0, 0.75), p(1, 0.75), p(1, 0.5), p(0, 0.5)]]

        testCell(5, 1, 1, 5)     | 2     | 3     | 56         | [[p(0.5, 1), p(0.75, 1), p(0.75, 0), p(0.5, 0)]]
        testCell(1, 5, 5, 1)     | 2     | 3     | 24         | [[p(0.25, 1), p(0.5, 1), p(0.5, 0), p(0.25, 0)]]

        //pentagons
        testCell(3, 5, 3, 3)     | 2     | 4     | 49         | [[p(0, 1), p(0.5, 1), p(1, 0.5), p(1, 0), p(0, 0)]]
        testCell(3, 1, 3, 3)     | 2     | 4     | 31         | [[p(0, 1), p(0.5, 1), p(1, 0.5), p(1, 0), p(0, 0)]]

        testCell(5, 3, 3, 3)     | 2     | 4     | 67         | [[p(0.5, 1), p(1, 1), p(1, 0), p(0, 0), p(0, 0.5)]]
        testCell(1, 3, 3, 3)     | 2     | 4     | 13         | [[p(0.5, 1), p(1, 1), p(1, 0), p(0, 0), p(0, 0.5)]]

        testCell(3, 3, 3, 5)     | 2     | 4     | 41         | [[p(0, 1), p(1, 1), p(1, 0), p(0.5, 0), p(0, 0.5)]]
        testCell(3, 3, 3, 1)     | 2     | 4     | 39         | [[p(0, 1), p(1, 1), p(1, 0), p(0.5, 0), p(0, 0.5)]]

        testCell(3, 3, 5, 3)     | 2     | 4     | 43         | [[p(0, 1), p(1, 1), p(1, 0.5), p(0.5, 0), p(0, 0)]]
        testCell(3, 3, 1, 3)     | 2     | 4     | 37         | [[p(0, 1), p(1, 1), p(1, 0.5), p(0.5, 0), p(0, 0)]]

        testCell(3, 5, 1, 1)     | 2     | 4     | 45         | [[p(0, 1), p(0.5, 1), p(1, 0.75), p(1, 0.25), p(0, 0.5)]]
        testCell(3, 1, 5, 5)     | 2     | 4     | 35         | [[p(0, 1), p(0.5, 1), p(1, 0.75), p(1, 0.25), p(0, 0.5)]]

        testCell(1, 3, 5, 1)     | 2     | 4     | 15         | [[p(0.5, 1), p(1, 1), p(1, 0.5), p(0.75, 0), p(0.25, 0)]]
        testCell(5, 3, 1, 5)     | 2     | 4     | 65         | [[p(0.5, 1), p(1, 1), p(1, 0.5), p(0.75, 0), p(0.25, 0)]]

        testCell(1, 1, 3, 5)     | 2     | 4     | 5          | [[p(0, 0.75), p(1, 0.5), p(1, 0), p(0.5, 0), p(0, 0.25)]]
        testCell(5, 5, 3, 1)     | 2     | 4     | 75         | [[p(0, 0.75), p(1, 0.5), p(1, 0), p(0.5, 0), p(0, 0.25)]]

        testCell(5, 1, 1, 3)     | 2     | 4     | 55         | [[p(0.25, 1), p(0.75, 1), p(0.5, 0), p(0, 0), p(0, 0.5)]]
        testCell(1, 5, 5, 3)     | 2     | 4     | 25         | [[p(0.25, 1), p(0.75, 1), p(0.5, 0), p(0, 0), p(0, 0.5)]]

        testCell(3, 1, 1, 5)     | 2     | 4     | 29         | [[p(0, 1), p(0.5, 1), p(0.75, 0), p(0.25, 0), p(0, 0.5)]]
        testCell(3, 5, 5, 1)     | 2     | 4     | 51         | [[p(0, 1), p(0.5, 1), p(0.75, 0), p(0.25, 0), p(0, 0.5)]]

        testCell(5, 3, 1, 1)     | 2     | 4     | 63         | [[p(0.5, 1), p(1, 1), p(1, 0.5), p(0, 0.25), p(0, 0.75)]]
        testCell(1, 3, 5, 5)     | 2     | 4     | 17         | [[p(0.5, 1), p(1, 1), p(1, 0.5), p(0, 0.25), p(0, 0.75)]]

        testCell(1, 5, 3, 1)     | 2     | 4     | 21         | [[p(0.25, 1), p(0.75, 1), p(1, 0.5), p(1, 0), p(0.5, 0)]]
        testCell(5, 1, 3, 5)     | 2     | 4     | 59         | [[p(0.25, 1), p(0.75, 1), p(1, 0.5), p(1, 0), p(0.5, 0)]]

        testCell(1, 1, 5, 3)     | 2     | 4     | 7          | [[p(0, 0.5), p(1, 0.75), p(1, 0.25), p(0.5, 0), p(0, 0)]]
        testCell(5, 5, 1, 3)     | 2     | 4     | 73         | [[p(0, 0.5), p(1, 0.75), p(1, 0.25), p(0.5, 0), p(0, 0)]]

        //hexagons
        testCell(1, 5, 3, 3)     | 2     | 4     | 22         | [[p(0, 0.5), p(0.25, 1), p(0.75, 1), p(1, 0.5), p(1, 0), p(0, 0)]]
        testCell(5, 1, 3, 3)     | 2     | 4     | 58         | [[p(0, 0.5), p(0.25, 1), p(0.75, 1), p(1, 0.5), p(1, 0), p(0, 0)]]

        testCell(5, 3, 3, 1)     | 2     | 4     | 66         | [[p(0, 0.75), p(0.5, 1), p(1, 1), p(1, 0), p(0.5, 0), p(0, 0.25)]]
        testCell(1, 3, 3, 5)     | 2     | 4     | 14         | [[p(0, 0.75), p(0.5, 1), p(1, 1), p(1, 0), p(0.5, 0), p(0, 0.25)]]

        testCell(3, 3, 1, 5)     | 2     | 4     | 38         | [[p(0, 1), p(1, 1), p(1, 0.5), p(0.75, 0), p(0.25, 0), p(0, 0.5)]]
        testCell(3, 3, 5, 1)     | 2     | 4     | 42         | [[p(0, 1), p(1, 1), p(1, 0.5), p(0.75, 0), p(0.25, 0), p(0, 0.5)]]

        testCell(3, 1, 5, 3)     | 2     | 4     | 34         | [[p(0, 1), p(0.5, 1), p(1, 0.75), p(1, 0.25), p(0.5, 0), p(0, 0)]]
        testCell(3, 5, 1, 3)     | 2     | 4     | 46         | [[p(0, 1), p(0.5, 1), p(1, 0.75), p(1, 0.25), p(0.5, 0), p(0, 0)]]


        testCell(5, 3, 1, 3)     | 2     | 4     | 64         | [[p(0, 0.5), p(0.5, 1), p(1, 1), p(1, 0.5), p(0.5, 0), p(0, 0)]]
        testCell(1, 3, 5, 3)     | 2     | 4     | 16         | [[p(0, 0.5), p(0.5, 1), p(1, 1), p(1, 0.5), p(0.5, 0), p(0, 0)]]

        testCell(3, 1, 3, 5)     | 2     | 4     | 32         | [[p(0, 1), p(0.5, 1), p(1, 0.5), p(1, 0), p(0.5, 0), p(0, 0.5)]]
        testCell(3, 5, 3, 1)     | 2     | 4     | 48         | [[p(0, 1), p(0.5, 1), p(1, 0.5), p(1, 0), p(0.5, 0), p(0, 0.5)]]

        //saddles

        //8-sided
        testCell(5, 1, 5, 1)     | 2     | 4     | 60         | [[p(0, 3 / 4), p(1 / 4, 1), p(3 / 4, 1), p(1, 3 / 4), p(1, 1 / 4), p(3 / 4, 0), p(1 / 4, 0), p(0, 1 / 4)]]
        testCell(5, -3, 5, -3)   | 2     | 4     | 60         | [[p(1 / 8, 1), p(3 / 8, 1), p(0, 5 / 8), p(0, 7 / 8)], [p(1, 3 / 8), p(1, 1 / 8), p(7 / 8, 0), p(5 / 8, 0)]]
        testCell(9, 1, 9, 1)     | 2     | 4     | 60         | [[p(0, 3 / 8), p(3 / 8, 0), p(1 / 8, 0), p(0, 1 / 8)], [p(5 / 8, 1), p(7 / 8, 1), p(1, 7 / 8), p(1, 5 / 8)]]

        testCell(1, 5, 1, 5)     | 2     | 4     | 20         | [[p(0, 3 / 4), p(1 / 4, 1), p(3 / 4, 1), p(1, 3 / 4), p(1, 1 / 4), p(3 / 4, 0), p(1 / 4, 0), p(0, 1 / 4)]]
        testCell(-3, 5, -3, 5)   | 2     | 4     | 20         | [[p(0, 3 / 8), p(3 / 8, 0), p(1 / 8, 0), p(0, 1 / 8)], [p(5 / 8, 1), p(7 / 8, 1), p(1, 7 / 8), p(1, 5 / 8)]]
        testCell(1, 9, 1, 9)     | 2     | 4     | 20         | [[p(1 / 8, 1), p(3 / 8, 1), p(0, 5 / 8), p(0, 7 / 8)], [p(1, 3 / 8), p(1, 1 / 8), p(7 / 8, 0), p(5 / 8, 0)]]

        //6-sided
        testCell(1.5, 3, 1.5, 3) | 2     | 4     | 10         | [[p(0, 2/3), p(1/3, 1), p(1, 1), p(1, 1/3), p(2/3, 0), p(0, 0)]]
        testCell(0, 3, 0, 3)     | 2     | 4     | 10         | [[p(0, 1/3), p(1/3, 0), p(0, 0)], [p(2/3, 1), p(1, 1), p(1, 2/3)]]

        testCell(5, 2, 5, 2)     | 2     | 4     | 70         | [[p(0, 2/3), p(1/3, 1), p(1, 1), p(1, 1/3), p(2/3, 0), p(0, 0)]]
        testCell(6, 3, 6, 3)     | 2     | 4     | 70         | [[p(0, 1/3), p(1/3, 0), p(0, 0)], [p(2/3, 1), p(1, 1), p(1, 2/3)]]


        testCell(3, 1.5, 3, 1.5) | 2     | 4     | 30         | [[p(0, 1), p(2/3, 1), p(1, 2/3), p(1, 0), p(1/3, 0), p(0, 1/3)]]
        testCell(3, 0, 3, 0)     | 2     | 4     | 30         | [[p(0, 1), p(1/3, 1), p(0, 2/3)], [p(1, 1/3), p(1, 0), p(2/3, 0)]]

        testCell(2, 5, 2, 5)     | 2     | 4     | 50         | [[p(0, 1), p(2/3, 1), p(1, 2/3), p(1, 0), p(1/3, 0), p(0, 1/3)]]
        testCell(3, 6, 3, 6)     | 2     | 4     | 50         | [[p(0, 1), p(1/3, 1), p(0, 2/3)], [p(1, 1/3), p(1, 0), p(2/3, 0)]]

        //7-sided
        testCell(5, 3, 5, 1)     | 2     | 4     | 69         | [[p(0.5, 1), p(1, 1), p(1, 0.5), p(0.75, 0), p(0.25, 0), p(0, 0.25), p(0,0.75)]]
        testCell(7, 3, 7, 1)     | 2     | 4     | 69         | [[p(0.75, 1), p(1, 1), p(1, 0.75)], [p(0, 0.5), p(0.5, 0), p(1/6, 0), p(0, 1/6)]]

        testCell(1, 3, 1, 5)     | 2     | 4     | 11         | [[p(0.5, 1), p(1, 1), p(1, 0.5), p(0.75, 0), p(0.25, 0), p(0, 0.25), p(0,0.75)]]
        testCell(-1, 3, -1, 5)   | 2     | 4     | 11         | [[p(0.75, 1), p(1, 1), p(1, 0.75)], [p(0, 0.5), p(0.5, 0), p(1/6, 0), p(0, 1/6)]]

        testCell(5, 1, 5, 3)     | 2     | 4     | 61         | [[p(0.25, 1), p(0.75, 1), p(1, 0.75), p(1, 0.25), p(0.5, 0), p(0, 0), p(0,0.5)]]
        testCell(7, 1, 7, 3)     | 2     | 4     | 61         | [[p(0, 0.25), p(0.25, 0), p(0, 0)], [p(0.5, 1), p(5/6, 1), p(1, 5/6), p(1, 0.5)]]

        testCell(1, 5, 1, 3)     | 2     | 4     | 19         | [[p(0.25, 1), p(0.75, 1), p(1, 0.75), p(1, 0.25), p(0.5, 0), p(0, 0), p(0,0.5)]]
        testCell(-1, 5, -1, 3)   | 2     | 4     | 19         | [[p(0, 0.25), p(0.25, 0), p(0, 0)], [p(0.5, 1), p(5/6, 1), p(1, 5/6), p(1, 0.5)]]

        testCell(3, 5, 1, 5)     | 2     | 4     | 47         | [[p(0, 1), p(0.5, 1), p(1, 0.75), p(1, 0.25), p(0.75, 0), p(0.25 ,0), p(0,0.5)]]
        testCell(3, 7, 1, 7)     | 2     | 4     | 47         | [[p(0, 1), p(0.25, 1), p(0, 0.75)], [p(1, 0.5), p(1, 1/6), p(5/6, 0), p(0.5, 0)]]

        testCell(3, 1, 5, 1)     | 2     | 4     | 33         | [[p(0, 1), p(0.5, 1), p(1, 0.75), p(1, 0.25), p(0.75, 0), p(0.25 ,0), p(0,0.5)]]
        testCell(3, -1, 5, -1)   | 2     | 4     | 33         | [[p(0, 1), p(0.25, 1), p(0, 0.75)], [p(1, 0.5), p(1, 1/6), p(5/6, 0), p(0.5, 0)]]

        testCell(1, 5, 3, 5)     | 2     | 4     | 23         | [[p(0.25, 1), p(0.75, 1), p(1, 0.5), p(1, 0), p(0.5, 0), p(0, 0.25), p(0, 0.75)]]
        testCell(1, 7, 3, 7)     | 2     | 4     | 23         | [[p(1, 0.25), p(1, 0), p(0.75, 0)], [p(1/6, 1), p(0.5, 1), p(0, 0.5), p(0, 5/6)]]

        testCell(5, 1, 3, 1)     | 2     | 4     | 57         | [[p(0.25, 1), p(0.75, 1), p(1, 0.5), p(1, 0), p(0.5, 0), p(0, 0.25), p(0, 0.75)]]
        testCell(5, -1, 3, -1)   | 2     | 4     | 57         | [[p(1, 0.25), p(1, 0), p(0.75, 0)], [p(1/6, 1), p(0.5, 1), p(0, 0.5), p(0, 5/6)]]
    }

    private static def testCell(topLeft, topRight, bottomRight, bottomLeft) {
        new Grid2DCell(
                Function2DValue.of(0, 1, topLeft),
                Function2DValue.of(1, 1, topRight),
                Function2DValue.of(1, 0, bottomRight),
                Function2DValue.of(0, 0, bottomLeft)
        )
    }

    private static def p(x, y) {
        Coordinates2D.of(x, y)
    }

    private static def arePolygonSetsEquivalent(List<List<Coordinates2D>> polys1,
                                                List<List<Coordinates2D>> polys2) {
        polygonSetContainsAll(polys1, polys2) && polygonSetContainsAll(polys2, polys1)
    }

    private static def areLineSetsEquivalent(List<LineEnds> list1, List<LineEnds> list2) {
        lineSetContainsAll(list2, list1) && lineSetContainsAll(list1, list2)
    }


    private static def polygonSetContainsAll(List<List<Coordinates2D>> polys1,
                                             List<List<Coordinates2D>> polys2) {
        for (it in polys2) {
            if (!polygonSetContains(polys1, it)) {
                return false
            }
        }
        true
    }

    private static def polygonSetContains(List<List<Coordinates2D>> polygons, List<Coordinates2D> polygon) {
        for (it in polygons) {
            if (arePolygonsEquivalent(it, polygon)) {
                return true
            }
        }
        false
    }

    private static def arePolygonsEquivalent(List<Coordinates2D> poly1, List<Coordinates2D> poly2) {
        if (poly1.size() != poly2.size()) {
            return false
        }
        for (ptIndex in 0..<poly1.size()) {
            if (!areCoordinatesEquivalent(poly1.get(ptIndex), poly2.get(ptIndex))) {
                return false
            }
        }
        true
    }

    private static def areCoordinatesEquivalent(Coordinates2D c1, Coordinates2D c2) {
        areClose(c1.x(), c2.x()) && areClose(c1.y(), c2.y())
    }

    private static def areClose(double d1, double d2) {
        Math.abs(d1 - d2) < EPSILON
    }

}
