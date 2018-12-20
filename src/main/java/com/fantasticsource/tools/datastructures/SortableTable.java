package com.fantasticsource.tools.datastructures;

@SuppressWarnings("unused")
public class SortableTable
{
    public static final int INITIAL_SIZE = 16;

    private Column[] columns;
    private int used = 0, sortedColumn = -1;
    private boolean ascending = true;

    public SortableTable(Class... columns) //Eg. new SortableTable(Double.class, String.class, SomethingElse.class)
    {
        if (columns.length == 0) throw new IllegalArgumentException("Must have at least 1 column, eg. new SortableTable(Integer.class)");

        this.columns = new Column[columns.length];
        for(int i = 0; i < columns.length; i++)
        {
            this.columns[i] = new Column(columns[i]);
        }
    }

    public void add(Object... item)
    {
        if (item.length != columns.length) throw new IllegalArgumentException("Item length (" + item.length + ") must match number of columns (" + columns.length + ")");
        for(int i = 0; i < item.length; i++)
        {
            //noinspection unchecked
            if (!columns[i].c.isAssignableFrom(item[i].getClass()))
            {
                throw new IllegalArgumentException("All item objects' classes must match column classes. Column class match error on column " + i);
            }
        }

        if (used == columns[0].values.length) expand();
        for(int i = 0; i < columns.length; i++) columns[i].values[used] = item[i];

        if (sortedColumn != -1)
        {
            if (ascending)
            {
                for(int i = used; i > 0; i--)
                {
                    if (columns[sortedColumn].greater(i - 1, i)) swap(i - 1, i);
                    else break;
                }
            }
            else
            {
                for(int i = used; i > 0; i--)
                {
                    if (columns[sortedColumn].greater(i, i - 1)) swap(i - 1, i);
                    else break;
                }
            }
        }

        used++;
    }

    public void set(int index, Object... item)
    {
        if (index >= used) throw new ArrayIndexOutOfBoundsException("Items: " + used + ", index given: " + index);
        if (item.length != columns.length) throw new IllegalArgumentException("Item length (" + item.length + ") must match number of columns (" + columns.length + ")");
        for(int i = 0; i < item.length; i++)
        {
            //noinspection unchecked
            if (!columns[i].c.isAssignableFrom(item[i].getClass()))
            {
                throw new IllegalArgumentException("All item objects' classes must match column classes. Column class match error on column " + i);
            }
        }

        for(int i = 0; i < columns.length; i++) columns[i].values[index] = item[i];

        if (sortedColumn != -1)
        {
            if (ascending)
            {
                int i = index;
                for(; i > 0; i--)
                {
                    if (columns[sortedColumn].greater(i - 1, i)) swap(i - 1, i);
                    else break;
                }
                for(; i < used - 1; i++)
                {
                    if (columns[sortedColumn].greater(i, i + 1)) swap(i, i + 1);
                    else break;
                }
            }
            else
            {
                int i = index;
                for(; i > 0; i--)
                {
                    if (columns[sortedColumn].greater(i, i - 1)) swap(i - 1, i);
                    else break;
                }
                for(; i < used - 1; i++)
                {
                    if (columns[sortedColumn].greater(i + 1, i)) swap(i, i + 1);
                    else break;
                }
            }
        }
    }

    public void delete(int index)
    {
        if (index >= used) throw new ArrayIndexOutOfBoundsException("Item count: " + used + ", index given: " + index);

        used--;
        for(int i = index; i < used; i++)
        {
            for (Column column : columns)
            {
                column.values[i] = column.values[i + 1];
            }
        }
    }
    public boolean delete(Object o, int column)
    {
        if (column >= columns.length) throw new ArrayIndexOutOfBoundsException("Column count: " + columns.length + ", index given: " + column);

        int i = columns[column].indexOf(o);
        if (i == -1) return false;
        delete(i);
        return true;
    }

    public void clear()
    {
        used = 0;
    }

    public void startSorting(int column)
    {
        startSorting(column, ascending);
    }
    public void startSorting(int column, boolean ascending)
    {
        if (column >= columns.length) throw new ArrayIndexOutOfBoundsException("Column count: " + columns.length + ", index given: " + column);

        this.ascending = ascending;
        this.sortedColumn = column;

        if (ascending)
        {
            for(int i2 = used - 1; i2 > 0; i2--)
            {
                for(int i = 0; i < i2; i++)
                {
                    if (columns[column].greater(i, i + 1)) swap(i, i + 1);
                }
            }
        }
        else
        {
            for(int i2 = used - 1; i2 > 0; i2--)
            {
                for(int i = 0; i < i2; i++)
                {
                    if (columns[column].greater(i + 1, i)) swap(i, i + 1);
                }
            }
        }
    }

    public void swap(int index1, int index2)
    {
        Object o;
        for (Column column : columns)
        {
            o = column.values[index1];
            column.values[index1] = column.values[index2];
            column.values[index2] = o;
        }
    }

    public void stopSorting()
    {
        this.sortedColumn = -1;
    }

    public int indexOf(Object value, int column)
    {
        return columns[column].indexOf(value);
    }

    public boolean contains(Object value, int column)
    {
        return columns[column].contains(value);
    }

    private void expand()
    {
        int newSize = columns[0].values.length * 2;
        Object[] objects = new Object[newSize];

        for (Column column : columns)
        {
            System.arraycopy(column.values, 0, objects, 0, used);
            column.values = objects.clone();
        }
    }

    public void label(int column, String label)
    {
        if (column >= columns.length) throw new ArrayIndexOutOfBoundsException("Columns: " + columns.length + ", index given: " + column);
        columns[column].label = label;
    }

    public String toString()
    {
        String[] strings = new String[columns.length * (1 + used)];
        int[] maxes = new int[columns.length];

        for(int i = 0; i < columns.length; i++)
        {
            strings[i] = columns[i].label;
            maxes[i] = strings[i].length();
        }
        for(int i2 = 0; i2 < used; i2++)
        {
            for(int i = 0; i < columns.length; i++)
            {
                strings[(1 + i2) * columns.length + i] = columns[i].toString(i2);
                if (strings[(1 + i2) * columns.length + i].length() > maxes[i]) maxes[i] = strings[(1 + i2) * columns.length + i].length();
            }
        }

        StringBuilder result = new StringBuilder();
        for(int i = 0; i < columns.length; i++)
        {
            result.append(strings[i]);
            for(int i3 = strings[i].length(); i3 < maxes[i] + 1; i3++) result.append(" ");
        }
        result.append("\r\n");
        for(int i2 = 0; i2 < used; i2++)
        {
            for(int i = 0; i < columns.length; i++)
            {
                result.append(strings[(1 + i2) * columns.length + i]);
                for(int i3 = strings[(1 + i2) * columns.length + i].length(); i3 < maxes[i] + 1; i3++) result.append(" ");
            }
            result.append("\r\n");
        }
        return result.toString();
    }

    public void print()
    {
        System.out.print(toString());
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private class Column
    {
        private static final byte
                BYTE = 0,
                SHORT = 1,
                INT = 2,
                LONG = 3,
                FLOAT = 4,
                DOUBLE = 5,
                BOOLEAN = 6,
                CHAR = 7,
                STRING = 8,
                CLASS = 9;

        byte comparison;
        String label;
        Object[] values = new Object[INITIAL_SIZE];
        Class c;

        Column(Class c)
        {
            this("", c);
        }
        Column(String label, Class c)
        {
            this.label = label;
            this.c = c;

            if (Integer.class.isAssignableFrom(c))
            {
                comparison = INT;
            }
            else if (Boolean.class.isAssignableFrom(c))
            {
                comparison = BOOLEAN;
            }
            else if (String.class.isAssignableFrom(c))
            {
                comparison = STRING;
            }
            else if (Float.class.isAssignableFrom(c))
            {
                comparison = FLOAT;
            }
            else if (Character.class.isAssignableFrom(c))
            {
                comparison = CHAR;
            }
            else if (Double.class.isAssignableFrom(c))
            {
                comparison = DOUBLE;
            }
            else if (Short.class.isAssignableFrom(c))
            {
                comparison = SHORT;
            }
            else if (Long.class.isAssignableFrom(c))
            {
                comparison = LONG;
            }
            else if (Byte.class.isAssignableFrom(c));
            else //Everything else; not a primitive or a String
            {
                comparison = CLASS;
            }
        }

        boolean greater(int index1, int index2) //True if index1 > index2
        {
            switch(comparison)
            {
                case INT: return ((int) values[index1]) > ((int) values[index2]);
                case BOOLEAN: return ((boolean) values[index1]) && !((boolean) values[index2]);
                case STRING:
                {
                    String str1 = (String) values[index1], str2 = (String) values[index2];
                    int i = 0, min = Math.min(str1.length(), str2.length());
                    char c1, c2;
                    while(i < min)
                    {
                        if (str1.length() > i) c1 = str1.charAt(i);
                        else c1 = 0;
                        if (str2.length() > i) c2 = str2.charAt(i);
                        else c2 = 0;

                        if (c1 > c2) return true;
                        if (c1 < c2) return false;
                        i++;
                    }
                    return str1.length() > str2.length();
                }
                case FLOAT: return ((float) values[index1]) > ((float) values[index2]);
                case CHAR: return ((char) values[index1]) > ((char) values[index2]);
                case DOUBLE: return ((double) values[index1]) > ((double) values[index2]);
                case SHORT: return ((short) values[index1]) > ((short) values[index2]);
                case LONG: return ((long) values[index1]) > ((long) values[index2]);
                case BYTE: return ((byte) values[index1]) > ((byte) values[index2]);
                default:
                {
                    String str1 = values[index1].getClass().getSimpleName(), str2 = values[index2].getClass().getSimpleName();
                    int i = 0, min = Math.min(str1.length(), str2.length());
                    char c1, c2;
                    while(i < min)
                    {
                        if (str1.length() > i) c1 = str1.charAt(i);
                        else c1 = 0;
                        if (str2.length() > i) c2 = str2.charAt(i);
                        else c2 = 0;

                        if (c1 > c2) return true;
                        if (c1 < c2) return false;
                        i++;
                    }
                    return str1.length() > str2.length();
                }
            }
        }

        int indexOf(Object value)
        {
            for(int i = 0; i < used; i++)
            {
                if (value.equals(values[i])) return i;
            }
            return -1;
        }

        boolean contains(Object value)
        {
            return indexOf(value) != -1;
        }

        String toString(int index)
        {
            if (comparison != CLASS) return "" + values[index];
            return values[index].getClass().getSimpleName();
        }
    }

    public static void test()
    {
        SortableTable st = new SortableTable(String.class, Integer.class);

        st.label(0, "Name");
        st.label(1, "Age");

        st.add("Kyle", 27);
        st.add("Charlie", 19);
        st.add("Baby", 0);
        st.add("Joe", 32);
        st.add("Grandpa", 90);

        System.out.println("Original table...");
        st.print();

        System.out.println();
        st.startSorting(1);
        System.out.println("Sorted by age (ascending)...");
        st.print();

        System.out.println();
        st.add("Gerry", 26);
        System.out.println("Added entry; table automatically puts it in right spot...");
        st.print();

        System.out.println();
        st.startSorting(0, false);
        System.out.println("Sorted by name (descending)...");
        st.print();

        System.out.println();
        st.set(st.indexOf("Grandpa", 0), "Artemis", 90);
        System.out.println("Changed 'Grandpa' to 'Artemis'; table automatically resorts just that one entry...");
        st.print();
    }
}
