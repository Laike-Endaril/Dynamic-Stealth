package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.passive.EntityLlama;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AILlamaHurtByTargetEdit extends AIHurtByTargetEdit
{
    private static Method setDidSpitMethod;

    static
    {
        setDidSpitMethod = ReflectionTool.getMethod(EntityLlama.class, "func_190714_x", "setDidSpit");
    }


    public EntityLlama llama;

    public AILlamaHurtByTargetEdit(EntityAIHurtByTarget oldAI)
    {
        super(oldAI);
        llama = (EntityLlama) attacker;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        try
        {
            if (llama.didSpit)
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
