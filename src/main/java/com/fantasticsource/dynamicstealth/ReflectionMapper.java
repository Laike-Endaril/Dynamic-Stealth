package com.fantasticsource.dynamicstealth;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class ReflectionMapper
{
    public static void mapFields(Class classType) throws IOException
    {
        Field[] fields = classType.getDeclaredFields();
        ArrayList<String> existingFields = new ArrayList<>();

        String filename = classType.getSimpleName() + "_field_mappings.txt";
        File file = new File(filename);

        if (file.exists())
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null && !line.equals(""))
            {
                existingFields.add(line);
                line = reader.readLine();
            }
            reader.close();
            file.delete();
        }

        if (fields.length > 0)
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

            if (existingFields.size() > 0)
            {
                if (existingFields.size() == fields.length)
                {
                    for (int i = 0; i < fields.length; i++)
                    {
                        writer.write(fields[i].getName() + "\t==\t" + existingFields.get(i) + "\r\n");
                    }
                    writer.close();
                }
                else throw new IllegalArgumentException("Map length mismatch!");
            }
            else
            {
                for (Field field : fields)
                {
                    writer.write(field.getName() + "\r\n");
                }
                writer.close();
            }
        }
    }

    public static void mapMethods(Class classType) throws IOException
    {
        Method[] methods = classType.getDeclaredMethods();
        ArrayList<String> existingMethods = new ArrayList<>();

        String filename = classType.getSimpleName() + "_method_mappings.txt";
        File file = new File(filename);

        if (file.exists())
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null && !line.equals(""))
            {
                existingMethods.add(line);
                line = reader.readLine();
            }
            reader.close();
            file.delete();
        }

        if (methods.length > 0)
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

            if (existingMethods.size() > 0)
            {
                if (existingMethods.size() == methods.length)
                {
                    for (int i = 0; i < methods.length; i++)
                    {
                        writer.write(methods[i].getName() + "(" + methods[i].getParameterCount() + " args)\t==\t" + existingMethods.get(i) + "\r\n");
                    }
                    writer.close();
                }
                else throw new IllegalArgumentException("Map length mismatch!");
            }
            else
            {
                for (Method method : methods)
                {
                    writer.write(method.getName() + "(" + method.getParameterCount() + " args)\r\n");
                }
                writer.close();
            }
        }
    }
}
