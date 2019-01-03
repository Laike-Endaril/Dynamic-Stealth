package com.fantasticsource.tools;

import java.util.Date;

@SuppressWarnings("unused")
public class TrigLookupTable
{
    private double[] table, invtable;

    public TrigLookupTable(int granularity)
    {
        table = new double[granularity];
        invtable = new double[granularity];

        double step = Math.PI * 2 / (double) granularity;
        double invstep = ((double) 2) / ((double) granularity);

        double theta, val;
        for (int i = 0; i < granularity; i++)
        {
            theta = step * i; // 0 -> 2pi
            if (theta == Math.PI) table[i] = 0;
            else table[i] = Math.sin(theta);

            val = invstep * i - 1; // -1 -> +1
            invtable[i] = Math.asin(val);
        }
    }

    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    public static void test()
    {
        TrigLookupTable t = new TrigLookupTable(1024);
        Date d1, d2, d3;
        int i;
        double[] doubles = new double[1000000];

        System.out.println("All tests are 1000000 runs of each method\r\n");
        System.out.println("The TrigLookupTable has a granularity of 1024\r\n");

        d1 = new Date();
        for (i = 0; i < 1000000; i++)
        {
            doubles[i] = t.sin(Math.random() * (double) 2000000 - (double) 1000000);
        }
        d2 = new Date();
        for (i = 0; i < 1000000; i++)
        {
            doubles[i] = Math.sin(Math.random() * (double) 2000000 - (double) 1000000);
        }
        d3 = new Date();
        System.out.println("TrigLookupTable.sin = " + (d2.getTime() - d1.getTime()));
        System.out.println("Math.sin = " + (d3.getTime() - d2.getTime()) + "\r\n");

        d1 = new Date();
        for (i = 0; i < 1000000; i++)
        {
            doubles[i] = t.cos(Math.random() * (double) 2000000 - (double) 1000000);
        }
        d2 = new Date();
        for (i = 0; i < 1000000; i++)
        {
            doubles[i] = Math.cos(Math.random() * (double) 2000000 - (double) 1000000);
        }
        d3 = new Date();
        System.out.println("TrigLookupTable.cos = " + (d2.getTime() - d1.getTime()));
        System.out.println("Math.cos = " + (d3.getTime() - d2.getTime()) + "\r\n");

        d1 = new Date();
        for (i = 0; i < 1000000; i++)
        {
            doubles[i] = t.tan(Math.random() * (double) 2000000 - (double) 1000000);
        }
        d2 = new Date();
        for (i = 0; i < 1000000; i++)
        {
            doubles[i] = Math.tan(Math.random() * (double) 2000000 - (double) 1000000);
        }
        d3 = new Date();
        System.out.println("TrigLookupTable.tan = " + (d2.getTime() - d1.getTime()));
        System.out.println("Math.tan = " + (d3.getTime() - d2.getTime()) + "\r\n");

        d1 = new Date();
        for (i = 0; i < 1000000; i++)
        {
            doubles[i] = t.arcsin(Math.random() * (double) 2 - (double) 1);
        }
        d2 = new Date();
        for (i = 0; i < 1000000; i++)
        {
            doubles[i] = Math.asin(Math.random() * (double) 2 - (double) 1);
        }
        d3 = new Date();
        System.out.println("TrigLookupTable.arcsin = " + (d2.getTime() - d1.getTime()));
        System.out.println("Math.asin = " + (d3.getTime() - d2.getTime()) + "\r\n");

        d1 = new Date();
        for (i = 0; i < 1000000; i++)
        {
            doubles[i] = t.arccos(Math.random() * (double) 2 - (double) 1);
        }
        d2 = new Date();
        for (i = 0; i < 1000000; i++)
        {
            doubles[i] = Math.acos(Math.random() * (double) 2 - (double) 1);
        }
        d3 = new Date();
        System.out.println("TrigLookupTable.arccos = " + (d2.getTime() - d1.getTime()));
        System.out.println("Math.acos = " + (d3.getTime() - d2.getTime()) + "\r\n");

        d1 = new Date();
        for (i = 0; i < 1000000; i++)
        {
            doubles[i] = t.arctan(Math.random() * (double) 2000000 - (double) 1000000);
        }
        d2 = new Date();
        for (i = 0; i < 1000000; i++)
        {
            doubles[i] = Math.atan(Math.random() * (double) 2000000 - (double) 1000000);
        }
        d3 = new Date();
        System.out.println("TrigLookupTable.arctan = " + (d2.getTime() - d1.getTime()));
        System.out.println("Math.atan = " + (d3.getTime() - d2.getTime()) + "\r\n");
    }

    public double sin(double theta)
    {
        theta = Tools.mod(theta, Math.PI * 2);

        int i = (int) Math.round(table.length * theta / (Math.PI * 2));
        if (i == table.length) i = 0;
        return table[i];
    }

    public double cos(double theta)
    {
        return sin(theta + Math.PI / 2);
    }

    public double tan(double theta)
    {
        return sin(theta) / cos(theta);
    }

    public double arcsin(double input)
    {
        if (input < -1 || input > 1) throw new IndexOutOfBoundsException("arcsin() and arccos() can only take in from -1 to 1.  Input was " + input);

        int i = (int) Math.round(invtable.length * (input + 1) / 2);
        if (i == invtable.length) return Math.PI / 2;
        return invtable[i];
    }

    public double arccos(double input)
    {
        return Math.PI / 2 - arcsin(input);
    }

    public double arctan(double input)
    {
        if (input == 0) return 0;
        if (input == Double.POSITIVE_INFINITY) return Math.PI / 2;
        if (input == Double.NEGATIVE_INFINITY) return -Math.PI / 2;
        if (input > 0) //Quad 1
        {
            if (input < 1) return arcsin(input / Math.sqrt(1 + input * input));
            else
            {
                double x = 1 / input;
                return arccos(x / Math.sqrt(1 + x * x));
            }
        }
        else //Quad 4
        {
            if (input > -1) return arcsin(input / Math.sqrt(1 + input * input));
            else
            {
                double x = 1 / input;
                return arccos(x / Math.sqrt(1 + x * x)) - Math.PI;
            }
        }
    }

    public double arctanFullcircle(double x1, double y1, double x2, double y2)
    {
        double x = x2 - x1, y = y1 - y2;

        if (x > 0)
        {
            if (y > 0) //Quad 1
            {
                if (x > y) return arcsin(y / Math.sqrt(x * x + y * y));
                else return (arccos(x / Math.sqrt(x * x + y * y)));
            }
            else if (y < 0) //Quad 4
            {
                if (x > -y) return Math.PI * 2 + arcsin(y / Math.sqrt(x * x + y * y));
                else return Math.PI * 2 - arccos(x / Math.sqrt(x * x + y * y));
            }
            else return 0;
        }
        else if (x < 0)
        {
            if (y > 0) //Quad 2
            {
                if (-x > y) return Math.PI - arcsin(y / Math.sqrt(x * x + y * y));
                else return arccos(x / Math.sqrt(x * x + y * y));
            }
            else if (y < 0) //Quad 3
            {
                if (x < y) return Math.PI - arcsin(y / Math.sqrt(x * x + y * y));
                else return Math.PI * 2 - arccos(x / Math.sqrt(x * x + y * y));
            }
            else return Math.PI;
        }
        else
        {
            if (y > 0) return Math.PI / 2;
            else if (y < 0) return Math.PI * 3 / 2;
            else return 0;
        }
    }

    public String toString()
    {
        StringBuilder str = new StringBuilder("" + table[0]);
        for (int i = 1; i < table.length; i++)
        {
            str.append("\r\n").append(table[i]);
        }
        return str.toString();
    }

    public double[] getArray()
    {
        return table;
    }

    public double[] getInvArray()
    {
        return invtable;
    }

    public int getGranularity()
    {
        return table.length;
    }
}
