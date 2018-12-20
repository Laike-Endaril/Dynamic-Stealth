package com.fantasticsource.tools.datastructures;

import java.util.PriorityQueue;

public class ExplicitPriorityQueue<T>
{
    private PriorityQueue<Entry> queue;

    public ExplicitPriorityQueue(int initialSize)
    {
        queue = new PriorityQueue<>(Math.max(1, initialSize));
    }

    public ExplicitPriorityQueue()
    {
        this(11);
    }

    public void add(T object, double priority)
    {
        queue.add(new Entry(object, priority));
    }

    public T peek()
    {
        Object result = queue.peek();
        return result == null ? null : (T) ((Entry) result).object;
    }

    public T poll()
    {
        Object result = queue.poll();
        return result == null ? null : (T) ((Entry) result).object;
    }

    public void clear()
    {
        queue.clear();
    }

    public int size()
    {
        return queue.size();
    }

    public boolean isEmpty()
    {
        return queue.isEmpty();
    }

    private class Entry implements Comparable<Entry>
    {
        T object;
        double priority;

        Entry(T object, double priority)
        {
            this.object = object;
            this.priority = priority;
        }

        @Override
        public int compareTo(Entry o)
        {
            return Double.compare(priority, o.priority);
        }
    }
}
