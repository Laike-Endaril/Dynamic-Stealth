package com.fantasticsource.tools;

@SuppressWarnings("unused")
public class Collision
{
    public static char
            ON = 0,
            LEFT = 1,
            RIGHT = 2;

    public static boolean within(double n, double limit1, double limit2)
    {
        return (limit1 <= n && n <= limit2) || (limit2 <= n && n <= limit1);
    }

    public static boolean pointPoint(double x1, double y1, double x2, double y2)
    {
        return (x1 == x2 && y1 == y2);
    }

    public static boolean pointRectangle(double x, double y, double rx1, double ry1, double rx2, double ry2)
    {
        return within(x, rx1, rx2) && within(y, ry1, ry2);
    }

    public static boolean rectangleRectangle(double r1x1, double r1y1, double r1x2, double r1y2, double r2x1, double r2y1, double r2x2, double r2y2)
    {
        return pointRectangle(r1x1, r1y1, r2x1, r2y1, r2x2, r2y2)
                || pointRectangle(r1x1, r1y2, r2x1, r2y1, r2x2, r2y2)
                || pointRectangle(r1x2, r1y1, r2x1, r2y1, r2x2, r2y2)
                || pointRectangle(r1x2, r1y2, r2x1, r2y1, r2x2, r2y2)
                || pointRectangle(r2x1, r2y1, r1x1, r1y1, r1x2, r1y2)
                || pointRectangle(r2x1, r2y2, r1x1, r1y1, r1x2, r1y2)
                || pointRectangle(r2x2, r2y1, r1x1, r1y1, r1x2, r1y2)
                || pointRectangle(r2x2, r2y2, r1x1, r1y1, r1x2, r1y2);
    }
    public static double[] rectangleRectangleExt(double r1x1, double r1y1, double r1x2, double r1y2, double r2x1, double r2y1, double r2x2, double r2y2)
    {
        double swap;
        if (r1x1 > r1x2)
        {
            swap = r1x1;
            r1x1 = r1x2;
            r1x2 = swap;
        }
        if (r1y1 > r1y2)
        {
            swap = r1y1;
            r1y1 = r1y2;
            r1y2 = swap;
        }
        if (r2x1 > r2x2)
        {
            swap = r2x1;
            r2x1 = r2x2;
            r2x2 = swap;
        }
        if (r2y1 > r2y2)
        {
            swap = r2y1;
            r2y1 = r2y2;
            r2y2 = swap;
        }

        if (within(r1x1, r2x1, r2x2))
        {
            if (within(r1x2, r2x1, r2x2))
            {
                if (within(r1y1, r2y1, r2y2))
                {
                    if (within(r1y2, r2y1, r2y2))
                    {
                        return new double[] {r1x1, r1y1, r1x2, r1y2};
                    }
                    else
                    {
                        return new double[] {r1x1, r1y1, r1x2, r2y1};
                    }
                }
                else
                {
                    if (within(r1y2, r2y1, r2y2))
                    {
                        return new double[] {r1x1, r2y2, r1x2, r1y2};
                    }
                    else if (within(r2y1, r1y1, r1y2))
                    {
                        return new double[] {r1x1, r2y1, r1x2, r2y2};
                    }
                }
            }
            else
            {
                if (within(r1y1, r2y1, r2y2))
                {
                    if (within(r1y2, r2y1, r2y2))
                    {
                        return new double[] {r1x1, r1y1, r2x2, r1y2};
                    }
                    else
                    {
                        return new double[] {r1x1, r1y1, r2x2, r2y2};
                    }
                }
                else
                {
                    if (within(r1y2, r2y1, r2y2))
                    {
                        return new double[] {r1x1, r2y1, r2x2, r1y2};
                    }
                    else
                    {
                        return new double[] {r1x1, r2y1, r2x2, r2y2};
                    }
                }
            }
        }
        else
        {
            if (within(r1x2, r2x1, r2x2))
            {
                if (within(r1y1, r2y1, r2y2))
                {
                    if (within(r1y2, r2y1, r2y2))
                    {
                        return new double[] {r2x1, r1y1, r1x2, r1y2};
                    }
                    else
                    {
                        return new double[] {r2x1, r1y1, r1x2, r2y2};
                    }
                }
                else
                {
                    if (within(r1y2, r2y1, r2y2))
                    {
                        return new double[] {r2x1, r2y1, r1x2, r1y2};
                    }
                    else if (within(r2y1, r1y1, r1y2))
                    {
                        return new double[] {r2x1, r2y1, r1x2, r2y2};
                    }
                }
            }
            else
            {
                if (within(r1y1, r2y1, r2y2))
                {
                    if (within(r1y2, r2y1, r2y2))
                    {
                        return new double[] {r2x1, r1y1, r2x2, r1y2};
                    }
                    else
                    {
                        return new double[] {r2x1, r1y1, r2x2, r2y2};
                    }
                }
                else
                {
                    if (within(r1y2, r2y1, r2y2))
                    {
                        return new double[] {r2x1, r2y1, r2x2, r1y2};
                    }
                    else if (pointRectangle(r2x1, r2y1, r1x1, r1y1, r1x2, r1y2))
                    {
                        return new double[] {r2x1, r2y1, r2x2, r2y2};
                    }
                }
            }
        }

        return null;
    }

    public static boolean colinear(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        return pointLine(x1, y1, x2, y2, x3, y3);
    }

    public static boolean pointLine(double x, double y, double linex1, double liney1, double linex2, double liney2)
    {
        return (linex1 - x) * (liney2 - y) == (linex2 - x) * (liney1 - y);
    }
    public static char pointLineSide(double x, double y, double linex1, double liney1, double linex2, double liney2, boolean upNegative)
    {
        //For directional line defined by (sx1, sy1), (sx2, sy2), returns whether point (x, y) is ON, to LEFT, or to RIGHT of line

        double d = (linex2 - x) * (liney1 - y) - (linex1 - x) * (liney2 - y);
        if (d > 0)
        {
            if (upNegative) return LEFT;
            return RIGHT;
        }
        if (d < 0)
        {
            if (upNegative) return RIGHT;
            return LEFT;
        }
        return ON;
    }

    public static boolean pointSegment(double x, double y, double sx1, double sy1, double sx2, double sy2)
    {
        return pointRectangle(x, y, sx1, sy1, sx2, sy2) && pointLine(x, y, sx1, sy1, sx2, sy2);
    }

    public static boolean pointConvexPolygon(double x, double y, double[] points)
    {
        //Requirements for polygon (so I don't have to make function less efficient checking these):
        //1. Must be convex
        //2. Must have at least 3 UNIQUE points (triangle)
        //3. First 3 points cannot be colinear

        char otherDir = pointLineSide(points[4], points[5], points[0], points[1], points[2], points[3], false);
        //Purposely mismatching upNegative here with the ones in checks; this allows to check whether the direction is
        //NOT equal to the OPPOSITE direction...which means we don't have to check for two cases (on line or same direction)

        for(int i = 2; i < points.length; i += 2)
        {
            if (pointLineSide(x, y, points[i - 2], points[i - 1], points[i], points[i + 1], true) == otherDir) return false;
        }
        return pointLineSide(x, y, points[points.length - 2], points[points.length - 1], points[0], points[1], true) != otherDir;
    }

    public static double[] lineLine(double line1x1, double line1y1, double line1x2, double line1y2, double line2x1, double line2y1, double line2x2, double line2y2)
    {
        boolean line1Point = line1x1 == line1x2 && line1y1 == line1y2,
                line2Point = line2x1 == line2x2 && line2y1 == line2y2;

        //Cases where one or both lines are actually points
        if (line1Point)
        {
            if (line2Point)
            {
                if (line1x1 == line2x1 && line1y1 == line2y1)
                {
                    return new double[]{line1x1, line1y1};
                }
                return null;
            }
            if (pointLine(line1x1, line1y1, line2x1, line2y1, line2x2, line2y2))
            {
                return new double[]{line1x1, line1y1};
            }
            return null;
        }
        if (line2Point)
        {
            if (pointLine(line2x1, line2y1, line1x1, line1y1, line1x2, line1y2))
            {
                return new double[]{line2x1, line2y1};
            }
            return null;
        }

        double xDist1 = line1x2 - line1x1, yDist1 = line1y2 - line1y1, xDist2 = line2x2 - line2x1, yDist2 = line2y2 - line2y1;
        double denominator = -xDist2 * yDist1 + xDist1 * yDist2;

        if (denominator == 0) //Already checked for 0 lengths, so these are parallel lines
        {
            if (colinear(line1x1, line1y1, line1x2, line1y2, line2x1, line2y1)) //Lines touch at all points
            {
                return new double[]{Double.NaN, Double.NaN};
            }
            return null; //Lines never touch
        }

        double numerator1 = xDist2 * (line1y1 - line2y1) - yDist2 * (line1x1 - line2x1);
        double scalar1 = numerator1 / denominator;

        return new double[]{line1x1 + scalar1 * xDist1, line1y1 + scalar1 * yDist1};
    }

    public static double[] segmentLine(double sx1, double sy1, double sx2, double sy2, double lx1, double ly1, double lx2, double ly2)
    {
        if (lx1 == lx2 && ly1 == ly2) //Line is a point
        {
            if (pointSegment(lx1, ly1, sx1, sy1, sx2, sy2)) return new double[] {lx1, ly1};
            return null;
        }
        if (sx1 == sx2 && sy1 == sy2) //Segment is a point
        {
            if (pointLine(sx1, sy1, lx1, ly1, lx2, ly2)) return new double[] {sx1, sy1};
        }

        //Neither are points
        double[] p1 = lineLine(lx1, ly1, lx2, ly2, sx1, sy1, sx2, sy2);

        if (p1 == null) return null; //Parallel and not colinear
        if (Double.isNaN(p1[1])) return new double[] {sx1, sy1, sx2, sy2}; //Parallel and colinear
        if (pointRectangle(p1[0], p1[1], sx1, sy1, sx2, sy2)) return p1; //Not parallel; see if point is within segment bounds
        return null;
    }

    public static boolean segmentSegment(double s1x1, double s1y1, double s1x2, double s1y2, double s2x1, double s2y1, double s2x2, double s2y2)
    {
        return segmentSegmentExt(s1x1, s1y1, s1x2, s1y2, s2x1, s2y1, s2x2, s2y2)[0] >= 8;
    }
    public static double[] segmentSegmentExt(double s1x1, double s1y1, double s1x2, double s1y2, double s2x1, double s2y1, double s2x2, double s2y2)
    {
        //Returns a double[] of varying lengths, depending on result

        //result[0] = result type, from LSB to MSB...
        //...bit0 = 1 (1) if first segment is NOT a point
        //...bit1 = 1 (2) if second segment is NOT a point
        //...bit2 = 1 (4) if segments are parallel (always 0 if either is a point)
        //...bit3 = 1 (8) if there is a collision (regardless of whether segments are points or whether collision is a segment or point)
        //...bit4 = 1 (16) if collision is a segment (segments are parallel and overlap for a portion; not just at a point)

        //result[1] and result[2] are...
        //...non-existent if there is no collision (if bit3 = 0...which means if result[0] is < 8)
        //...the collision point if there is a collision, but it is not a segment (bit3 = 1, bit4 = 0; 8 < result[0] < 16)
        //...the first point of collision (start of collision segment) if there is a collision segment (bit4 = 1; result[0] >= 16)

        //result[3] and result[4] are...
        //...non-existent if there is not a collision segment (bit4 = 0; result[0] < 16)
        //...the second point of collision (end of collision segment) if there is a collision segment (bit4 = 1; result[0] >= 16)

        double xDist1 = s1x2 - s1x1, yDist1 = s1y2 - s1y1, xDist2 = s2x2 - s2x1, yDist2 = s2y2 - s2y1;
        double denominator = -xDist2 * yDist1 + xDist1 * yDist2;

        if (denominator == 0) //Special cases; return overlapping segment if it exists, otherwise return code indicating circumstances
        {
            //Cases where one or both segments are 0 length (ie, one or both are actually points and not segments)
            if (xDist1 == 0 && yDist1 == 0)
            {
                if (xDist2 == 0 && yDist2 == 0) //Both segments are 0 length (point, point)
                {
                    if (s1x1 != s2x1 || s1y1 != s2y1)
                    {
                        return new double[]{0};
                    }

                    return new double[]{8, s1x1, s1y1};
                }
                else //First segment is 0 length, but second segment is not (point, segment)
                {
                    if (pointSegment(s1x1, s1y1, s2x1, s2y1, s2x2, s2y2))
                    {
                        return new double[]{10, s1x1, s1y1};
                    }

                    return new double[]{2};
                }
            }
            else
            {
                if (xDist2 == 0 && yDist2 == 0) //Second segment is 0 length, but first segment is not (segment, point)
                {
                    if (pointSegment(s2x1, s2y1, s1x1, s1y1, s1x2, s1y2))
                    {
                        return new double[]{9, s2x1, s2y1};
                    }

                    return new double[]{1};
                }
            }

            //Cases where segments are parallel and neither is a point
            if (!colinear(s1x1, s1y1, s1x2, s1y2, s2x1, s2y1)) //Segments not touching at all, but parallel
            {
                return new double[]{4};
            }

            //Already know they are colinear, so only need to check if points are within bounds
            boolean s1p1ONs2 = pointRectangle(s1x1, s1y1, s2x1, s2y1, s2x2, s2y2),
                    s1p2ONs2 = pointRectangle(s1x2, s1y2, s2x1, s2y1, s2x2, s2y2),
                    s2p1ONs1 = pointRectangle(s2x1, s2y1, s1x1, s1y1, s1x2, s1y2),
                    s2p2ONs1 = pointRectangle(s2x2, s2y2, s1x1, s1y1, s1x2, s1y2);

            //Segments touching, so we will be returning the overlapping segment or point (if only ends touch)
            double[] result = new double[5];

            if (s1p1ONs2 && s1p2ONs2) //S1 is fully within s2
            {
                result[0] = 31; //Neither is a point, they are parallel, there is a collision, and collision is a segment
                result[1] = s1x1; //S1p1 will be first point of collision, as it is the start of our "force" segment
                result[2] = s1y1;
                result[3] = s1x2;
                result[4] = s1y2;
                return result;
            }

            if (s2p1ONs1 && s2p2ONs1) //S2 is fully within s1
            {
                result[0] = 31; //Neither is a point, they are parallel, there is a collision, and collision is a segment
                if (s1x2 - s1x1 < 0 && s2x2 - s2x1 < 0 ||
                        s1x2 - s1x1 > 0 && s2x2 - s2x1 > 0 ||
                        s1y2 - s1y1 < 0 && s2y2 - s2y1 < 0 ||
                        s1y2 - s1y1 > 0 && s2y2 - s2y1 > 0) //This mess is true if the direction of the segments is same, meaning...
                {
                    result[1] = s2x1; //...s2p1 will be the first point of collision
                    result[2] = s2y1;
                    result[3] = s2x2;
                    result[4] = s2y2;
                }
                else //Segments are "going" opposite directions, so...
                {
                    result[1] = s2x2; //...s2p2 will be the first point of collision
                    result[2] = s2y2;
                    result[3] = s2x1;
                    result[4] = s2y1;
                }
                return result;
            }

            result[0] = 15; //Neither is a point, they are parallel, and there is a collision

            //They touch, and neither fully within other, partial overlap, meaning each segment has exactly 1 point on other segment
            if (s1p1ONs2)
            {
                result[1] = s1x1; //If s1p1 is part of overlapping segment, it will be the first contact point no matter what
                result[2] = s1y1;

                if (s2p1ONs1)
                {
                    result[3] = s2x1;
                    result[4] = s2y1;
                }
                else
                {
                    result[3] = s2x2;
                    result[4] = s2y2;
                }
            }
            else
            {
                result[3] = s1x2; //If s1p1 is NOT part of overlapping segment, s1p2 IS, and will be LAST contact point, no matter what
                result[4] = s1y2;

                if (s2p1ONs1)
                {
                    result[1] = s2x1;
                    result[2] = s2y1;
                }
                else
                {
                    result[1] = s2x2;
                    result[2] = s2y2;
                }
            }

            if (result[1] != result[3] || result[2] != result[4]) result[0] += 16; //Collision is a segment
            return result;
        }

        double numerator1 = xDist2 * (s1y1 - s2y1) - yDist2 * (s1x1 - s2x1),
                numerator2 = -yDist1 * (s1x1 - s2x1) + xDist1 * (s1y1 - s2y1);

        if (denominator > 0)
        {
            if (numerator2 >= 0 && numerator2 <= denominator && numerator1 >= 0 && numerator1 <= denominator)
            {
                double scalar1 = numerator1 / denominator;

                return new double[]{11, s1x1 + scalar1 * xDist1, s1y1 + scalar1 * yDist1};
            }
        }
        else
        {
            if (numerator2 >= denominator && numerator2 <= 0 && numerator1 >= denominator && numerator1 <= 0)
            {
                double scalar1 = numerator1 / denominator;

                return new double[]{11, s1x1 + scalar1 * xDist1, s1y1 + scalar1 * yDist1};
            }
        }

        return new double[]{3};
    }

    public static double[] lineRectangle(double x1, double y1, double x2, double y2, double rx1, double ry1, double rx2, double ry2)
    {
        if (x1 == x2 && y1 == y2) //Line is a point
        {
            if (pointRectangle(x1, y1, rx1, ry1, rx2, ry2)) return new double[] {x1, y1};
            return null;
        }

        if (rx1 == rx2) //Rectangle is either a segment or a point
        {
            if (ry1 == ry2) //Rectangle is a point
            {
                if (pointLine(rx1, ry1, x1, y1, x2, y2)) return new double[] {rx1, ry1};
                return null;
            }
            else //Rectangle is a vertical segment
            {
                return segmentLine(rx1, ry1, rx2, ry2, x1, y1, x2, y2);
            }
        }
        else if (ry1 == ry2) //Rectangle is a horizontal segment
        {
            return segmentLine(rx1, ry1, rx2, ry2, x1, y1, x2, y2);
        }

        //Line is not a point, rectangle is not a point or segment

        double points[] = segmentLine(rx1, ry1, rx2, ry1, x1, y1, x2, y2), result[] = new double[4];
        boolean onePoint = false;
        if (points != null)
        {
            if (points.length == 4) return points;
            result[0] = points[0];
            result[1] = points[1];
            onePoint = true;
        }

        points = segmentLine(rx1, ry2, rx2, ry2, x1, y1, x2, y2);
        if (points != null)
        {
            if (points.length == 4) return points;
            if (onePoint)
            {
                //Don't need to check for duplicate points, because we checked opposite rectangle sides (and rectangle is not a segment or point)
                result[2] = points[0];
                result[3] = points[1];
                return result;
            }
            else
            {
                result[0] = points[0];
                result[1] = points[1];
                onePoint = true;
            }
        }

        points = segmentLine(rx1, ry1, rx1, ry2, x1, y1, x2, y2);
        if (points != null)
        {
            if (points.length == 4) return points;
            if (onePoint)
            {
                //Now we need to check for duplicate points
                if (points[0] != result[0] || points[1] != result[1])
                {
                    result[2] = points[0];
                    result[3] = points[1];
                    return result;
                }
            }
            else
            {
                result[0] = points[0];
                result[1] = points[1];
                onePoint = true;
            }
        }

        points = segmentLine(rx2, ry1, rx2, ry2, x1, y1, x2, y2);
        if (points != null)
        {
            if (points.length == 4) return points;
            if (onePoint)
            {
                //Now we need to check for duplicate points
                if (points[0] != result[0] || points[1] != result[1])
                {
                    result[2] = points[0];
                    result[3] = points[1];
                    return result;
                }
            }
            else
            {
                result[0] = points[0];
                result[1] = points[1];
                onePoint = true;
            }
        }

        if (onePoint) return new double[] {result[0], result[1]};
        return null;
    }

    public static double[] segmentRectangle(double x1, double y1, double x2, double y2, double rx1, double ry1, double rx2, double ry2)
    {
        if (x1 == x2 && y1 == y2)
        {
            if (pointRectangle(x1, y1, rx1, ry1, rx2, ry2)) return new double[] {x1, y1};
            return null;
        }

        double result[];
        if (rx1 == rx2)
        {
            if (ry1 == ry2)
            {
                if (pointSegment(rx1, ry1, x1, y1, x2, y2)) return new double[] {rx1, ry1};
                return null;
            }
            result = segmentSegmentExt(x1, y1, x2, y2, rx1, ry1, rx2, ry2);
            if (result.length == 1) return null;
            if (result.length == 3) return new double[] {result[1], result[2]};
            return new double[] {result[1], result[2], result[3], result[4]};
        }
        if (ry1 == ry2)
        {
            result = segmentSegmentExt(x1, y1, x2, y2, rx1, ry1, rx2, ry2);
            if (result.length == 1) return null;
            if (result.length == 3) return new double[] {result[1], result[2]};
            return new double[] {result[1], result[2], result[3], result[4]};
        }

        //Segment is not a point and rectangle is not a segment or a point
        if (pointRectangle(x1, y1, rx1, ry1, rx2, ry2)) //First segment point is within rectangle
        {
            if (pointRectangle(x2, y2, rx1, ry1, rx2, ry2)) return new double[] {x1, y1, x2, y2}; //Both segment points are within rectangle

            //First segment point is within rectangle, second is not
            //Top
            result = segmentSegmentExt(x1, y1, x2, y2, rx1, ry1, rx2, ry1);
            if (result.length == 5) return new double[] {result[1], result[2], result[3], result[4]};
            if (result[1] != x1 || result[2] != y1) return new double[] {x1, y1, result[1], result[2]};
            //Bottom
            result = segmentSegmentExt(x1, y1, x2, y2, rx1, ry2, rx2, ry2);
            if (result.length == 5) return new double[] {result[1], result[2], result[3], result[4]};
            if (result[1] != x1 || result[2] != y1) return new double[] {x1, y1, result[1], result[2]};
            //Left
            result = segmentSegmentExt(x1, y1, x2, y2, rx1, ry1, rx1, ry2);
            if (result.length == 5) return new double[] {result[1], result[2], result[3], result[4]};
            if (result[1] != x1 || result[2] != y1) return new double[] {x1, y1, result[1], result[2]};
            //Right
            result = segmentSegmentExt(x1, y1, x2, y2, rx2, ry1, rx2, ry2);
            if (result.length == 5) return new double[] {result[1], result[2], result[3], result[4]};
            if (result[1] != x1 || result[2] != y1) return new double[] {x1, y1, result[1], result[2]};
            //None of the above gave a new point, so first segment point must be on edge of rectangle
            return new double[] {x1, y1};
        }
        if (pointRectangle(x2, y2, rx1, ry1, rx2, ry2)) //Second segment point is within rectangle, first is not
        {
            //Top
            result = segmentSegmentExt(x1, y1, x2, y2, rx1, ry1, rx2, ry1);
            if (result.length == 5) return new double[] {result[1], result[2], result[3], result[4]};
            if (result[1] != x2 || result[2] != y2) return new double[] {result[1], result[2], x2, y2};
            //Bottom
            result = segmentSegmentExt(x1, y1, x2, y2, rx1, ry2, rx2, ry2);
            if (result.length == 5) return new double[] {result[1], result[2], result[3], result[4]};
            if (result[1] != x2 || result[2] != y2) return new double[] {result[1], result[2], x2, y2};
            //Left
            result = segmentSegmentExt(x1, y1, x2, y2, rx1, ry1, rx1, ry2);
            if (result.length == 5) return new double[] {result[1], result[2], result[3], result[4]};
            if (result[1] != x2 || result[2] != y2) return new double[] {result[1], result[2], x2, y2};
            //Right
            result = segmentSegmentExt(x1, y1, x2, y2, rx2, ry1, rx2, ry2);
            if (result.length == 5) return new double[] {result[1], result[2], result[3], result[4]};
            if (result[1] != x2 || result[2] != y2) return new double[] {result[1], result[2], x2, y2};
            //None of the above gave a new point, so second segment point must be on edge of rectangle
            return new double[] {x2, y2};
        }

        //Both segment points are outside the rectangle
        //Top
        result = segmentSegmentExt(x1, y1, x2, y2, rx1, ry1, rx2, ry1);
        if (result.length == 5) return new double[] {result[1], result[2], result[3], result[4]};
        double prevResult[] = null;
        if (result.length == 3) prevResult = result;
        //Bottom
        result = segmentSegmentExt(x1, y1, x2, y2, rx1, ry2, rx2, ry2);
        if (result.length == 5) return new double[] {result[1], result[2], result[3], result[4]};
        double nearest[];
        if (result.length == 3)
        {
            if (prevResult == null) prevResult = result;
            else
            {
                if (prevResult[1] != result[1] || prevResult[2] != result[2])
                {
                    nearest = pointListNearest(x1, y1, prevResult[1], prevResult[2], result[1], result[2]);
                    if (result[1] != nearest[0]  || result[2] != nearest[1]) return new double[] {nearest[0], nearest[1], result[1], result[2]};
                    return new double[] {nearest[0], nearest[1], prevResult[1], prevResult[2]};
                }
            }
        }
        //Left
        result = segmentSegmentExt(x1, y1, x2, y2, rx1, ry1, rx1, ry2);
        if (result.length == 5) return new double[] {result[1], result[2], result[3], result[4]};
        if (result.length == 3)
        {
            if (prevResult == null) prevResult = result;
            else
            {
                if (prevResult[1] != result[1] || prevResult[2] != result[2])
                {
                    nearest = pointListNearest(x1, y1, prevResult[1], prevResult[2], result[1], result[2]);
                    if (result[1] != nearest[0]  || result[2] != nearest[1]) return new double[] {nearest[0], nearest[1], result[1], result[2]};
                    return new double[] {nearest[0], nearest[1], prevResult[1], prevResult[2]};
                }
            }
        }
        //Right
        result = segmentSegmentExt(x1, y1, x2, y2, rx1, ry1, rx1, ry2);
        if (result.length == 5) return new double[] {result[1], result[2], result[3], result[4]};
        if (result.length == 3)
        {
            if (prevResult == null || (prevResult[1] == result[1] && prevResult[2] == result[2])) return new double[] {result[1], result[2]};

            nearest = pointListNearest(x1, y1, prevResult[1], prevResult[2], result[1], result[2]);
            if (result[1] != nearest[0]  || result[2] != nearest[1]) return new double[] {nearest[0], nearest[1], result[1], result[2]};
            return new double[] {nearest[0], nearest[1], prevResult[1], prevResult[2]};
        }

        if (prevResult != null) return new double[] {prevResult[1], prevResult[2]};
        return null;
    }

    public static double[] segmentConvexPolygon(double x1, double y1, double x2, double y2, double[] points)
    {
        //Same polygon requirements as pointConvexPolygon with one addition...
        //...no sides can be 0 length because it could mess with the final case's loop
        //If no collision, returns null
        //If one collision point, returns pair of doubles containing it
        //If collision segment, returns two pairs of doubles containing it, first point being initial collision point

        if (x1 == x2 && y1 == y2) //Segment is actually point
        {
            if (pointConvexPolygon(x1, y1, points))
            {
                return new double[]{x1, y1};
            }
            return null;
        }

        //Segment is not a point
        double result[];
        if (pointConvexPolygon(x1, y1, points)) //p1 within polygon, so it is first collision point
        {
            if (pointConvexPolygon(x2, y2, points)) //p2 also within, so it is endpoint of collision segment
            {
                //Segment, because we already checked that the points are not the same
                return new double[]{x1, y1, x2, y2};
            }

            //p1 within, p2 outside; find end of collision segment
            for(int i = 2; i < points.length; i += 2)
            {
                result = segmentSegmentExt(x1, y1, x2, y2, points[i - 2], points[i - 1], points[i], points[i + 1]);
                if (result.length == 5) return new double[] {result[1], result[2], result[3], result[4]};
                if (result.length == 3 && (result[1] != x1 || result[2] != y1)) return new double[] {x1, y1, result[1], result[2]};
            }
            //Final segment check (from last point of poly to first point of poly)
            result = segmentSegmentExt(x1, y1, x2, y2, points[points.length - 2], points[points.length - 1], points[0], points[1]);
            if (result.length == 5) return new double[] {result[1], result[2], result[3], result[4]};
            if (result.length == 3 && (result[1] != x1 || result[2] != y1)) return new double[] {x1, y1, result[1], result[2]};

            //Start point on edge of polygon, going outwards
            return new double[]{x1, y1};
        }

        //P1 outside polygon
        if (pointConvexPolygon(x2, y2, points)) //p1 outside, but p2 within, so p2 is endpoint of collision segment
        {
            for(int i = 2; i < points.length; i += 2)
            {
                result = segmentSegmentExt(x1, y1, x2, y2, points[i - 2], points[i - 1], points[i], points[i + 1]);
                if (result.length == 5) return new double[] {result[1], result[2], result[3], result[4]};
                if (result.length == 3 && (result[1] != x2 || result[2] != y2)) return new double[] {result[1], result[2], x2, y2};
            }
            //Final segment check (from last point of poly to first point of poly)
            result = segmentSegmentExt(x1, y1, x2, y2, points[points.length - 2], points[points.length - 1], points[0], points[1]);
            if (result.length == 5) return new double[] {result[1], result[2], result[3], result[4]};
            if (result.length == 3 && (result[1] != x2 || result[2] != y2)) return new double[] {result[1], result[2], x2, y2};

            //End point on edge of polygon, coming from outside
            return new double[]{x2, y2};
        }

        //p1 and p2 BOTH outside polygon, so if there is a collision, it is intersecting multiple sides of the polygon
        double[] prevResult = null, nearest;
        for(int i = 2; i < points.length; i += 2)
        {
            result = segmentSegmentExt(x1, y1, x2, y2, points[i - 2], points[i - 1], points[i], points[i + 1]);
            if (result.length == 5) return new double[] {result[1], result[2], result[3], result[4]};
            if (result.length == 3)
            {
                if (prevResult == null) prevResult = result;
                else if (result[1] != prevResult[1] || result[2] != prevResult[2])
                {
                    nearest = pointListNearest(x1, y1, result[1], result[2], prevResult[1], prevResult[2]);
                    if (result[1] != nearest[0]  || result[2] != nearest[1]) return new double[] {nearest[0], nearest[1], result[1], result[2]};
                    return new double[] {nearest[0], nearest[1], prevResult[1], prevResult[2]};
                }
            }
        }
        result = segmentSegmentExt(x1, y1, x2, y2, points[points.length - 2], points[points.length - 1], points[0], points[1]);
        if (result.length == 5) return new double[] {result[1], result[2], result[3], result[4]};
        if (result.length == 3)
        {
            if (prevResult == null || (result[1] == prevResult[1] && result[2] == prevResult[2])) return new double[] {result[1], result[2]};

            nearest = pointListNearest(x1, y1, result[1], result[2], prevResult[1], prevResult[2]);
            if (result[1] != nearest[0]  || result[2] != nearest[1]) return new double[] {nearest[0], nearest[1], result[1], result[2]};
            return new double[] {nearest[0], nearest[1], prevResult[1], prevResult[2]};
        }

        return null;
    }

    public static double[][] segmentConvexPolygonExt(double x1, double y1, double x2, double y2, double[] points)
    {
        //Same polygon requirements as pointConvexPolygon with one addition...
        //...no sides can be 0 length because it could mess with the final case's loop
        //If no collision, returns null
        //If one collision point, returns pair of doubles containing it
        //If collision segment, returns two pairs of doubles containing it, first point being initial collision point

        //Second return array is sides collided with at start of collision but not at end of collision
        //Third return array is sides collided with at both start and end of collision
        //Fourth return array is sides collided with at end of collision but not at start

        if (x1 == x2 && y1 == y2) //Segment is actually point
        {
            if (pointConvexPolygon(x1, y1, points))
            {
                return new double[][]{{x1, y1}, null, null};
            }
            return null;
        }

        //Segment is not a point
        boolean startCoords = false, endCoords = false;
        double result[], finalResult[] = new double[4], startSides[] = null, midSide[] = null, endSides[] = null;

        if (pointConvexPolygon(x1, y1, points)) //p1 within polygon, so it is first collision point
        {
            if (pointConvexPolygon(x2, y2, points)) //p2 also within, so it is endpoint of collision segment
            {
                //Segment, because we already checked that the points are not the same
                //No polygon sides collided with, since start and end of segment are both within poly
                return new double[][]{{x1, y1, x2, y2}, null, null};
            }

            //p1 within, p2 outside; find end of collision segment
            finalResult[0] = x1;
            finalResult[1] = y1;

            for(int i = 2; i < points.length; i += 2)
            {
                result = segmentSegmentExt(x1, y1, x2, y2, points[i - 2], points[i - 1], points[i], points[i + 1]);
                if (result.length == 5)
                {
                    finalResult[2] = result[3];
                    finalResult[3] = result[4];
                    endCoords = true;

                    midSide = new double[]{points[i - 2], points[i - 1], points[i], points[i + 1]};
                }
                if (result.length == 3)
                {
                    if (result[1] != x1 || result[2] != y1)
                    {
                        finalResult[2] = result[1];
                        finalResult[3] = result[2];
                        endCoords = true;

                        if (endSides == null) endSides = new double[]{points[i - 2], points[i - 1], points[i], points[i + 1]};
                        else
                        {
                            result = endSides;
                            endSides = new double[]{result[0], result[1], result[2], result[3], points[i - 2], points[i - 1], points[i], points[i + 1]};
                        }
                    }
                    else
                    {
                        if (startSides == null) startSides = new double[]{points[i - 2], points[i - 1], points[i], points[i + 1]};
                        else
                        {
                            result = startSides;
                            startSides = new double[]{result[0], result[1], result[2], result[3], points[i - 2], points[i - 1], points[i], points[i + 1]};
                        }
                    }
                }
            }
            //Final segment check (from last point of poly to first point of poly)
            result = segmentSegmentExt(x1, y1, x2, y2, points[points.length - 2], points[points.length - 1], points[0], points[1]);
            if (result.length == 5)
            {
                finalResult[2] = result[3];
                finalResult[3] = result[4];
                endCoords = true;

                midSide = new double[]{points[points.length - 2], points[points.length - 1], points[0], points[1]};
            }
            if (result.length == 3)
            {
                if (result[1] != x1 || result[2] != y1)
                {
                    finalResult[2] = result[1];
                    finalResult[3] = result[2];
                    endCoords = true;

                    if (endSides == null) endSides = new double[]{points[points.length - 2], points[points.length - 1], points[0], points[1]};
                    else
                    {
                        result = endSides;
                        endSides = new double[]{result[0], result[1], result[2], result[3], points[points.length - 2], points[points.length - 1], points[0], points[1]};
                    }
                }
                else
                {
                    if (startSides == null) startSides = new double[]{points[points.length - 2], points[points.length - 1], points[0], points[1]};
                    else
                    {
                        result = startSides;
                        startSides = new double[]{result[0], result[1], result[2], result[3], points[points.length - 2], points[points.length - 1], points[0], points[1]};
                    }
                }
            }

            if (endCoords) return new double[][]{finalResult, startSides, midSide, endSides};
            else return new double[][]{{finalResult[0], finalResult[1]}, startSides, null, null};
        }

        //P1 outside polygon
        if (pointConvexPolygon(x2, y2, points)) //p1 outside, but p2 within, so p2 is endpoint of collision segment
        {
            finalResult[2] = x2;
            finalResult[3] = y2;

            for(int i = 2; i < points.length; i += 2)
            {
                result = segmentSegmentExt(x1, y1, x2, y2, points[i - 2], points[i - 1], points[i], points[i + 1]);
                if (result.length == 5)
                {
                    finalResult[0] = result[1];
                    finalResult[1] = result[2];
                    startCoords = true;

                    midSide = new double[]{points[i - 2], points[i - 1], points[i], points[i + 1]};
                }
                if (result.length == 3)
                {
                    if (result[1] != x2 || result[2] != y2)
                    {
                        finalResult[0] = result[1];
                        finalResult[1] = result[2];
                        startCoords = true;

                        if (startSides == null) startSides = new double[]{points[i - 2], points[i - 1], points[i], points[i + 1]};
                        else
                        {
                            result = startSides;
                            startSides = new double[]{result[0], result[1], result[2], result[3], points[i - 2], points[i - 1], points[i], points[i + 1]};
                        }
                    }
                    else
                    {
                        if (endSides == null) endSides = new double[]{points[i - 2], points[i - 1], points[i], points[i + 1]};
                        else
                        {
                            result = endSides;
                            endSides = new double[]{result[0], result[1], result[2], result[3], points[i - 2], points[i - 1], points[i], points[i + 1]};
                        }
                    }
                }
            }
            //Final segment check (from last point of poly to first point of poly)
            result = segmentSegmentExt(x1, y1, x2, y2, points[points.length - 2], points[points.length - 1], points[0], points[1]);
            if (result.length == 5)
            {
                finalResult[0] = result[1];
                finalResult[1] = result[2];
                startCoords = true;

                midSide = new double[]{points[points.length - 2], points[points.length - 1], points[0], points[1]};
            }
            if (result.length == 3)
            {
                if (result[1] != x2 || result[2] != y2)
                {
                    finalResult[0] = result[1];
                    finalResult[1] = result[2];
                    startCoords = true;

                    if (startSides == null) startSides = new double[]{points[points.length - 2], points[points.length - 1], points[0], points[1]};
                    else
                    {
                        result = startSides;
                        startSides = new double[]{result[0], result[1], result[2], result[3], points[points.length - 2], points[points.length - 1], points[0], points[1]};
                    }
                }
                else
                {
                    if (endSides == null) endSides = new double[]{points[points.length - 2], points[points.length - 1], points[0], points[1]};
                    else
                    {
                        result = endSides;
                        endSides = new double[]{result[0], result[1], result[2], result[3], points[points.length - 2], points[points.length - 1], points[0], points[1]};
                    }
                }
            }

            if (startCoords) return new double[][]{finalResult, startSides, midSide, endSides};
            else return new double[][]{{finalResult[2], finalResult[3]}, endSides, null, null};
        }

        //p1 and p2 BOTH outside polygon, so if there is a collision, it is intersecting multiple sides of the polygon
        double[] nearest;
        for(int i = 2; i < points.length; i += 2)
        {
            result = segmentSegmentExt(x1, y1, x2, y2, points[i - 2], points[i - 1], points[i], points[i + 1]);
            if (result.length == 5)
            {
                if (startCoords && (finalResult[0] != result[1] || finalResult[1] != result[2]))
                {
                    endSides = startSides;
                    startSides = null;
                }

                finalResult[0] = result[1];
                finalResult[1] = result[2];
                finalResult[2] = result[3];
                finalResult[3] = result[4];
                startCoords = true;
                endCoords = true;

                midSide = new double[]{points[i - 2], points[i - 1], points[i], points[i + 1]};
            }
            if (result.length == 3)
            {
                if (!startCoords)
                {
                    finalResult[0] = result[1];
                    finalResult[1] = result[2];
                    startCoords = true;

                    startSides = new double[]{points[i - 2], points[i - 1], points[i], points[i + 1]};
                }
                else
                {
                    if (finalResult[0] == result[1] && finalResult[1] == result[2])
                    {
                        if (startSides == null)
                        {
                            startSides = new double[]{points[i - 2], points[i - 1], points[i], points[i + 1]};
                        }
                        else
                        {
                            result = startSides;
                            startSides = new double[]{result[0], result[1], result[2], result[3], points[i - 2], points[i - 1], points[i], points[i + 1]};
                        }
                    }
                    else if (endCoords && finalResult[2] == result[1] && finalResult[3] == result[2])
                    {
                        if (endSides == null)
                        {
                            endSides = new double[]{points[i - 2], points[i - 1], points[i], points[i + 1]};
                        }
                        else
                        {
                            result = endSides;
                            endSides = new double[]{result[0], result[1], result[2], result[3], points[i - 2], points[i - 1], points[i], points[i + 1]};
                        }
                    }
                    else //Is not current start side or current end side, which means current start side could actually be end side
                    {
                        endCoords = true;

                        nearest = pointListNearest(x1, y1, finalResult[0], finalResult[1], result[1], result[2]);
                        if (nearest[0] == finalResult[0] && nearest[1] == finalResult[1])
                        {
                            finalResult[2] = result[1];
                            finalResult[3] = result[2];

                            endSides = new double[]{points[i - 2], points[i - 1], points[i], points[i + 1]};
                        }
                        else
                        {
                            finalResult[2] = finalResult[0];
                            finalResult[3] = finalResult[1];
                            finalResult[0] = result[1];
                            finalResult[1] = result[2];

                            endSides = startSides;
                            startSides = new double[]{points[i - 2], points[i - 1], points[i], points[i + 1]};
                        }
                    }
                }
            }
        }
        result = segmentSegmentExt(x1, y1, x2, y2, points[points.length - 2], points[points.length - 1], points[0], points[1]);
        if (result.length == 5)
        {
            if (startCoords && (finalResult[0] != result[1] || finalResult[1] != result[2]))
            {
                endSides = startSides;
                startSides = null;
            }

            finalResult[0] = result[1];
            finalResult[1] = result[2];
            finalResult[2] = result[3];
            finalResult[3] = result[4];
            startCoords = true;
            endCoords = true;

            midSide = new double[]{points[points.length - 2], points[points.length - 1], points[0], points[1]};
        }
        if (result.length == 3)
        {
            if (!startCoords)
            {
                finalResult[0] = result[1];
                finalResult[1] = result[2];
                startCoords = true;

                startSides = new double[]{points[points.length - 2], points[points.length - 1], points[0], points[1]};
            }
            else
            {
                if (finalResult[0] == result[1] && finalResult[1] == result[2])
                {
                    if (startSides == null)
                    {
                        startSides = new double[]{points[points.length - 2], points[points.length - 1], points[0], points[1]};
                    }
                    else
                    {
                        result = startSides;
                        startSides = new double[]{result[0], result[1], result[2], result[3], points[points.length - 2], points[points.length - 1], points[0], points[1]};
                    }
                }
                else if (endCoords && finalResult[2] == result[1] && finalResult[3] == result[2])
                {
                    if (endSides == null)
                    {
                        endSides = new double[]{points[points.length - 2], points[points.length - 1], points[0], points[1]};
                    }
                    else
                    {
                        result = endSides;
                        endSides = new double[]{result[0], result[1], result[2], result[3], points[points.length - 2], points[points.length - 1], points[0], points[1]};
                    }
                }
                else
                {
                    endCoords = true;

                    nearest = pointListNearest(x1, y1, finalResult[0], finalResult[1], result[1], result[2]);
                    if (nearest[0] == finalResult[0] && nearest[1] == finalResult[1])
                    {
                        finalResult[2] = result[1];
                        finalResult[3] = result[2];

                        endSides = new double[]{points[points.length - 2], points[points.length - 1], points[0], points[1]};
                    }
                    else
                    {
                        finalResult[2] = finalResult[0];
                        finalResult[3] = finalResult[1];
                        finalResult[0] = result[1];
                        finalResult[1] = result[2];

                        endSides = startSides;
                        startSides = new double[]{points[points.length - 2], points[points.length - 1], points[0], points[1]};
                    }
                }
            }
        }

        if (!endCoords)
        {
            if (!startCoords)
            {
                return null;
            }

            return new double[][]{{finalResult[0], finalResult[1]}, startSides, null, null};
        }

        return new double[][]{finalResult, startSides, midSide, endSides};
    }

    public static double[] lineConvexPolygon(double x1, double y1, double x2, double y2, double[] points)
    {
        //Same polygon requirements as segmentConvexPolygon

        if (x1 == x2 && y1 == y2) //Line is actually point
        {
            if (pointConvexPolygon(x1, y1, points))
            {
                return new double[]{x1, y1};
            }
            return null;
        }

        //Line is not a point
        double[] result, prevResult = null;
        for (int i = 2; i < points.length; i += 2)
        {
            result = segmentLine(points[i - 2], points[i - 1], points[i], points[i + 1], x1, y1, x2, y2);
            if (result != null)
            {
                if (result.length == 4) return result;

                if (prevResult == null) prevResult = result;
                else if (result[0] != prevResult[0] || result[1] != prevResult[1])
                {
                    return new double[]{result[0], result[1], prevResult[1], prevResult[2]};
                }
            }
        }
        result = segmentLine(points[points.length - 2], points[points.length - 1], points[0], points[1], x1, y1, x2, y2);
        if (result != null)
        {
            if (result.length == 4) return result;

            if (prevResult == null || (result[0] == prevResult[0] && result[1] == prevResult[1])) return result;
            return new double[]{result[0], result[1], prevResult[1], prevResult[2]};
        }

        return prevResult;
    }

    public static double[] pointListNearest(double x, double y, double... arg)
    {
        if (arg == null || arg.length < 2) return null;

        double distSquared = Math.pow(x - arg[0], 2) + Math.pow(y - arg[1], 2);
        double result[] = new double[] {arg[0], arg[1]};

        double checkDistSquared;
        for(int i = 2; i + 1 < arg.length; i += 2)
        {
            checkDistSquared = Math.pow(x - arg[i], 2) + Math.pow(y - arg[i + 1], 2);
            if (checkDistSquared < distSquared)
            {
                distSquared = checkDistSquared;
                result[0] = arg[i];
                result[1] = arg[i + 1];
            }
        }

        return result;
    }

    public static boolean rectangleConvexPolygon(double x1, double y1, double x2, double y2, double[] points)
    {
        if (x1 == x2)
        {
            if (y1 == y2)
            {
                return pointConvexPolygon(x1, y1, points);
            }

            return segmentConvexPolygon(x1, y1, x2, y2, points) != null;
        }
        if (y1 == y2) return segmentConvexPolygon(x1, y1, x2, y2, points) != null;

        return (pointRectangle(points[0], points[1], x1, y1, x2, y2) || segmentConvexPolygon(x1, y1, x2, y1, points) != null || segmentConvexPolygon(x2, y1, x2, y2, points) != null || segmentConvexPolygon(x2, y2, x1, y2, points) != null || segmentConvexPolygon(x1, y2, x1, y1, points) != null);
    }

    public static boolean convexPolygonConvexPolygon(double[] points1, double[] points2)
    {
        for(int i = 2; i < points1.length; i += 2)
        {
            if (segmentConvexPolygon(points1[i - 2], points1[i - 1], points1[i], points1[i + 1], points2) != null) return true;
        }
        return (segmentConvexPolygon(points1[points1.length - 2], points1[points1.length - 1], points1[0], points1[1], points2) != null);
    }

    public static double[] pointLineNearest(double x, double y, double x1, double y1, double x2, double y2)
    {
        return lineLine(x1, y1, x2, y2, x, y, x + y1 - y2, y - x1 + x2);
    }

    public static double[] pointSegmentNearest(double x, double y, double x1, double y1, double x2, double y2)
    {
        double[] p1 = pointLineNearest(x, y, x1, y1, x2, y2);
        if (p1 == null) return null; //Should only happen if segment is a point
        if (pointRectangle(p1[0], p1[1], x1, y1, x2, y2)) return p1;
        if (Math.pow(x1 - x, 2) + Math.pow(y1 - y, 2) < Math.pow(x2 - x, 2) + Math.pow(y2 - y, 2)) return new double[] {x1, y1};
        return new double[] {x2, y2};
    }

    public static double[] pointRectangleNearest(double x, double y, double x1, double y1, double x2, double y2)
    {
        double[] result = new double[2];
        if (x1 > x2)
        {
            result[0] = x1;
            x1 = x2;
            x2 = result[0];
        }
        if (y1 > y2)
        {
            result[0] = y1;
            y1 = y2;
            y2 = result[0];
        }

        result[0] = x2;
        result[1] = y2;

        if (x < x1) result[0] = x1;
        else if (x < x2) result[0] = x;

        if (y < y1) result[1] = y1;
        else if (y < y2) result[1] = y;

        return result;
    }

    public static double[] pointConvexPolygonNearest(double x, double y, double[] points)
    {
        //Same requirements as pointConvexPolygon

        if (pointConvexPolygon(x, y, points)) return new double[] {x, y};

        double[] result = new double[points.length];
        double[] point = pointSegmentNearest(x, y, points[points.length - 2], points[points.length - 1], points[0], points[1]);
        System.arraycopy(point, 0, result, 0, 2);

        for(int i = 2; i < points.length; i += 2)
        {
            point = pointSegmentNearest(x, y, points[i - 2], points[i - 1], points[i], points[i + 1]);
            System.arraycopy(point, 0, result, i, 2);
        }

        return pointListNearest(x, y, result);
    }

    public static boolean pointCircle(double x, double y, double cx, double cy, double r)
    {
        return Math.pow(cx - x, 2) + Math.pow(cy - y, 2) <= Math.pow(r, 2);
    }

    public static double[] lineCircle(double x1, double y1, double x2, double y2, double cx, double cy, double r)
    {
        if (x1 == x2 && y1 == y2)
        {
            if (pointCircle(x1, y1, cx, cy, r)) return new double[] {x1, y1};
            return null;
        }

        double rSquared = Math.pow(r, 2);
        double[] p1 = pointLineNearest(cx, cy, x1, y1, x2, y2);
        double p1dSquared = Math.pow(p1[0] - cx, 2) + Math.pow(p1[1] - cy, 2); //Distance from p1 to center of circle...squared

        if (p1dSquared > rSquared) return null;

        if (p1dSquared < rSquared)
        {
            double legLength = Math.sqrt(rSquared - p1dSquared);
            double p1d2 = Math.sqrt(Math.pow(p1[0] - x1, 2) + Math.pow(p1[1] - y1, 2)); //Distance from p1 to (x1, y1)

            double ratio, xd, yd;
            if (p1d2 != 0)
            {
                ratio = legLength / p1d2;
                xd = ratio * (p1[0] - x1);
                yd = ratio * (p1[1] - y1);

                if (x2 >= x1 && pointCircle(x1, y1, cx, cy, r)) return new double[] {p1[0] + xd, p1[1] + yd, p1[0] - xd, p1[1] - yd};
                return new double[] {p1[0] - xd, p1[1] - yd, p1[0] + xd, p1[1] + yd};
            }

            p1d2 = Math.sqrt(Math.pow(p1[0] - x2, 2) + Math.pow(p1[1] - y2, 2)); //Swapping in distance from p1 to (x2, y2)...because we need the denominator of the ratio to be non-zero

            ratio = legLength / p1d2;
            xd = ratio * (p1[1] - x2);
            yd = ratio * (p1[2] - y2);

            return new double[] {p1[0] + xd, p1[1] + yd, p1[0] - xd, p1[1] - yd};
        }

        return p1;
    }

    public static double[] segmentCircle(double x1, double y1, double x2, double y2, double cx, double cy, double r)
    {
        if (x1 == x2 && y1 == y2)
        {
            if (pointCircle(x1, y1, cx, cy, r)) return new double[] {x1, y1};
            return null;
        }

        double[] points = lineCircle(x1, y1, x2, y2, cx, cy, r);

        if (points == null) return null;

        if (points.length == 2)
        {
            if (pointRectangle(points[0], points[1], x1, y1, x2, y2)) return points;
            return null;
        }

        double[] nearest;
        if (pointRectangle(x1, y1, points[0], points[1], points[2], points[3])) //First segment point is intersecting
        {
            if (pointRectangle(x2, y2, points[0], points[1], points[2], points[3])) return new double[] {x1, y1, x2, y2}; //Both are

            //First segment point is intersecting circle but second is not
            nearest = pointListNearest(x2, y2, points[0], points[1], points[2], points[3]);
            if (nearest[0] != x1 || nearest[1] != y1) return new double[] {x1, y1, nearest[0], nearest[1]};
            return new double[] {x1, y1};
        }

        if (pointRectangle(x2, y2, points[0], points[1], points[2], points[3]))
        {
            //Second segment point is intersecting circle but first is not
            nearest = pointListNearest(x1, y1, points[0], points[1], points[2], points[3]);
            if (nearest[0] != x2 || nearest[1] != y2) return new double[] {nearest[0], nearest[1], x2, y2};
            return new double[] {x2, y2};
        }

        //Neither segment ends are intersecting circle; either passing through all the way or no collision
        if ((points[0] == x1 && points[1] == y1 && points[2] == x2 && points[3] == y2) || (points[0] == x2 && points[1] == y2 && points[2] == x1 && points[3] == y1))
        {
            return points;
        }
        return null;
    }

    public static boolean rectangleCircle(double x1, double y1, double x2, double y2, double cx, double cy, double r)
    {
        if (pointRectangle(cx, cy, x1, y1, x2, y2)) return true;

        //Center of circle is not within rectangle (so if no part of rectangle is inside circle, no collision
        if (segmentCircle(x1, y1, x2, y1, cx, cy, r) != null) return true;
        if (segmentCircle(x1, y2, x2, y2, cx, cy, r) != null) return true;
        if (segmentCircle(x1, y1, x1, y2, cx, cy, r) != null) return true;
        return segmentCircle(x2, y1, x2, y2, cx, cy, r) != null;
    }

    public static boolean circleConvexPolygon(double cx, double cy, double r, double[] points)
    {
        if (pointConvexPolygon(cx, cy, points)) return true;

        if (segmentCircle(points[points.length - 2], points[points.length - 1], points[0], points[1], cx, cy, r) != null) return true;
        for(int i = 2; i < points.length; i += 2)
        {
            if (segmentCircle(points[i - 2], points[i - 1], points[i], points[i + 1], cx, cy, r) != null) return true;
        }

        return false;
    }

    public static double[] pointCircleNearest(double x, double y, double cx, double cy, double r)
    {
        double[] points = segmentCircle(x, y, cx, cy, cx, cy, r);
        return new double[] {points[0], points[1]};
    }

    public static double[] circleTan2Center(double cx, double cy, double r, double l1x1, double l1y1, double l1x2, double l1y2, double l2x1, double l2y1, double l2x2, double l2y2)
    {
        //Given a circle and 2 tangent lines, return the center point of a circle aligned to the tangent lines
        //The returned point is on the same side of each tangent line as the circle's original center point

        if ((l1x1 == l1x2 && l1y1 == l1y2) || (l2x1 == l2x2 && l2y1 == l2y2)) return null;

        double intersection[] = lineLine(l1x1, l1y1, l1x2, l1y2, l2x1, l2y1, l2x2, l2y2);

        if (intersection == null || Double.isNaN(intersection[0])) return null; //Parallel lines

        double dist = Math.sqrt(Math.pow(l1x1 - l1x2, 2) + Math.pow(l1y1 - l1y2, 2));
        double ratio = r / dist;
        double dx = (l1y2 - l1y1) * ratio;
        double dy = (l1x1 - l1x2) * ratio;
        double dir = pointLineSide(cx, cy, l1x1, l1y1, l1x2, l1y2, true);
        double oldX = l1x1, oldY = l1y1;

        l1x1 += dx;
        l1x2 += dx;
        l1y1 += dy;
        l1y2 += dy;
        if (dir == pointLineSide(oldX, oldY, l1x1, l1y1, l1x2, l1y2, true))
        {
            l1x1 -= dx * 2;
            l1x2 -= dx * 2;
            l1y1 -= dy * 2;
            l1y2 -= dy * 2;
        }

        dist = Math.sqrt(Math.pow(l2x1 - l2x2, 2) + Math.pow(l2y1 - l2y2, 2));
        ratio = r / dist;
        dx = (l2y2 - l2y1) * ratio;
        dy = (l2x1 - l2x2) * ratio;
        dir = pointLineSide(cx, cy, l2x1, l2y1, l2x2, l2y2, true);
        oldX = l2x1;
        oldY = l2y1;

        l2x1 += dx;
        l2x2 += dx;
        l2y1 += dy;
        l2y2 += dy;
        if (dir == pointLineSide(oldX, oldY, l2x1, l2y1, l2x2, l2y2, true))
        {
            l2x1 -= dx * 2;
            l2x2 -= dx * 2;
            l2y1 -= dy * 2;
            l2y2 -= dy * 2;
        }

        return lineLine(l1x1, l1y1, l1x2, l1y2, l2x1, l2y1, l2x2, l2y2);
    }

    public static double[] mirror(double x1, double y1, double x2, double y2, double points[])
    {
        //Given a line and a list of points, return the list of points mirrored over the line
        double nearest[];
        double result[] = new double[points.length];
        for(int i = 0; i < points.length; i += 2)
        {
            nearest = pointLineNearest(points[i], points[i + 1], x1, y1, x2, y2);
            result[i] = nearest[0] * 2 - points[i];
            result[i + 1] = nearest[1] * 2 - points[i + 1];
        }
        return result;
    }
}
