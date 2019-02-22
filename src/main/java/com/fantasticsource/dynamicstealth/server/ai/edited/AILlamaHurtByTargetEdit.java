package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.passive.EntityLlama;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AILlamaHurtByTargetEdit extends AIHurtByTargetEdit
{
    private static Method setDidSpitMethod;
    private static Field didSpitField;

    static
    {
        setDidSpitMethod = ReflectionTool.getMethod(EntityLlama.class, "func_190714_x", "setDidSpit");

        try
        {
            didSpitField = ReflectionTool.getField(EntityLlama.class, "field_190723_bJ", "didSpit");
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            MCTools.crash(e, 131, false);
        }
    }


    EntityLlama llama;

    public AILlamaHurtByTargetEdit(EntityAIHurtByTarget oldAI) throws IllegalAccessException
    {
        super(oldAI);
        llama = (EntityLlama) attacker;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        try
        {
            if ((boolean) didSpitField.get(llama))
            {
                setDidSpitMethod.invoke(llama, false);
                return false;
            }
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            MCTools.crash(e, 132, false);
        }

        return super.shouldContinueExecuting();
    }
}
