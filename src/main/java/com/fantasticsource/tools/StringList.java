package com.fantasticsource.tools;

import java.io.*;
import java.util.ArrayList;

public class StringList
{
    private String filename;
    private ArrayList<String> strings = new ArrayList<>(10);

    public StringList(String filename)
    {
        this.filename = filename;
        load();
    }

    public void save()
    {
        //noinspection ResultOfMethodCallIgnored
        new File("data/").mkdir();
        File f = new File("data/" + filename + ".txt");
        try
        {
            FileWriter fw = new FileWriter(f);
            for (String string : strings)
            {
                fw.write(string + "\r\n");
            }
            fw.flush();
            fw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void load()
    {
        try
        {
            BufferedReader fileReader = new BufferedReader(new FileReader("data/" + filename + ".txt"));
            try
            {
                String line = fileReader.readLine();
                while (line != null)
                {
                    strings.add(line);
                    line = fileReader.readLine();
                }
                fileReader.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException ignored)
        {
        }
        save();
    }

    public String get(int index)
    {
        return strings.get(index);
    }

    public int indexOf(String string)
    {
        for (int i = 0; i < size(); i++)
        {
            if (get(i).equals(string)) return i;
        }
        return -1;
    }

    public boolean contains(String string)
    {
        return indexOf(string) > -1;
    }

    public int size()
    {
        return strings.size();
    }

    public void add(String string)
    {
        strings.add(string);
        save();
    }

    public boolean remove(String string)
    {
        int i = strings.indexOf(string);
        if (i > -1)
        {
            remove(i);
            return true;
        }
        return false;
    }

    public void remove(int index)
    {
        strings.remove(index);
        save();
    }
}
