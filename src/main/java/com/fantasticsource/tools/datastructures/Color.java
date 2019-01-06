package com.fantasticsource.tools.datastructures;

import static com.fantasticsource.tools.Tools.max;
import static com.fantasticsource.tools.Tools.min;

public class Color
{
    private int intValue, r, g, b, a;
    private float rf, gf, bf, af;
    private String hex;


    public Color(int color)
    {
        setColor(color);
    }

    public Color(int r, int g, int b, int a)
    {
        setColor(r, g, b, a);
    }

    public Color(float r, float g, float b, float a)
    {
        setColor(r, g, b, a);
    }

    public Color(String hex)
    {
        setColor(hex);
    }

    public Color copy()
    {
        return new Color(intValue);
    }


    public Color setColor(int color)
    {
        intValue = color;

        r = (intValue >> 24) & 0xff;
        g = (intValue >> 16) & 0xff;
        b = (intValue >> 8) & 0xff;
        a = intValue & 0xff;

        rf = (float) r / 255;
        gf = (float) g / 255;
        bf = (float) b / 255;
        af = (float) a / 255;

        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setColor(int r, int g, int b, int a)
    {
        this.r = min(max(r, 0), 255);
        this.g = min(max(g, 0), 255);
        this.b = min(max(b, 0), 255);
        this.a = min(max(a, 0), 255);

        rf = (float) r / 255;
        gf = (float) g / 255;
        bf = (float) b / 255;
        af = (float) a / 255;

        intValue = (r << 24) | (g << 16) | (b << 8) | a;

        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setColor(float r, float g, float b, float a)
    {
        rf = min(max(r, 0), 1);
        gf = min(max(g, 0), 1);
        bf = min(max(b, 0), 1);
        af = min(max(a, 0), 1);

        this.r = min(max((int) rf * 255, 0), 255);
        this.g = min(max((int) gf * 255, 0), 255);
        this.b = min(max((int) bf * 255, 0), 255);
        this.a = min(max((int) af * 255, 0), 255);

        intValue = (this.r << 24) | (this.g << 16) | (this.b << 8) | this.a;

        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setColor(String hex)
    {
        this.hex = hex;

        intValue = Integer.parseInt(hex, 16);

        r = (intValue >> 24) & 0xff;
        g = (intValue >> 16) & 0xff;
        b = (intValue >> 8) & 0xff;
        a = intValue & 0xff;

        rf = (float) r / 255;
        gf = (float) g / 255;
        bf = (float) b / 255;
        af = (float) a / 255;

        return this;
    }


    public int getInt()
    {
        return intValue;
    }

    public int getR()
    {
        return r;
    }

    public int getG()
    {
        return g;
    }

    public int getB()
    {
        return b;
    }

    public int getA()
    {
        return a;
    }

    public float getRF()
    {
        return rf;
    }

    public float getGF()
    {
        return gf;
    }

    public float getBF()
    {
        return bf;
    }

    public float getAF()
    {
        return af;
    }

    public String getString()
    {
        return hex;
    }

    public String toString()
    {
        return getString();
    }
}
