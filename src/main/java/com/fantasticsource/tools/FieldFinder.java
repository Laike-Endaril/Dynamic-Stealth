package com.fantasticsource.tools;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;

public class FieldFinder
{
    private Class clss, fieldClass;
    private ArrayList<Field> possibleFields;
    private StringBuilder log = new StringBuilder();

    private FieldFinder()
    {
    }

    public static FieldFinder getFinder(Class clss, Class fieldClass) throws NoSuchFieldException, IllegalAccessException
    {
        if (clss == null || fieldClass == null) return null;


        FieldFinder fieldFinder = new FieldFinder();

        fieldFinder.clss = clss;
        fieldFinder.fieldClass = fieldClass;
        fieldFinder.possibleFields = new ArrayList<>(Arrays.asList(clss.getDeclaredFields()));
        for (Field field : fieldFinder.possibleFields)
        {
            field.setAccessible(true);

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        }

        fieldFinder.log.append("Created FieldFinder for ").append(fieldClass.getSimpleName()).append("s within a ").append(clss.getSimpleName()).append("\r\n");
        fieldFinder.log.append("Initial number of possible fields is ").append(fieldFinder.possibleFields.size()).append("\r\n\r\n");

        return fieldFinder;
    }

    @SuppressWarnings({"UnusedReturnValue", "unchecked"})
    public FieldFinder removeIfNot(Object objectContainingFields, Object goalValue) throws IllegalAccessException
    {
        if (objectContainingFields.getClass() == clss && goalValue.getClass() == fieldClass)
        {
            for (Field field : (ArrayList<Field>) possibleFields.clone())
            {
                Object observedValue = field.get(objectContainingFields);
                if (!observedValue.equals(goalValue))
                {
                    log.append("Removing field ").append(field.getName()).append("; Goal was <").append(goalValue).append(">, but the field's value was <").append(observedValue).append(">\r\n");
                    possibleFields.remove(field);
                }
            }
            log.append("There are now ").append(possibleFields.size()).append(" possible fields remaining\r\n\r\n");
        }

        return this;
    }

    public int remainingPossibilities()
    {
        return possibleFields.size();
    }

    public String toString()
    {
        return toString(null);
    }

    @SuppressWarnings("WeakerAccess")
    public String toString(Object objectContainingFields)
    {
        if (possibleFields.size() == 0) return null;

        StringBuilder str = new StringBuilder();

        if (objectContainingFields == null)
        {
            for (Field field : possibleFields) str.append(field).append("\r\n");
        }
        else if (objectContainingFields.getClass() != clss)
        {
            return objectContainingFields + " is not an instance of " + clss.getName();
        }
        else
        {
            try
            {
                for (Field field : possibleFields)
                {
                    str.append(field).append(" = ").append(field.get(objectContainingFields)).append("\r\n");
                }
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
        return str.toString();
    }


    public String getLog()
    {
        return getLog(null);
    }

    @SuppressWarnings("WeakerAccess")
    public String getLog(Object objectContainingFields)
    {
        return log.toString() + "Remaining entries are as follows:\r\n\r\n" + toString(objectContainingFields) + "\r\n";
    }
}
