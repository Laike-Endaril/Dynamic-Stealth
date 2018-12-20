package com.fantasticsource.tools;

@SuppressWarnings("unused")
public class Matrices
{
    public static float[] mult(float[]... matrices)
    {
        if (matrices[0].length != 16) throw new IllegalArgumentException("All matrices must be of length 16 (4x4 matrices; argument 0 was not)");

        float[] result = matrices[0].clone();
        for(int i = 1; i < matrices.length; i++)
        {
            if (matrices[i].length != 16) throw new IllegalArgumentException("All matrices must be of length 16 (4x4 matrices; argument " + i + " was not)");

            result[0] = result[0] * matrices[i][0] + result[4] * matrices[i][1] + result[8] * matrices[i][2] + result[12] * matrices[i][3];
            result[1] = result[1] * matrices[i][0] + result[5] * matrices[i][1] + result[9] * matrices[i][2] + result[13] * matrices[i][3];
            result[2] = result[2] * matrices[i][0] + result[6] * matrices[i][1] + result[10] * matrices[i][2] + result[14] * matrices[i][3];
            result[3] = result[3] * matrices[i][0] + result[7] * matrices[i][1] + result[11] * matrices[i][2] + result[15] * matrices[i][3];

            result[4] = result[0] * matrices[i][4] + result[4] * matrices[i][5] + result[8] * matrices[i][6] + result[12] * matrices[i][7];
            result[5] = result[1] * matrices[i][4] + result[5] * matrices[i][5] + result[9] * matrices[i][6] + result[13] * matrices[i][7];
            result[6] = result[2] * matrices[i][4] + result[6] * matrices[i][5] + result[10] * matrices[i][6] + result[14] * matrices[i][7];
            result[7] = result[3] * matrices[i][4] + result[7] * matrices[i][5] + result[11] * matrices[i][6] + result[15] * matrices[i][7];

            result[8] = result[0] * matrices[i][8] + result[4] * matrices[i][9] + result[8] * matrices[i][10] + result[12] * matrices[i][11];
            result[9] = result[1] * matrices[i][8] + result[5] * matrices[i][9] + result[9] * matrices[i][10] + result[13] * matrices[i][11];
            result[10] = result[2] * matrices[i][8] + result[6] * matrices[i][9] + result[10] * matrices[i][10] + result[14] * matrices[i][11];
            result[11] = result[3] * matrices[i][8] + result[7] * matrices[i][9] + result[11] * matrices[i][10] + result[15] * matrices[i][11];

            result[12] = result[0] * matrices[i][12] + result[4] * matrices[i][13] + result[8] * matrices[i][14] + result[12] * matrices[i][15];
            result[13] = result[1] * matrices[i][12] + result[5] * matrices[i][13] + result[9] * matrices[i][14] + result[13] * matrices[i][15];
            result[14] = result[2] * matrices[i][12] + result[6] * matrices[i][13] + result[10] * matrices[i][14] + result[14] * matrices[i][15];
            result[15] = result[3] * matrices[i][12] + result[7] * matrices[i][13] + result[11] * matrices[i][14] + result[15] * matrices[i][15];
        }
        return result;
    }

    public static float[] translate(float[] matrix, float x, float y, float z)
    {
        return mult(matrix, translate(x, y, z));
    }
    public static float[] translate(float x, float y, float z)
    {
        return new float[]{
                1,
                0,
                0,
                0,

                0,
                1,
                0,
                0,

                0,
                0,
                1,
                0,

                x,
                y,
                z,
                1
        };
    }

    public static float[] scale(float[] matrix, float x, float y, float z)
    {
        return mult(matrix, scale(x, y, z));
    }
    public static float[] scale(float x, float y, float z)
    {
        return new float[]{
                x,
                0,
                0,
                0,

                0,
                y,
                0,
                0,

                0,
                0,
                z,
                0,

                0,
                0,
                0,
                1
        };
    }

    public static float[] frustumBetter(float left, float right, float top, float bottom, float zStart, float zDepth, float xScale, float yScale, float zScale)
    {
        //I suggest calling this with the following arguments and then tweaking as needed...
        //-Display.getWidth / 2
        //Display.getWidth / 2
        //-Display.getHeight / 2
        //Display.getHeight / 2
        //zStart                    (the minimum z coordinates you want to be able to see; can be +, -, or 0; basically zNear)
        //zDepth                    (how far PAST zStart you want to be able to see; equal to zFar - zNear)
        //1                         (Making this further from 0 stretches things out, and making it closer to 0 smashes things together)

        //Formula I came up with for default z scaling...aka MAGIC!
        zScale *= -.0002f * Math.min(Math.abs(left - right), Math.abs(top - bottom));

        final int STATIC_ZNEAR = 100; //Used as the actual glFrustum's zNear for better z buffering

        if (zDepth <= 0) zDepth = .000001f; //If depth is 0, give it just enough to draw what's at zstart

        return scale(
                translate(
                        frustum(left, right, top, bottom, STATIC_ZNEAR, STATIC_ZNEAR + zDepth),
                        0, 0, zStart - STATIC_ZNEAR),
                xScale, yScale, zScale);
    }

    public static float[] frustum(float left, float right, float top, float bottom, float zNear, float zFar)
    {
        return new float[]{
                2 * zNear / (right - left),
                0,
                0,
                0,

                0,
                2 * zNear / (top - bottom),
                0,
                0,

                (right + left) / (right - left),
                (top + bottom) / (top - bottom),
                -(zFar + zNear) / (zFar - zNear),
                -1,

                0,
                0,
                -2 * zFar * zNear / (zFar - zNear),
                0
        };
    }
}
