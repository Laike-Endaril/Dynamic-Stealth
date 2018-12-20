package com.fantasticsource.dynamicstealth.ai;

import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AIPigmanHurtByAggressorEdit extends AIHurtByTargetEdit
{
    private static Method pigmanBecomeAngryAtMethod;
    static
    {
        initReflections();
    }



    public AIPigmanHurtByAggressorEdit(EntityAIHurtByTarget oldAI) throws IllegalAccessException
    {
        super(oldAI);
    }

    @Override
    protected void setEntityAttackTarget(EntityCreature attacker, EntityLivingBase target)
    {
        super.setEntityAttackTarget(attacker, target);

        if (attacker instanceof EntityPigZombie)
        {
            try
            {
                pigmanBecomeAngryAtMethod.invoke(attacker, target);
            }
            catch (IllegalAccessException | InvocationTargetException e)
            {
                e.printStackTrace();
                FMLCommonHandler.instance().exitJava(130, false);
            }
        }
    }



    private static void initReflections()
    {
        pigmanBecomeAngryAtMethod = ReflectionTool.getMethod(EntityPigZombie.class, "func_184645_a", "becomeAngryAt");
    }
}
