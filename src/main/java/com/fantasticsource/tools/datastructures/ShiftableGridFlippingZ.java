package com.fantasticsource.tools.datastructures;

import static com.fantasticsource.tools.Tools.mod;

@SuppressWarnings({"UnusedParameters", "unused"})
public class ShiftableGridFlippingZ<T> extends ShiftableGrid<T>
{
    private ShiftableGridFlippingZ[] shiftableGrids = new ShiftableGridFlippingZ[2];

    private ShiftableGridFlippingZ(int width, int height, boolean flag)
    {
        super(width, height);
    }

    public ShiftableGridFlippingZ(int width, int height)
    {
        super(width, height);

        shiftableGrids[0] = this;
        shiftableGrids[1] = new ShiftableGridFlippingZ(width, height, true);
    }

    public void set(int x, int y, int z, T value)
    {
        //noinspection unchecked
        shiftableGrids[mod(z, 2)].set(x, y, value);
    }

    public T get(int x, int y, int z)
    {
        //noinspection unchecked
        return (T) shiftableGrids[mod(z, 2)].get(x, y);
    }

    public void clearBoth()
    {
        shiftableGrids[0].clear();
        shiftableGrids[1].clear();
    }

    public void shift(int x, int y)
    {
        shift(x, y, 0);
    }

    private void shift(int x, int y, boolean flag)
    {
        super.shift(x, y);
    }

    public void shift(int x, int y, int z)
    {
        shiftableGrids[0].shift(x, y, true);
        shiftableGrids[1].shift(x, y, true);

        if (mod(z, 2) == 1)
        {
            ShiftableGridFlippingZ sg = shiftableGrids[0];
            shiftableGrids[0] = shiftableGrids[1];
            shiftableGrids[1] = sg;
        }
    }

    private String toString(boolean flag)
    {
        return super.toString();
    }

    public String toString()
    {
        return shiftableGrids[0].toString(true) + "\r\n" + shiftableGrids[1].toString(true);
    }
}
