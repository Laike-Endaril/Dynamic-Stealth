package com.fantasticsource.tools.datastructures;

public class Pair<K, V>
{
    private K key;
    private V value;

    public Pair(K key, V value)
    {
        set(key, value);
    }

    public K getKey()
    {
        return key;
    }

    public Pair<K, V> setKey(K key)
    {
        this.key = key;
        return this;
    }

    public V getValue()
    {
        return value;
    }

    public Pair<K, V> setValue(V value)
    {
        this.value = value;
        return this;
    }

    public Pair<K, V> set(K key, V value)
    {
        setKey(key);
        setValue(value);
        return this;
    }

    public boolean equals(Object other)
    {
        if (!(other instanceof Pair)) return false;
        Pair otherPair = (Pair) other;
        return equal(key, otherPair.key) && equal(value, otherPair.value);
    }

    private boolean equal(Object obj1, Object obj2)
    {
        return obj1 == null ? obj2 == null : obj1.equals(obj2);
    }
}
