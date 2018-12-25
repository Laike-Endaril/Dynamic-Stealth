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

    public V getValue()
    {
        return value;
    }

    public Pair<K, V> setKey(K key)
    {
        this.key = key;
        return this;
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
}
