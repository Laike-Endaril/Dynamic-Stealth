package com.fantasticsource.tools;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import static com.fantasticsource.tools.Tools.*;

@SuppressWarnings("unused")
public class PNG
{
    //Only does RGBA for now

    private static int totalBuffers = 0;
    private static long totalBufferMemory = 0;

    private final InputStream input;
    private final byte[] buffer = new byte[4096];

    private int width, height, chunkBytesRemaining;
    private byte[] line, lastLine;
    private ByteBuffer directBuffer = null;
    private Inflater inflater = new Inflater();
    private boolean loaded = false;


    public PNG(String filename)
    {
        InputStream is = null;
        try
        {
            is = new FileInputStream(filename);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        input = is;

        try
        {
            //Read file header and check it
            read(buffer, 0, 8);
            if (bytesToInt(buffer, 0) != 0x89504E47 || bytesToInt(buffer, 4) != 0x0D0A1A0A)
            {
                throw new IOException("Not a PNG file (file header is not PNG file header)");
            }

            //Read first chunk header and make sure it is image header chunk header (headerception)
            read(buffer, 0, 8);
            if (bytesToInt(buffer, 0) != 13) throw new IOException("PNG has wrong image header length");
            if (!bytesToASCII(buffer, 4, 4).equals("IHDR"))
                throw new IOException("PNG file's first chunk was not image header");

            //Read image header chunk and check it
            read(buffer, 0, 13);
            width = bytesToInt(buffer, 0);
            height = bytesToInt(buffer, 4);
            if (buffer[8] != 8) throw new IllegalArgumentException("PNG does not have 8 bits of alpha");
            if (buffer[9] != 6)
                throw new IllegalArgumentException("PNG is not 32 bit (true color + alpha) color format");
            if (buffer[12] != 0) throw new IOException("PNG does not use standard interlacing");

            skip(4); //Skip CRC


            //Skip all non-image-data chunks
            read(buffer, 0, 8);
            while (!bytesToASCII(buffer, 4, 4).equals("IDAT"))
            {
                skip(bytesToInt(buffer, 0) + 4);
                read(buffer, 0, 8);
            }
            chunkBytesRemaining = bytesToInt(buffer, 0);


            //Read image data
            int lineSize = width * 4;

            directBuffer = allocateNative(height * lineSize);
            totalBuffers++;
            totalBufferMemory += height * lineSize;

            line = new byte[lineSize + 1];
            try
            {
                for (int y = 0; y < height; y++)
                {
                    readLine();
                    unfilter();

                    directBuffer.position(y * lineSize);
                    directBuffer.put(line, 1, lineSize);

                    lastLine = line;
                }
            }
            catch (DataFormatException e)
            {
                e.printStackTrace();
            }
            finally
            {
                inflater.end();
            }
            //noinspection ConstantConditions
            input.close();
            loaded = true;

            //Flip the buffer so it can be read correctly
            directBuffer.flip();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static int totalBuffersUsed()
    {
        return totalBuffers;
    }

    public static long totalBufferMemoryUsed()
    {
        return totalBufferMemory;
    }

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }

    public ByteBuffer getDirectBuffer() //It's already flipped
    {
        return directBuffer;
    }

    public void free() //Because direct byte buffers are not unloaded by the garbage collector!
    {
        if (loaded)
        {
            try
            {
                freeDirectByteBuffer(directBuffer);
                directBuffer = null;
                totalBuffers--;
                totalBufferMemory -= height * width * 4;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            loaded = false;
        }
    }

    public void finalize() throws Throwable //Because direct byte buffers are not unloaded by the garbage collector!
    {
        super.finalize();
        if (loaded) System.err.println("WARNING: PNG object was not freed manually!\r\nThis can cause some massive memory usage due to delayed freeing by garbage collector!");
        free();
    }


    private void readLine() throws IOException, DataFormatException
    {
        for (int bytesRead, position = 0; position < line.length; position += bytesRead)
        {
            bytesRead = inflater.inflate(line, position, line.length - position);

            if (bytesRead == 0)
            {
                if (inflater.finished()) throw new EOFException("PNG had EOF before all image data could be read");

                if (chunkBytesRemaining == 0) //Reached the end of IDAT chunk but not the end of current line; need next IDAT chunk
                {
                    skip(4); //Toss the CRC
                    read(line, 0, 8);
                    if (!bytesToASCII(line, 4, 4).equals("IDAT")) throw new IOException("PNG has less image data than header indicates");
                    chunkBytesRemaining = bytesToInt(line, 0);
                }
                inflater.setInput(buffer, 0, readChunkOrMax());
            }
        }
    }

    private int readChunkOrMax() throws IOException
    {
        int length = buffer.length;
        if (length > chunkBytesRemaining) length = chunkBytesRemaining;

        read(buffer, 0, length);

        chunkBytesRemaining -= length;
        return length;
    }


    private void unfilter() throws IOException
    {
        switch (line[0])
        {
            case 0:
                break;
            case 1:
                sub(line);
                break;
            case 2:
                up(line, lastLine);
                break;
            case 3:
                average(line, lastLine);
                break;
            case 4:
                paeth(line, lastLine);
                break;
            default:
                throw new IOException("Bad filter type: " + line[0]);
        }
    }

    private void sub(byte[] line)
    {
        for (int i = 5; i < line.length; i++) line[i] += line[i - 4];
    }

    private void up(byte[] line, byte[] lastLine)
    {
        for (int i = 1; i < line.length; i++) line[i] += lastLine[i];
    }

    private void average(byte[] line, byte[] lastLine)
    {
        int i;
        for (i = 1; i <= 4; i++)
        {
            line[i] += (byte) ((lastLine[i] & 0xFF) >>> 1);
        }
        for (; i < line.length; i++)
        {
            line[i] += (byte) ((lastLine[i] & 0xFF) + (line[i - 4] & 0xFF) >>> 1);
        }
    }

    private void paeth(byte[] line, byte[] lastLine)
    {
        int i;
        for (i = 1; i <= 4; i++) line[i] += lastLine[i];

        for (; i < line.length; i++)
        {
            int a = line[i - 4] & 0xFF;
            int b = lastLine[i] & 0xFF;
            int c = lastLine[i - 4] & 0xFF;

            int p = a + b - c;

            int pa = Math.abs(p - a);
            int pb = Math.abs(p - b);
            int pc = Math.abs(p - c);

            if (pa <= pb && pa <= pc) c = a;
            else if (pb <= pc) c = b;

            line[i] += (byte) c;
        }
    }


    private void read(byte[] buffer, int offset, int length) throws IOException
    {
        do
        {
            int bytesRead = input.read(buffer, offset, length);
            if (bytesRead < 0) throw new EOFException();
            offset += bytesRead;
            length -= bytesRead;
        }
        while (length > 0);
    }

    private void skip(int length) throws IOException
    {
        do
        {
            long bytesSkipped = input.skip(length);
            if (bytesSkipped < 0) throw new EOFException();
            length -= bytesSkipped;
        }
        while (length > 0);
    }
}
