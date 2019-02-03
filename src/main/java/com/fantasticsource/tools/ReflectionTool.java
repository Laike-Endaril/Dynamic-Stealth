package com.fantasticsource.tools;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ReflectionTool
{
    public static Field getField(Class classType, String... possibleFieldnames) throws NoSuchFieldException, IllegalAccessException
    {
        return getField(false, classType, possibleFieldnames);
    }

    public static Field getField(boolean printFound, Class classType, String... possibleFieldnames) throws NoSuchFieldException, IllegalAccessException
    {
        Field[] fields = classType.getDeclaredFields();
        for (Field field : fields)
        {
            for (String name : possibleFieldnames)
            {
                if (field.getName().equals(name))
                {
                    field.setAccessible(true);

                    Field modifiersField = Field.class.getDeclaredField("modifiers");
                    modifiersField.setAccessible(true);
                    modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

                    if (printFound) System.out.println(name);
                    return field;
                }
            }
        }
        return null;
    }

    public static Method getMethod(Class classType, String... possibleMethodNames)
    {
        return getMethod(false, classType, possibleMethodNames);
    }

    public static Method getMethod(boolean printFound, Class classType, String... possibleMethodNames)
    {
        Method[] methods = classType.getDeclaredMethods();
        for (Method method : methods)
        {
            for (String name : possibleMethodNames)
            {
                if (method.getName().equals(name))
                {
                    method.setAccessible(true);
                    if (printFound) System.out.println(name);
                    return method;
                }
            }
        }
        return null;
    }

    public static Class getInternalClass(Class classType, String... possibleInternalClassNames)
    {
        Class[] classes = classType.getDeclaredClasses();
        for (Class class1 : classes)
        {
            for (String name : possibleInternalClassNames)
            {
                if (class1.getSimpleName().equals(name))
                {
                    return class1;
                }
            }
        }
        return null;
    }
}
