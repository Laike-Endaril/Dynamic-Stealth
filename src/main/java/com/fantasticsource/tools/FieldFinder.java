package com.fantasticsource.tools;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;

public class FieldFinder
{
    private Class clss, fieldClass;
    private ArrayList<Field> possibleFields;

    private FieldFinder()
    {
    }

    public static FieldFinder getFinder(Class clss, Class fieldClass)
    {
        if (clss == null || fieldClass == null) return null;


        FieldFinder fieldFinder = new FieldFinder();

        fieldFinder.clss = clss;
        fieldFinder.fieldClass = fieldClass;
        fieldFinder.possibleFields = new ArrayList<>(Arrays.asList(clss.getDeclaredFields()));

        return fieldFinder;
    }

    public FieldFinder removeIfNot(Object objectContainingFields, Object value) throws IllegalAccessException, NoSuchFieldException
    {
        if (objectContainingFields.getClass() == clss && value.getClass() == fieldClass)
        {
            for (Field field : (ArrayList<Field>) possibleFields.clone())
            {
                field.setAccessible(true);

                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

                if (!field.get(objectContainingFields).equals(value)) possibleFields.remove(field);
            }
        }

        return this;
    }

    public int remainingPossibilities()
    {
        return possibleFields.size();
    }

    public String toString()
    {
        if (possibleFields.size() == 0) return null;

        StringBuilder str = new StringBuilder();
        for(Field field : possibleFields) str.append(field).append("\r\n");
        return str.toString();
    }
}
