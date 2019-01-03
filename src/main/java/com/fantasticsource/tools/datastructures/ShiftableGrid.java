package com.fantasticsource.tools.datastructures;

import static com.fantasticsource.tools.Tools.mod;

@SuppressWarnings("unused")
public class ShiftableGrid<T>
{
    private Object[][] values;
    private int xOffset = 0, yOffset = 0, w, h;

    public ShiftableGrid(int width, int height)
    {
        if (width < 0 || height < 0) throw new IllegalArgumentException("Width and height must both be >= 0. Given values: w = " + width + ", h = " + height);

        w = width;
        h = height;
        values = new Object[w][h];
    }

    public void set(int x, int y, T value)
    {
        //This IS the mod you're looking for...but it's certainly not the same as %, so check out the mod function itself in Tools object
        values[mod(x + xOffset, w)][mod(y + yOffset, h)] = value;
    }

    public T get(int x, int y)
    {
        //noinspection unchecked
        return (T) values[mod(x + xOffset, w)][mod(y + yOffset, h)];
    }

    public void clear()
    {
        values = new Object[w][h];
    }

    public void shift(int x, int y)
    {
        if (x < 0)
        {
            for (int ix = 0; ix > x && ix > -w; ix--)
            {
                clearColumn(xOffset);

                xOffset++;
                if (xOffset == w) xOffset = 0;
            }
        }
        else
        {
            for (int ix = 0; ix < x && ix < w; ix++)
            {
                if (xOffset == 0) xOffset = w;
                xOffset--;

                clearColumn(xOffset);
            }
        }

        if (y < 0)
        {
            for (int iy = 0; iy > y && iy > -h; iy--)
            {
                clearRow(yOffset);

                yOffset++;
                if (yOffset == h) yOffset = 0;
            }
        }
        else
        {
            for (int iy = 0; iy < y && iy < h; iy++)
            {
                if (yOffset == 0) yOffset = h;
                yOffset--;

                clearRow(yOffset);
            }
        }
    }

    private void clearRow(int y)
    {
        for (int i = 0; i < h; i++) values[i][y] = null;
    }

    private void clearColumn(int x)
    {
        for (int i = 0; i < h; i++) values[x][i] = null;
    }

    public String toString(int x, int y)
    {
        T t = get(x, y);
        if (t == null) return "";
        return t.toString();
    }

    public String toString()
    {
        if (w == 0 || h == 0) return "Grid can't hold anything (width = " + w + ", height = " + h + ")";

        StringBuilder result = new StringBuilder();

        for (int iy = 0; iy < h; iy++)
        {
            result.append(toString(0, iy));

            for (int ix = 1; ix < w; ix++)
            {
                result.append(", ").append(toString(ix, iy));
            }
            result.append("\r\n");
        }

        return result.toString();
    }

    public void print()
    {
        System.out.println(toString());
    }
}
