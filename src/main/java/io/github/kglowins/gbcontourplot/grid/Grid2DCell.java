package io.github.kglowins.gbcontourplot.grid;

import io.github.kglowins.gbcontourplot.graphics.LineEnds;
import io.github.kglowins.gbcontourplot.graphics.Coordinates2D;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class Grid2DCell {

    private final Function2DValue topLeft;
    private final Function2DValue topRight;
    private final Function2DValue bottomRight;
    private final Function2DValue bottomLeft;

    private final double topY;
    private final double bottomY;
    private final double leftX;
    private final double rightX;

    Grid2DCell(Function2DValue topLeft, Function2DValue topRight,
               Function2DValue bottomRight, Function2DValue bottomLeft) {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomRight = bottomRight;
        this.bottomLeft = bottomLeft;

        topY = topLeft.y();
        bottomY = bottomRight.y();
        leftX = topLeft.x();
        rightX = topRight.x();
    }

    public List<LineEnds> toLineEnds(double isoLevel) {
        int caseIndex = getIsoLineCase(isoLevel);
        List<LineEnds> lineEnds;

        switch (caseIndex) {
            case 0:
            case 15:
                lineEnds = emptyList();
                break;

            case 1:
            case 14:
                lineEnds = singletonList(
                    LineEnds.of(
                        leftX,
                        interpolateCoordOfIsoLevel(isoLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()),
                        interpolateCoordOfIsoLevel(isoLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomY)
                );
                break;

            case 2:
            case 13:
                lineEnds = singletonList(
                    LineEnds.of(
                        rightX,
                        interpolateCoordOfIsoLevel(isoLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f()),
                        interpolateCoordOfIsoLevel(isoLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomY));
                break;

            case 3:
            case 12:
                lineEnds = singletonList(
                    LineEnds.of(leftX,
                        interpolateCoordOfIsoLevel(isoLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()),
                        rightX,
                        interpolateCoordOfIsoLevel(isoLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f()))
                );
                break;

            case 4:
            case 11:
                lineEnds = singletonList(
                    LineEnds.of(interpolateCoordOfIsoLevel(isoLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topY,
                        rightX,
                        interpolateCoordOfIsoLevel(isoLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f()))
                );
                break;

            case 5:
                lineEnds = asList(
                    LineEnds.of(
                        rightX,
                        interpolateCoordOfIsoLevel(isoLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f()),
                        interpolateCoordOfIsoLevel(isoLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomY),
                    LineEnds.of(
                        leftX,
                        interpolateCoordOfIsoLevel(isoLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()),
                        interpolateCoordOfIsoLevel(isoLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topY)
                );
                break;

            case 6:
            case 9:
                lineEnds = singletonList(
                    LineEnds.of(interpolateCoordOfIsoLevel(isoLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topY,
                        interpolateCoordOfIsoLevel(isoLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomY)
                );
                break;

            case 7:
            case 8:
                lineEnds = singletonList(
                    LineEnds.of(
                        leftX,
                        interpolateCoordOfIsoLevel(isoLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()),
                        interpolateCoordOfIsoLevel(isoLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topY)
                );
                break;

            case 10:
                lineEnds = asList(
                    LineEnds.of(
                        leftX,
                        interpolateCoordOfIsoLevel(isoLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()),
                        interpolateCoordOfIsoLevel(isoLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomY),
                    LineEnds.of(interpolateCoordOfIsoLevel(isoLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topY,
                        rightX,
                        interpolateCoordOfIsoLevel(isoLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f()))
                );
                break;

            default: throw new IllegalArgumentException();
        }

        return lineEnds;
    }

    public List<List<Coordinates2D>> toPolygons(double lowerLevel, double upperLevel) {
        int caseIndex = getIsoBandCase(lowerLevel, upperLevel);
        List<List<Coordinates2D>> polygons;

        switch (caseIndex) {
            case 0:
            case 80:
                polygons = emptyList();
                break;

            // single triangles
            case 1: //0001
                polygons = singletonList(triangleCornerBottomLeft(lowerLevel, lowerLevel));
                break;

            case 79: //2221
                polygons = singletonList(triangleCornerBottomLeft(upperLevel, upperLevel));
                break;

            case 3: //0010
                polygons = singletonList(triangleCornerBottomRight(lowerLevel, lowerLevel));
                break;

            case 77: //2212
                polygons = singletonList(triangleCornerBottomRight(upperLevel, upperLevel));
                break;

            case 9: //0100
                polygons = singletonList(triangleCornerTopRight(lowerLevel, lowerLevel));
                break;

            case 71: //2122
                polygons = singletonList(triangleCornerTopRight(upperLevel, upperLevel));
                break;

            case 27: //1000
                polygons = singletonList(triangleCornerTopLeft(lowerLevel, lowerLevel));
                break;

            case 53: //1222
                polygons = singletonList(triangleCornerTopLeft(upperLevel, upperLevel));
                break;

            // single trapezoid
            case 2: //0002
                polygons = singletonList(bandCornerBottomLeft(lowerLevel, lowerLevel, upperLevel, upperLevel));
                break;

            case 78: //2220
                polygons = singletonList(bandCornerBottomLeft(upperLevel, upperLevel, lowerLevel, lowerLevel));
                break;

            case 6: //0020
                polygons = singletonList(bandCornerBottomRight(lowerLevel, upperLevel, upperLevel, lowerLevel));
                break;

            case 74: //2202
                polygons = singletonList(bandCornerBottomRight(upperLevel, lowerLevel, lowerLevel, upperLevel));
                break;

            case 18: //0200
                polygons = singletonList(bandCornerTopRight(lowerLevel, upperLevel, upperLevel, lowerLevel));
                break;

            case 62: //2022
                polygons = singletonList(bandCornerTopRight(upperLevel, lowerLevel, lowerLevel, upperLevel));
                break;

            case 26: //2000
                polygons = singletonList(bandCornerTopLeft(lowerLevel, upperLevel, upperLevel, lowerLevel));
                break;

            case 54: //0222
                polygons = singletonList(bandCornerTopLeft(upperLevel, lowerLevel, lowerLevel, upperLevel));
                break;

            // square
            case 40: //1111
                var polygon = asList(
                    Coordinates2D.of(topLeft.x(), topLeft.y()),
                    Coordinates2D.of(topRight.x(), topRight.y()),
                    Coordinates2D.of(bottomRight.x(), bottomRight.y()),
                    Coordinates2D.of(bottomLeft.x(), bottomLeft.y())
                );
                polygons = singletonList(polygon);
                break;

            // rectangles
            case 36: //1100
                polygons = singletonList(rectangleTop(lowerLevel));
                break;

            case 4: //0011
                polygons = singletonList(rectangleBottom(lowerLevel));
                break;

            case 44: //1122
                polygons = singletonList(rectangleTop(upperLevel));
                break;

            case 76: //2211
                polygons = singletonList(rectangleBottom(upperLevel));
                break;

            case 12: //0110
                polygons = singletonList(rectangleRight(lowerLevel));
                break;

            case 28: //1001
                polygons = singletonList(rectangleLeft(lowerLevel));
                break;

            case 68: //2112
                polygons = singletonList(rectangleRight(upperLevel));
                break;

            case 52: //1221
                polygons = singletonList(rectangleLeft(upperLevel));
                break;

            case 72: //2200
                polygon = asList(
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f())),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(upperLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
                );
                polygons = singletonList(polygon);
                break;

            case 8: //0022
                polygon = asList(
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f())),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(upperLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
                );
                polygons = singletonList(polygon);
                break;

            case 56: //2002
                polygon = asList(
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topLeft.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topLeft.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y())
                );
                polygons = singletonList(polygon);
                break;

            case 24: //0220
                polygon = asList(
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topLeft.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topLeft.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y())
                );
                polygons = singletonList(polygon);
                break;

            case 49: //1211
                polygon = asList(
                    Coordinates2D.of(topLeft.x(), topLeft.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topLeft.y()),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(upperLevel, bottomRight.y(), bottomRight.f(), topRight.y(), topRight.f())),
                    Coordinates2D.of(bottomRight.x(), bottomRight.y()),
                    Coordinates2D.of(bottomLeft.x(), bottomLeft.y())
                );
                polygons = singletonList(polygon);
                break;

            case 31: //1011
                polygon = asList(
                    Coordinates2D.of(topLeft.x(), topLeft.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topLeft.y()),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, bottomRight.y(), bottomRight.f(), topRight.y(), topRight.f())),
                    Coordinates2D.of(bottomRight.x(), bottomRight.y()),
                    Coordinates2D.of(bottomLeft.x(), bottomLeft.y())
                );
                polygons = singletonList(polygon);
                break;

            case 67: //2111
                polygon = asList(
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topLeft.y()),
                    Coordinates2D.of(topRight.x(), topRight.y()),
                    Coordinates2D.of(bottomRight.x(), bottomRight.y()),
                    Coordinates2D.of(bottomLeft.x(), bottomLeft.y()),
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(upperLevel, bottomLeft.y(), bottomLeft.f(), topLeft.y(), topLeft.f()))
                );
                polygons = singletonList(polygon);
                break;

            case 13: //0111
                polygon = asList(
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topLeft.y()),
                    Coordinates2D.of(topRight.x(), topRight.y()),
                    Coordinates2D.of(bottomRight.x(), bottomRight.y()),
                    Coordinates2D.of(bottomLeft.x(), bottomLeft.y()),
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.y(), bottomLeft.f(), topLeft.y(), topLeft.f()))
                );
                polygons = singletonList(polygon);
                break;

            case 41: //1112
                polygon = asList(
                    Coordinates2D.of(topLeft.x(), topLeft.y()),
                    Coordinates2D.of(topRight.x(), topRight.y()),
                    Coordinates2D.of(bottomRight.x(), bottomRight.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y()),
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(upperLevel, bottomLeft.y(), bottomLeft.f(), topLeft.y(), topLeft.f()))
                );
                polygons = singletonList(polygon);
                break;

            case 39: //1110
                polygon = asList(
                    Coordinates2D.of(topLeft.x(), topLeft.y()),
                    Coordinates2D.of(topRight.x(), topRight.y()),
                    Coordinates2D.of(bottomRight.x(), bottomRight.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y()),
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.y(), bottomLeft.f(), topLeft.y(), topLeft.f()))
                );
                polygons = singletonList(polygon);
                break;

            case 43: //1121
                polygon = asList(
                    Coordinates2D.of(topLeft.x(), topLeft.y()),
                    Coordinates2D.of(topRight.x(), topRight.y()),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(upperLevel, bottomRight.y(), bottomRight.f(), topRight.y(), topRight.f())),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y()),
                    Coordinates2D.of(bottomLeft.x(), bottomLeft.y())
                );
                polygons = singletonList(polygon);
                break;

            case 37: //1101
                polygon = asList(
                    Coordinates2D.of(topLeft.x(), topLeft.y()),
                    Coordinates2D.of(topRight.x(), topRight.y()),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, bottomRight.y(), bottomRight.f(), topRight.y(), topRight.f())),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y()),
                    Coordinates2D.of(bottomLeft.x(), bottomLeft.y())
                );
                polygons = singletonList(polygon);
                break;

            case 45: //1200
                polygon = asList(
                    Coordinates2D.of(topLeft.x(), topLeft.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topLeft.y()),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(upperLevel, bottomRight.y(), bottomRight.f(), topRight.y(), topRight.f())),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, bottomRight.y(), bottomRight.f(), topRight.y(), topRight.f())),
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.y(), bottomLeft.f(), topLeft.y(), topLeft.f()))
                );
                polygons = singletonList(polygon);
                break;

            case 35: //1022
                polygon = asList(
                    Coordinates2D.of(topLeft.x(), topLeft.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topLeft.y()),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, bottomRight.y(), bottomRight.f(), topRight.y(), topRight.f())),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(upperLevel, bottomRight.y(), bottomRight.f(), topRight.y(), topRight.f())),
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(upperLevel, bottomLeft.y(), bottomLeft.f(), topLeft.y(), topLeft.f()))
                );
                polygons = singletonList(polygon);
                break;

            case 15: //0120
                polygon = asList(
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topLeft.y()),
                    Coordinates2D.of(topRight.x(), topRight.y()),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(upperLevel, bottomRight.y(), bottomRight.f(), topRight.y(), topRight.f())),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomLeft.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomLeft.y())
                );
                polygons = singletonList(polygon);
                break;

            case 65: //2102
                polygon = asList(
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topLeft.y()),
                    Coordinates2D.of(topRight.x(), topRight.y()),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, bottomRight.y(), bottomRight.f(), topRight.y(), topRight.f())),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomLeft.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomLeft.y())
                );
                polygons = singletonList(polygon);
                break;

            case 5: //0012
                polygon = asList(
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f())),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, bottomRight.y(), bottomRight.f(), topRight.y(), topRight.f())),
                    Coordinates2D.of(bottomRight.x(), bottomRight.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y()),
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
                );
                polygons = singletonList(polygon);
                break;

            case 75: //2210
                polygon = asList(
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f())),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(upperLevel, bottomRight.y(), bottomRight.f(), topRight.y(), topRight.f())),
                    Coordinates2D.of(bottomRight.x(), bottomRight.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y()),
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
                );
                polygons = singletonList(polygon);
                break;

            case 55: //2001
                polygon = asList(
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topLeft.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topLeft.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y()),
                    Coordinates2D.of(bottomLeft.x(), bottomLeft.y()),
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
                );
                polygons = singletonList(polygon);
                break;

            case 25: //0221
                polygon = asList(
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topLeft.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topLeft.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y()),
                    Coordinates2D.of(bottomLeft.x(), bottomLeft.y()),
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
                );
                polygons = singletonList(polygon);
                break;

            case 29: //1002
                polygon = asList(
                    Coordinates2D.of(topLeft.x(), topLeft.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topLeft.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y()),
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
                );
                polygons = singletonList(polygon);
                break;

            case 51: //1220
                polygon = asList(
                    Coordinates2D.of(topLeft.x(), topLeft.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topLeft.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y()),
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
                );
                polygons = singletonList(polygon);
                break;

            case 63: //2100
                polygon = asList(
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topLeft.y()),
                    Coordinates2D.of(topRight.x(), topRight.y()),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f())),
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
                );
                polygons = singletonList(polygon);
                break;

            case 17: //0122
                polygon = asList(
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topLeft.y()),
                    Coordinates2D.of(topRight.x(), topRight.y()),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(upperLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f())),
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
                );
                polygons = singletonList(polygon);
                break;

            case 21: //0210
                polygon = asList(
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topLeft.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topLeft.y()),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(upperLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                    Coordinates2D.of(bottomRight.x(), bottomRight.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y())
                );
                polygons = singletonList(polygon);
                break;

            case 59: //2012
                polygon = asList(
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topLeft.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topLeft.y()),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                    Coordinates2D.of(bottomRight.x(), bottomRight.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y())
                );
                polygons = singletonList(polygon);
                break;

            case 7: //0021
                polygon = asList(
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f())),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(upperLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y()),
                    Coordinates2D.of(bottomLeft.x(), bottomLeft.y())
                );
                polygons = singletonList(polygon);
                break;

            case 73: //2201
                polygon = asList(
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f())),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(upperLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y()),
                    Coordinates2D.of(bottomLeft.x(), bottomLeft.y())
                );
                polygons = singletonList(polygon);
                break;

            case 22: //0211
                polygon = asList(
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f())),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topRight.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topRight.y()),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(upperLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                    Coordinates2D.of(bottomRight.x(), bottomRight.y()),
                    Coordinates2D.of(bottomLeft.x(), bottomLeft.y())
                );
                polygons = singletonList(polygon);
                break;

            case 58: //2011
                polygon = asList(
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f())),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topRight.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topRight.y()),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                    Coordinates2D.of(bottomRight.x(), bottomRight.y()),
                    Coordinates2D.of(bottomLeft.x(), bottomLeft.y())
                );
                polygons = singletonList(polygon);
                break;

            case 66: //2110
                polygon = asList(
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f())),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topRight.y()),
                    Coordinates2D.of(topRight.x(), topRight.y()),
                    Coordinates2D.of(bottomRight.x(), bottomRight.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y()),
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
                );
                polygons = singletonList(polygon);
                break;

            case 14: //0112
                polygon = asList(
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f())),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topRight.y()),
                    Coordinates2D.of(topRight.x(), topRight.y()),
                    Coordinates2D.of(bottomRight.x(), bottomRight.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y()),
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
                );
                polygons = singletonList(polygon);
                break;

            case 38: //1102
                polygon = asList(
                    Coordinates2D.of(topLeft.x(), topLeft.y()),
                    Coordinates2D.of(topRight.x(), topRight.y()),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y()),
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
                );
                polygons = singletonList(polygon);
                break;

            case 42: //1120
                polygon = asList(
                    Coordinates2D.of(topLeft.x(), topLeft.y()),
                    Coordinates2D.of(topRight.x(), topRight.y()),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(upperLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y()),
                    Coordinates2D.of(
                        topLeft.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
                );
                polygons = singletonList(polygon);
                break;

            case 34: //1021
                polygon = asList(
                    Coordinates2D.of(topLeft.x(), topLeft.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topLeft.y()),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(upperLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y()),
                    Coordinates2D.of(bottomLeft.x(), bottomLeft.y())
                );
                polygons = singletonList(polygon);
                break;

            case 46: //1201
                polygon = asList(
                    Coordinates2D.of(topLeft.x(), topLeft.y()),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(upperLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                        topLeft.y()),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(upperLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                    Coordinates2D.of(
                        topRight.x(),
                        interpolateCoordOfIsoLevel(lowerLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                    Coordinates2D.of(
                        interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                        bottomRight.y()),
                    Coordinates2D.of(bottomLeft.x(), bottomLeft.y())
                );
                polygons = singletonList(polygon);
                break;

            case 64: //2101
                polygons = singletonList(hexagonDiagonalBottomLeftToTopRight(upperLevel, upperLevel, lowerLevel, lowerLevel));
                break;

            case 16: //0121
                polygons = singletonList(hexagonDiagonalBottomLeftToTopRight(lowerLevel, lowerLevel, upperLevel, upperLevel));
                break;

            case 32: //1012
                polygons = singletonList(hexagonDiagonalTopLeftToBottomRight(lowerLevel, lowerLevel, upperLevel, upperLevel));
                break;

            case 48: //1210
                polygons = singletonList(hexagonDiagonalTopLeftToBottomRight(upperLevel, upperLevel, lowerLevel, lowerLevel));
                break;

            //saddles
            // 8-sided
            case 60: //2020
                var centerBit = getIsoBandBit(estimatedCenterValue(), lowerLevel, upperLevel);
                List<Coordinates2D> polygon2;
                if (centerBit == 0) {
                    polygon = bandCornerTopLeft(upperLevel, lowerLevel, lowerLevel, upperLevel);
                    polygon2 = bandCornerBottomRight(lowerLevel, upperLevel, upperLevel, lowerLevel);
                    polygons = asList(polygon, polygon2);

                } else if (centerBit == 1) {
                    polygon = asList(
                        Coordinates2D.of(
                            topLeft.x(),
                            interpolateCoordOfIsoLevel(upperLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f())),
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(upperLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                            topLeft.y()),
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(lowerLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                            topLeft.y()),
                        Coordinates2D.of(
                            topRight.x(),
                            interpolateCoordOfIsoLevel(lowerLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                        Coordinates2D.of(
                            topRight.x(),
                            interpolateCoordOfIsoLevel(upperLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(upperLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                            bottomRight.y()),
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                            bottomRight.y()),
                        Coordinates2D.of(
                            topLeft.x(),
                            interpolateCoordOfIsoLevel(lowerLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
                    );
                    polygons = singletonList(polygon);

                } else {
                    polygon = bandCornerBottomLeft(upperLevel, upperLevel ,lowerLevel, lowerLevel);
                    polygon2 = bandCornerTopRight(upperLevel, lowerLevel, lowerLevel, upperLevel);
                    polygons = asList(polygon, polygon2);
                }
                break;

            case 20: //0202
                centerBit = getIsoBandBit(estimatedCenterValue(), lowerLevel, upperLevel);
                if (centerBit == 2) {
                    polygon = bandCornerTopLeft(lowerLevel, upperLevel, upperLevel, lowerLevel);
                    polygon2 = bandCornerBottomRight(upperLevel, lowerLevel, lowerLevel, upperLevel);
                    polygons = asList(polygon, polygon2);

                } else if (centerBit == 1) {
                    polygon = asList(
                        Coordinates2D.of(
                            topLeft.x(),
                            interpolateCoordOfIsoLevel(lowerLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f())),
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(lowerLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                            topLeft.y()),
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(upperLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                            topLeft.y()),
                        Coordinates2D.of(
                            topRight.x(),
                            interpolateCoordOfIsoLevel(upperLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                        Coordinates2D.of(
                            topRight.x(),
                            interpolateCoordOfIsoLevel(lowerLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                            bottomRight.y()),
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(upperLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                            bottomRight.y()),
                        Coordinates2D.of(
                            topLeft.x(),
                            interpolateCoordOfIsoLevel(upperLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
                    );
                    polygons = singletonList(polygon);

                } else {
                    polygon = bandCornerBottomLeft(lowerLevel, lowerLevel ,upperLevel, upperLevel);
                    polygon2 = bandCornerTopRight(lowerLevel, upperLevel, upperLevel, lowerLevel);
                    polygons = asList(polygon, polygon2);
                }
                break;

            //6-sided
            case 10: //0101
                centerBit = getIsoBandBit(estimatedCenterValue(), lowerLevel, upperLevel);
                if (centerBit == 1) {
                    polygons = singletonList(hexagonDiagonalBottomLeftToTopRight(lowerLevel, lowerLevel, lowerLevel, lowerLevel));
                } else {
                    polygons = asList(
                        triangleCornerBottomLeft(lowerLevel, lowerLevel),
                        triangleCornerTopRight(lowerLevel, lowerLevel));
                }
                break;

            case 70: //2121
                centerBit = getIsoBandBit(estimatedCenterValue(), lowerLevel, upperLevel);
                if (centerBit == 1) {
                    polygons = singletonList(hexagonDiagonalBottomLeftToTopRight(upperLevel, upperLevel, upperLevel, upperLevel));
                } else {
                    polygons = asList(
                        triangleCornerBottomLeft(upperLevel, upperLevel),
                        triangleCornerTopRight(upperLevel, upperLevel));
                }
                break;

            case 30: //1010
                centerBit = getIsoBandBit(estimatedCenterValue(), lowerLevel, upperLevel);
                if (centerBit == 1) {
                    polygons = singletonList(hexagonDiagonalTopLeftToBottomRight(lowerLevel, lowerLevel, lowerLevel, lowerLevel));
                } else {
                    polygons = asList(
                        triangleCornerTopLeft(lowerLevel, lowerLevel),
                        triangleCornerBottomRight(lowerLevel, lowerLevel));
                }
                break;

            case 50: //1212
                centerBit = getIsoBandBit(estimatedCenterValue(), lowerLevel, upperLevel);
                if (centerBit == 1) {
                    polygons = singletonList(hexagonDiagonalTopLeftToBottomRight(upperLevel, upperLevel, upperLevel, upperLevel));
                } else {
                    polygons = asList(
                        triangleCornerTopLeft(upperLevel, upperLevel),
                        triangleCornerBottomRight(upperLevel, upperLevel));
                }
                break;

            //7-sided

            case 69: //2120
                centerBit = getIsoBandBit(estimatedCenterValue(), lowerLevel, upperLevel);
                if (centerBit == 1) {
                    polygon = asList(
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(upperLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                            topLeft.y()),
                        Coordinates2D.of(topRight.x(), topRight.y()),
                        Coordinates2D.of(
                            topRight.x(),
                            interpolateCoordOfIsoLevel(upperLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(upperLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                            bottomRight.y()),
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                            bottomRight.y()),
                        Coordinates2D.of(
                            topLeft.x(),
                            interpolateCoordOfIsoLevel(lowerLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f())),
                        Coordinates2D.of(
                            topLeft.x(),
                            interpolateCoordOfIsoLevel(upperLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
                    );
                    polygons = singletonList(polygon);
                } else {
                    polygon = triangleCornerTopRight(upperLevel, upperLevel);
                    polygon2 = bandCornerBottomLeft(upperLevel, upperLevel, lowerLevel, lowerLevel);
                    polygons = asList(polygon, polygon2);
                }
                break;

            case 11: //0102
                centerBit = getIsoBandBit(estimatedCenterValue(), lowerLevel, upperLevel);
                if (centerBit == 1) {
                    polygon = asList(
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(lowerLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                            topLeft.y()),
                        Coordinates2D.of(topRight.x(), topRight.y()),
                        Coordinates2D.of(
                            topRight.x(),
                            interpolateCoordOfIsoLevel(lowerLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                            bottomRight.y()),
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(upperLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                            bottomRight.y()),
                        Coordinates2D.of(
                            topLeft.x(),
                            interpolateCoordOfIsoLevel(upperLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f())),
                        Coordinates2D.of(
                            topLeft.x(),
                            interpolateCoordOfIsoLevel(lowerLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
                    );
                    polygons = singletonList(polygon);
                } else {
                    polygon = triangleCornerTopRight(lowerLevel, lowerLevel);
                    polygon2 = bandCornerBottomLeft(lowerLevel, lowerLevel, upperLevel, upperLevel);
                    polygons = asList(polygon, polygon2);
                }
                break;

            case 61: //2021
                centerBit = getIsoBandBit(estimatedCenterValue(), lowerLevel, upperLevel);
                if (centerBit == 1) {
                    polygon = asList(
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(upperLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                            topLeft.y()),
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(lowerLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                            topLeft.y()),
                        Coordinates2D.of(
                            topRight.x(),
                            interpolateCoordOfIsoLevel(lowerLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                        Coordinates2D.of(
                            topRight.x(),
                            interpolateCoordOfIsoLevel(upperLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(upperLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                            bottomRight.y()),
                        Coordinates2D.of(bottomLeft.x(), bottomLeft.y()),
                        Coordinates2D.of(
                            topLeft.x(),
                            interpolateCoordOfIsoLevel(upperLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
                    );
                    polygons = singletonList(polygon);
                } else {
                    polygon = triangleCornerBottomLeft(upperLevel, upperLevel);
                    polygon2 = bandCornerTopRight(upperLevel, lowerLevel, lowerLevel, upperLevel);
                    polygons = asList(polygon, polygon2);
                }
                break;

            case 19: //0201
                centerBit = getIsoBandBit(estimatedCenterValue(), lowerLevel, upperLevel);
                if (centerBit == 1) {
                    polygon = asList(
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(lowerLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                            topLeft.y()),
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(upperLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                            topLeft.y()),
                        Coordinates2D.of(
                            topRight.x(),
                            interpolateCoordOfIsoLevel(upperLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                        Coordinates2D.of(
                            topRight.x(),
                            interpolateCoordOfIsoLevel(lowerLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                            bottomRight.y()),
                        Coordinates2D.of(bottomLeft.x(), bottomLeft.y()),
                        Coordinates2D.of(
                            topLeft.x(),
                            interpolateCoordOfIsoLevel(lowerLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
                    );
                    polygons = singletonList(polygon);
                } else {
                    polygon = triangleCornerBottomLeft(lowerLevel, lowerLevel);
                    polygon2 = bandCornerTopRight(lowerLevel, upperLevel, upperLevel, lowerLevel);
                    polygons = asList(polygon, polygon2);
                }
                break;

            case 47: //1202
                centerBit = getIsoBandBit(estimatedCenterValue(), lowerLevel, upperLevel);
                if (centerBit == 1) {
                    polygon = asList(
                        Coordinates2D.of(topLeft.x(), topLeft.y()),
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(upperLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                            topLeft.y()),
                        Coordinates2D.of(
                            topRight.x(),
                            interpolateCoordOfIsoLevel(upperLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                        Coordinates2D.of(
                            topRight.x(),
                            interpolateCoordOfIsoLevel(lowerLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                            bottomRight.y()),
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(upperLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                            bottomRight.y()),
                        Coordinates2D.of(
                            topLeft.x(),
                            interpolateCoordOfIsoLevel(upperLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
                    );
                    polygons = singletonList(polygon);
                } else {
                    polygon = triangleCornerTopLeft(upperLevel, upperLevel);
                    polygon2 = bandCornerBottomRight(upperLevel, lowerLevel, lowerLevel, upperLevel);
                    polygons = asList(polygon, polygon2);
                }
                break;

            case 33: //1020
                centerBit = getIsoBandBit(estimatedCenterValue(), lowerLevel, upperLevel);
                if (centerBit == 1) {
                    polygon = asList(
                        Coordinates2D.of(topLeft.x(), topLeft.y()),
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(lowerLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                            topLeft.y()),
                        Coordinates2D.of(
                            topRight.x(),
                            interpolateCoordOfIsoLevel(lowerLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                        Coordinates2D.of(
                            topRight.x(),
                            interpolateCoordOfIsoLevel(upperLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(upperLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                            bottomRight.y()),
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                            bottomRight.y()),
                        Coordinates2D.of(
                            topLeft.x(),
                            interpolateCoordOfIsoLevel(lowerLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
                    );
                    polygons = singletonList(polygon);
                } else {
                    polygon = triangleCornerTopLeft(lowerLevel, lowerLevel);
                    polygon2 = bandCornerBottomRight(lowerLevel, upperLevel, upperLevel, lowerLevel);
                    polygons = asList(polygon, polygon2);
                }
                break;

            case 23: //0212
                centerBit = getIsoBandBit(estimatedCenterValue(), lowerLevel, upperLevel);
                if (centerBit == 1) {
                    polygon = asList(
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(lowerLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                            topLeft.y()),
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(upperLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                            topLeft.y()),
                        Coordinates2D.of(
                            topRight.x(),
                            interpolateCoordOfIsoLevel(upperLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                        Coordinates2D.of(bottomRight.x(), bottomRight.y()),
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(upperLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                            bottomRight.y()),
                        Coordinates2D.of(
                            topLeft.x(),
                            interpolateCoordOfIsoLevel(upperLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f())),
                        Coordinates2D.of(
                            topLeft.x(),
                            interpolateCoordOfIsoLevel(lowerLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
                    );
                    polygons = singletonList(polygon);
                } else {
                    polygon = triangleCornerBottomRight(upperLevel, upperLevel);
                    polygon2 = bandCornerTopLeft(lowerLevel, upperLevel, upperLevel, lowerLevel);
                    polygons = asList(polygon, polygon2);
                }
                break;

            case 57: //2010
                centerBit = getIsoBandBit(estimatedCenterValue(), lowerLevel, upperLevel);
                if (centerBit == 1) {
                    polygon = asList(
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(upperLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                            topLeft.y()),
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(lowerLevel, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                            topLeft.y()),
                        Coordinates2D.of(
                            topRight.x(),
                            interpolateCoordOfIsoLevel(lowerLevel, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
                        Coordinates2D.of(bottomRight.x(), bottomRight.y()),
                        Coordinates2D.of(
                            interpolateCoordOfIsoLevel(lowerLevel, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                            bottomRight.y()),
                        Coordinates2D.of(
                            topLeft.x(),
                            interpolateCoordOfIsoLevel(lowerLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f())),
                        Coordinates2D.of(
                            topLeft.x(),
                            interpolateCoordOfIsoLevel(upperLevel, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
                    );
                    polygons = singletonList(polygon);
                } else {
                    polygon = triangleCornerBottomRight(lowerLevel, lowerLevel);
                    polygon2 = bandCornerTopLeft(upperLevel, lowerLevel, lowerLevel, upperLevel);
                    polygons = asList(polygon, polygon2);
                }
                break;

            default:
                throw new IllegalArgumentException();
        }
        return polygons;
    }

    private double estimatedCenterValue() {
        return 0.25 * (topLeft.f() + topRight.f() + bottomRight.f() + bottomLeft.f());
    }

    private double interpolateCoordOfIsoLevel(double isoLevel,
                                      double coord1, double f1,
                                      double coord2, double f2) {
        double a = (f1 - f2) / (coord1 - coord2);
        double b = f1 - a * coord1;
        return (isoLevel - b) / a;
    }

    private int getIsoLineCase(double isoLevel) {
        int topLeftBit = getIsoLineBit(topLeft.f(), isoLevel);
        int topRightBit = getIsoLineBit(topRight.f(), isoLevel);
        int bottomRightBit = getIsoLineBit(bottomRight.f(), isoLevel);
        int bottomLeftBit = getIsoLineBit(bottomLeft.f(), isoLevel);
        int caseIndex = 8 * topLeftBit + 4 * topRightBit + 2 * bottomRightBit + bottomLeftBit;

        if ((caseIndex == 5 || caseIndex == 10) && estimatedCenterValue() < isoLevel) {
            caseIndex = 5 + caseIndex % 10;
        }

        return caseIndex;
    }

    private int getIsoLineBit(double value, double isoLevel) {
        return value > isoLevel ? 1 : 0;
    }

    private int getIsoBandCase(double lowerLevel, double upperLevel) {
        int topLeftBit = getIsoBandBit(topLeft.f(), lowerLevel, upperLevel);
        int topRightBit = getIsoBandBit(topRight.f(), lowerLevel, upperLevel);
        int bottomRightBit = getIsoBandBit(bottomRight.f(), lowerLevel, upperLevel);
        int bottomLeftBit = getIsoBandBit(bottomLeft.f(), lowerLevel, upperLevel);
        return 27 * topLeftBit + 9 * topRightBit + 3 * bottomRightBit + bottomLeftBit;
    }

    private int getIsoBandBit(double value, double lowerLevel, double upperLevel) {
        if (value > upperLevel) {
            return 2;
        } else if (value < lowerLevel) {
            return 0;
        } else {
            return 1;
        }
    }

    private List<Coordinates2D> hexagonDiagonalBottomLeftToTopRight(double level1, double level2, double level3, double level4) {
        return asList(
            Coordinates2D.of(
                topLeft.x(),
                interpolateCoordOfIsoLevel(level1, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f())),
            Coordinates2D.of(
                interpolateCoordOfIsoLevel(level2, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                topLeft.y()),
            Coordinates2D.of(topRight.x(), topRight.y()),
            Coordinates2D.of(
                topRight.x(),
                interpolateCoordOfIsoLevel(level3, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
            Coordinates2D.of(
                interpolateCoordOfIsoLevel(level4, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                bottomRight.y()),
            Coordinates2D.of(bottomLeft.x(), bottomLeft.y())
        );
    }

    private List<Coordinates2D> hexagonDiagonalTopLeftToBottomRight(double level1, double level2, double level3, double level4) {
        return asList(
            Coordinates2D.of(topLeft.x(), topLeft.y()),
            Coordinates2D.of(
                interpolateCoordOfIsoLevel(level1, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                topLeft.y()),
            Coordinates2D.of(
                topRight.x(),
                interpolateCoordOfIsoLevel(level2, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
            Coordinates2D.of(bottomRight.x(), bottomRight.y()),
            Coordinates2D.of(
                interpolateCoordOfIsoLevel(level3, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                bottomRight.y()),
            Coordinates2D.of(
                topLeft.x(),
                interpolateCoordOfIsoLevel(level4, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
        );
    }

    private List<Coordinates2D> triangleCornerBottomLeft(double level1, double level2) {
        return asList(
            Coordinates2D.of(
                topLeft.x(),
                interpolateCoordOfIsoLevel(level1, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f())),
            Coordinates2D.of(
                interpolateCoordOfIsoLevel(level2, bottomRight.x(), bottomRight.f(), bottomLeft.x(), bottomLeft.f()),
                bottomRight.y()),
            Coordinates2D.of(bottomLeft.x(), bottomLeft.y())
        );
    }

    private List<Coordinates2D> triangleCornerBottomRight(double level1, double level2) {
        return asList(
            Coordinates2D.of(
                topRight.x(),
                interpolateCoordOfIsoLevel(level1, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
            Coordinates2D.of(bottomRight.x(), bottomRight.y()),
            Coordinates2D.of(
                interpolateCoordOfIsoLevel(level2, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                bottomRight.y())
        );
    }

    private List<Coordinates2D> triangleCornerTopRight(double level1, double level2) {
        return asList(
            Coordinates2D.of(
                interpolateCoordOfIsoLevel(level1, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                topLeft.y()),
            Coordinates2D.of(topRight.x(), topRight.y()),
            Coordinates2D.of(
                topRight.x(),
                interpolateCoordOfIsoLevel(level2, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f()))
        );
    }

    private List<Coordinates2D> triangleCornerTopLeft(double level1, double level2) {
        return asList(
            Coordinates2D.of(topLeft.x(), topLeft.y()),
            Coordinates2D.of(
                interpolateCoordOfIsoLevel(level1, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                topRight.y()),
            Coordinates2D.of(
                topLeft.x(),
                interpolateCoordOfIsoLevel(level2, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
        );
    }

    private List<Coordinates2D> bandCornerBottomLeft(double level1, double level2, double level3, double level4) {
        return asList(
            Coordinates2D.of(
                topLeft.x(),
                interpolateCoordOfIsoLevel(level1, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f())),
            Coordinates2D.of(
                interpolateCoordOfIsoLevel(level2, bottomRight.x(), bottomRight.f(), bottomLeft.x(), bottomLeft.f()),
                bottomRight.y()),
            Coordinates2D.of(
                interpolateCoordOfIsoLevel(level3, bottomRight.x(), bottomRight.f(), bottomLeft.x(), bottomLeft.f()),
                bottomRight.y()),
            Coordinates2D.of(
                topLeft.x(),
                interpolateCoordOfIsoLevel(level4, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
        );
    }

    private List<Coordinates2D> bandCornerBottomRight(double level1, double level2, double level3, double level4) {
        return asList(
            Coordinates2D.of(
                topRight.x(),
                interpolateCoordOfIsoLevel(level1, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
            Coordinates2D.of(
                topRight.x(),
                interpolateCoordOfIsoLevel(level2, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
            Coordinates2D.of(
                interpolateCoordOfIsoLevel(level3, bottomRight.x(), bottomRight.f(), bottomLeft.x(), bottomLeft.f()),
                bottomRight.y()),
            Coordinates2D.of(
                interpolateCoordOfIsoLevel(level4, bottomRight.x(), bottomRight.f(), bottomLeft.x(), bottomLeft.f()),
                bottomRight.y())
        );
    }

    private List<Coordinates2D> bandCornerTopRight(double level1, double level2, double level3, double level4) {
        return asList(
            Coordinates2D.of(
                interpolateCoordOfIsoLevel(level1, topRight.x(), topRight.f(), topLeft.x(), topLeft.f()),
                topLeft.y()),
            Coordinates2D.of(
                interpolateCoordOfIsoLevel(level2, topRight.x(), topRight.f(), topLeft.x(), topLeft.f()),
                topLeft.y()),
            Coordinates2D.of(
                topRight.x(),
                interpolateCoordOfIsoLevel(level3, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
            Coordinates2D.of(
                topRight.x(),
                interpolateCoordOfIsoLevel(level4, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f()))
        );
    }

    private List<Coordinates2D> bandCornerTopLeft(double level1, double level2, double level3, double level4) {
        return asList(
            Coordinates2D.of(
                interpolateCoordOfIsoLevel(level1, topRight.x(), topRight.f(), topLeft.x(), topLeft.f()),
                topLeft.y()),
            Coordinates2D.of(
                interpolateCoordOfIsoLevel(level2, topRight.x(), topRight.f(), topLeft.x(), topLeft.f()),
                topLeft.y()),
            Coordinates2D.of(
                topLeft.x(),
                interpolateCoordOfIsoLevel(level3, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f())),
            Coordinates2D.of(
                topLeft.x(),
                interpolateCoordOfIsoLevel(level4, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
        );
    }

    private List<Coordinates2D> rectangleBottom(double level) {
        return asList(
            Coordinates2D.of(
                topLeft.x(),
                interpolateCoordOfIsoLevel(level, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f())),
            Coordinates2D.of(
                topRight.x(),
                interpolateCoordOfIsoLevel(level, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
            Coordinates2D.of(bottomRight.x(), bottomRight.y()),
            Coordinates2D.of(bottomLeft.x(), bottomLeft.y())
        );
    }

    private List<Coordinates2D> rectangleTop(double level) {
        return asList(
            Coordinates2D.of(topLeft.x(), topLeft.y()),
            Coordinates2D.of(topRight.x(), topRight.y()),
            Coordinates2D.of(
                topRight.x(),
                interpolateCoordOfIsoLevel(level, topRight.y(), topRight.f(), bottomRight.y(), bottomRight.f())),
            Coordinates2D.of(
                topLeft.x(),
                interpolateCoordOfIsoLevel(level, topLeft.y(), topLeft.f(), bottomLeft.y(), bottomLeft.f()))
        );
    }

    private List<Coordinates2D> rectangleRight(double level) {
        return asList(
            Coordinates2D.of(
                interpolateCoordOfIsoLevel(level, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                topLeft.y()),
            Coordinates2D.of(topRight.x(), topRight.y()),
            Coordinates2D.of(bottomRight.x(), bottomRight.y()),
            Coordinates2D.of(
                interpolateCoordOfIsoLevel(level, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                bottomRight.y())
        );
    }

    private List<Coordinates2D> rectangleLeft(double level) {
        return asList(
            Coordinates2D.of(topLeft.x(), topLeft.y()),
            Coordinates2D.of(
                interpolateCoordOfIsoLevel(level, topLeft.x(), topLeft.f(), topRight.x(), topRight.f()),
                topLeft.y()),
            Coordinates2D.of(
                interpolateCoordOfIsoLevel(level, bottomLeft.x(), bottomLeft.f(), bottomRight.x(), bottomRight.f()),
                bottomRight.y()),
            Coordinates2D.of(bottomLeft.x(), bottomLeft.y())
        );
    }
}
