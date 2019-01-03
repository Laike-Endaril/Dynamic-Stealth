package com.fantasticsource.dynamicstealth.server.ai;

import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AILlamaHurtByTargetEdit extends AIHurtByTargetEdit
{
    private static Method setDidSpitMethod;
    private static Field didSpitField;

    static
    {
        initReflections();
    }


    EntityLlama llama;

    public AILlamaHurtByTargetEdit(EntityAIHurtByTarget oldAI) throws IllegalAccessException
    {
        super(oldAI);
        llama = (EntityLlama) attacker;
    }

    private static void initReflections()
    {
        setDidSpitMethod = ReflectionTool.getMethod(EntityLlama.class, "func_190714_x", "setDidSpit");

        try
        {
            didSpitField = ReflectionTool.getField(EntityLlama.class, "field_190723_bJ", "didSpit");
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().exitJava(131, false);
        }
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
            e.printStackTrace();
            FMLCommonHandler.instance().exitJava(132, false);
        }

        return super.shouldContinueExecuting();
    }
}
