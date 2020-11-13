package com.fantasticsource.dynamicstealth.compat;

import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.Entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class CompatIceAndFire
{
    protected static final Class
            STONE_ENTITY_PROPERTIES_CLASS = ReflectionTool.getClassByName("com.github.alexthe666.iceandfire.entity.StoneEntityProperties"),
            ENTITY_PROPERTIES_HANDLER_CLASS = ReflectionTool.getClassByName("net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler");

    protected static final Field STONE_ENTITY_PROPERTIES_IS_STONE_FIELD = ReflectionTool.getField(STONE_ENTITY_PROPERTIES_CLASS, "isStone");

    protected static final Object ENTITY_PROPERTIES_HANDLER_INSTANCE = ReflectionTool.get(ENTITY_PROPERTIES_HANDLER_CLASS, "INSTANCE", null);

    protected static final Method ENTITY_PROPERTIES_HANDLER_GET_PROPERTIES_METHOD = ReflectionTool.getMethod(ENTITY_PROPERTIES_HANDLER_CLASS, "getProperties");


    public static boolean isPetrified(Entity entity)
    {
        Object properties = ReflectionTool.invoke(ENTITY_PROPERTIES_HANDLER_GET_PROPERTIES_METHOD, ENTITY_PROPERTIES_HANDLER_INSTANCE, entity, STONE_ENTITY_PROPERTIES_CLASS);
        return properties != null && (boolean) ReflectionTool.get(STONE_ENTITY_PROPERTIES_IS_STONE_FIELD, properties);
    }
}
