package com.fantasticsource.tools;

import sun.misc.Cleaner;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

@SuppressWarnings("unused")
public class Tools
{
    public static int min(int... values)
    {
        int result = Integer.MAX_VALUE;
        for (int value : values)
        {
            if (value < result) result = value;
        }
        return result;
    }

    public static float min(float... values)
    {
        float result = Float.MAX_VALUE;
        for (float value : values)
        {
            if (value < result) result = value;
        }
        return result;
    }

    public static double min(double... values)
    {
        double result = Double.MAX_VALUE;
        for (double value : values)
        {
            if (value < result) result = value;
        }
        return result;
    }

    public static int max(int... values)
    {
        int result = Integer.MIN_VALUE;
        for (int value : values)
        {
            if (value > result) result = value;
        }
        return result;
    }

    public static float max(float... values)
    {
        float result = Float.MIN_VALUE;
        for (float value : values)
        {
            if (value > result) result = value;
        }
        return result;
    }

    public static double max(double... values)
    {
        double result = Double.MIN_VALUE;
        for (double value : values)
        {
            if (value > result) result = value;
        }
        return result;
    }


    public static void printStackTrace(Thread thread, int maxNodes)
    {
        StackTraceElement[] stack = thread.getStackTrace();
        for (int i = 0; i < maxNodes && i < stack.length; i++)
        {
            System.out.println(stack[i].toString());
        }
    }

    public static void printStackTrace(Thread thread)
    {
        printStackTrace(thread, Integer.MAX_VALUE);
    }

    public static void printStackTrace()
    {
        printStackTrace(Thread.currentThread());
    }

    public static boolean stackContainsSubstring(Thread thread, String subString)
    {
        subString = subString.toLowerCase();

        StackTraceElement[] stack = thread.getStackTrace();
        for (StackTraceElement element : stack)
        {
            if (element.getClassName().toLowerCase().contains(subString)) return true;
        }
        return false;
    }

    public static boolean stackContainsSubstring(String substring)
    {
        return stackContainsSubstring(Thread.currentThread(), substring);
    }


    public static byte choose(byte... choices)
    {
        return choices[(int) Math.floor(Math.random() * choices.length)];
    }

    public static short choose(short... choices)
    {
        return choices[(int) Math.floor(Math.random() * choices.length)];
    }

    public static int choose(int... choices)
    {
        return choices[(int) Math.floor(Math.random() * choices.length)];
    }

    public static long choose(long... choices)
    {
        return choices[(int) Math.floor(Math.random() * choices.length)];
    }

    public static float choose(float... choices)
    {
        return choices[(int) Math.floor(Math.random() * choices.length)];
    }

    public static double choose(double... choices)
    {
        return choices[(int) Math.floor(Math.random() * choices.length)];
    }

    public static boolean choose(boolean... choices)
    {
        return choices[(int) Math.floor(Math.random() * choices.length)];
    }

    public static char choose(char... choices)
    {
        return choices[(int) Math.floor(Math.random() * choices.length)];
    }

    public static String choose(String... choices)
    {
        return choices[(int) Math.floor(Math.random() * choices.length)];
    }

    @SuppressWarnings("unchecked")
    public static <E> E chooseObj(E... choices)
    {
        return choices[(int) Math.floor(Math.random() * choices.length)];
    }


    public static void insertBytes(byte[] bytes, int index, short value)
    {
        bytes[index] = (byte) (value >> 8);
        bytes[index + 1] = (byte) value;
    }

    public static void insertBytes(byte[] bytes, int index, char value)
    {
        bytes[index] = (byte) (value >> 8);
        bytes[index + 1] = (byte) value;
    }

    public static void insertBytes(byte[] bytes, int index, int value)
    {
        bytes[index] = (byte) (value >> 24);
        bytes[index + 1] = (byte) (value >> 16);
        bytes[index + 2] = (byte) (value >> 8);
        bytes[index + 3] = (byte) value;
    }

    public static void insertBytes(byte[] bytes, int index, float value)
    {
        insertBytes(bytes, index, Float.floatToRawIntBits(value));
    }

    public static void insertBytes(byte[] bytes, int index, long value)
    {
        bytes[index] = (byte) (value >> 56);
        bytes[index + 1] = (byte) (value >> 48);
        bytes[index + 2] = (byte) (value >> 40);
        bytes[index + 3] = (byte) (value >> 32);
        bytes[index + 4] = (byte) (value >> 24);
        bytes[index + 5] = (byte) (value >> 16);
        bytes[index + 6] = (byte) (value >> 8);
        bytes[index + 7] = (byte) value;
    }

    public static void insertBytes(byte[] bytes, int index, double value)
    {
        insertBytes(bytes, index, Double.doubleToRawLongBits(value));
    }


    public static byte[] mergeByteArrays(byte[] dest, byte[] src)
    {
        byte[] temp = new byte[dest.length + src.length];
        int i = 0;
        for (; i < dest.length; i++) temp[i] = dest[i];
        for (int i2 = i; i < temp.length; i++) temp[i] = src[i - i2];

        return temp;
    }

    public static byte[] subArray(byte[] bytes, int start)
    {
        return subArray(bytes, start, bytes.length - start);
    }

    public static byte[] subArray(byte[] bytes, int start, int length)
    {
        byte[] result = new byte[length];
        System.arraycopy(bytes, start, result, 0, length);
        return result;
    }

    public static char[] subArray(char[] chars, int start)
    {
        return subArray(chars, start, chars.length - start);
    }

    public static char[] subArray(char[] chars, int start, int length)
    {
        char[] result = new char[length];
        System.arraycopy(chars, start, result, 0, length);
        return result;
    }


    public static short bytesToShort(byte[] b, int index)
    {
        return (short) (((b[index] & 0xFF) << 8) | (b[index + 1] & 0xFF));
    }

    public static char bytesToChar(byte[] b, int index)
    {
        return (char) (((b[index] & 0xFF) << 8) | (b[index + 1] & 0xFF));
    }

    public static int bytesToInt(byte[] b, int index)
    {
        return (((b[index] & 0xFF) << 24) | ((b[index + 1] & 0xFF) << 16) | ((b[index + 2] & 0xFF) << 8) | (b[index + 3] & 0xFF));
    }

    public static float bytesToFloat(byte[] b, int index)
    {
        return Float.intBitsToFloat(bytesToInt(b, index));
    }

    public static String bytesToASCII(byte[] b, int index, int length)
    {
        String result = "";
        for (int i = 0; i < length; i++) result += (char) b[index + i];
        return result;
    }

    public static double degtorad(double deg)
    {
        return deg * Math.PI / 180;
    }

    public static double radtodeg(double rad)
    {
        return rad * 180 / Math.PI;
    }

    public static int mod(int a, int b)
    {
        a = a % b;
        if (a < 0) a += b;
        return a;
    }

    public static float mod(float a, float b)
    {
        a = a % b;
        if (a < 0) a += b;
        return a;
    }

    public static double mod(double a, double b)
    {
        a = a % b;
        if (a < 0) a += b;
        return a;
    }

    public static byte random(byte maxvalue)
    {
        return (byte) (maxvalue * Math.random());
    }

    public static short random(short maxvalue)
    {
        return (short) (maxvalue * Math.random());
    }

    public static int random(int maxvalue)
    {
        return (int) (maxvalue * Math.random());
    }

    public static long random(long maxvalue)
    {
        return (long) (maxvalue * Math.random());
    }

    public static float random(float maxvalue)
    {
        return (float) (maxvalue * Math.random());
    }

    public static double random(double maxvalue)
    {
        return maxvalue * Math.random();
    }

    public static char random(char maxvalue)
    {
        return (char) (maxvalue * Math.random());
    }

    public static double randomGaussian(Random random, double center, double range)
    {
        double result = random.nextGaussian() * .5;
        while (result < -1 || result > 1) result = random.nextGaussian() * .5;
        return center + range * result;
    }

    public static double distance(double x1, double y1, double x2, double y2)
    {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }


    public static void freeDirectByteBuffer(ByteBuffer directBuffer) throws NoSuchFieldException, IllegalAccessException
    {
        Field field = directBuffer.getClass().getDeclaredField("cleaner");
        field.setAccessible(true);
        Cleaner cleaner = (Cleaner) field.get(directBuffer);
        cleaner.clean();
    }


    public static String readTXT(String filename) throws IOException
    {
        return readTXT(filename, false, null);
    }

    public static String readTXT(String filename, boolean internal, Class calledFrom) throws IOException
    {
        InputStream in;
        if (internal) in = calledFrom.getResourceAsStream(filename);
        else in = new FileInputStream(filename);

        String result = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

        String line;
        while ((line = reader.readLine()) != null) result += line + "\r\n";

        reader.close();
        in.close();

        return result;
    }


    public static ByteBuffer allocateNative(int bytes)
    {
        return ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder());
    }
}
