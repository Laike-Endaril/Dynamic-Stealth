package com.fantasticsource.tools.datastructures;

import java.util.PriorityQueue;
import java.util.function.Predicate;

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

    public void removeIf(Predicate<Entry> predicate)
    {
        queue.removeIf(predicate);
    }

    public T peek()
    {
        Object result = queue.peek();
        return result == null ? null : ((Entry) result).object;
    }

    public double peekPriority()
    {
        Object result = queue.peek();
        return result == null ? Double.NaN : ((Entry) result).priority;
    }

    public T poll()
    {
        Object result = queue.poll();
        return result == null ? null : ((Entry) result).object;
    }

    public T[] toArray()
    {
        return (T[]) queue.toArray();
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

    public class Entry implements Comparable<Entry>
    {
        public T object;
        public double priority;

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
