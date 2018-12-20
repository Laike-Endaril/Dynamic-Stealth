package com.fantasticsource.tools.datastructures;

@SuppressWarnings("unused")
public class WrappingQueue<T>
{
    private Object[] array;
    private int insertPos = 0, startPos = 0, length = 0;

    public WrappingQueue(int size)
    {
        array = new Object[size];
    }

    public boolean add(T t) //Returns true if an entry was overwritten
    {
        array[insertPos] = t;

        insertPos++;
        if (insertPos == array.length) insertPos = 0;

        length++;
        if (length > array.length)
        {
            length = array.length;
            startPos++;
            if (startPos == array.length) startPos = 0;
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public T get(int index)
    {
        if (index >= length) throw new ArrayIndexOutOfBoundsException("Index: " + index + ", Length: " + length);

        index += startPos;
        if (index >= array.length) index -= array.length;
        return (T) array[index];
    }

    public T pop()
    {
        return remove(0);
    }
    @SuppressWarnings("unchecked")
    public T remove(int index)
    {
        if (index >= length) throw new ArrayIndexOutOfBoundsException("Index: " + index + ", Length: " + length);

        int i = (index + startPos) % array.length;
        T result = (T) array[i];
        for(; index >= 0; index--)
        {
            if (i == 0) array[0] = array[array.length - 1];
            else array[i] = array[i - 1];

            i--;
            if (i < 0) i = array.length - 1;
        }

        startPos++;
        if (startPos == array.length) startPos = 0;

        return result;
    }
    public boolean remove(T t) //Returns true if the object was removed, false if object was not found
    {
        int index = indexOf(t);
        if (index == -1) return false;
        remove(index);
        return true;
    }

    public Object[] getArray()
    {
        return getArray(0, length);
    }
    public Object[] getArray(int index, int length)
    {
        if (index + length > this.length) throw new ArrayIndexOutOfBoundsException("Index: " + index + length + ", Length: " + this.length);

        Object[] result = new Object[length];
        index = (index + startPos) % array.length;
        for(int i = 0; i < length; i++)
        {
            result[i] = array[index];

            index++;
            if (index == array.length) index = 0;
        }

        return result;
    }

    public int size()
    {
        return length;
    }

    public int indexOf(T t)
    {
        int index = startPos;
        for(int i = 0; i < length; i++)
        {
            if (array[index].equals(t)) return i;
            index++;
            if (index == array.length) index = 0;
        }
        return -1;
    }
    public boolean contains(T t)
    {
        return indexOf(t) != -1;
    }

    public void clear()
    {
        startPos = 0;
        length = 0;
        insertPos = 0;
    }
}
