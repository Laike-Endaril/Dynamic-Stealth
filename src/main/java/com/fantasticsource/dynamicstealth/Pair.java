package com.fantasticsource.dynamicstealth;

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

    public V getValue()
    {
        return value;
    }

    public Pair setKey(K key)
    {
        this.key = key;
        return this;
    }

    public Pair setValue(V value)
    {
        this.value = value;
        return this;
    }

    public Pair set(K key, V value)
    {
        setKey(key);
        setValue(value);
        return this;
    }
}
