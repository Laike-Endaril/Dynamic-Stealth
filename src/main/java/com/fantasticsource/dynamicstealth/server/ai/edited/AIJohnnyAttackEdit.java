package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.lang.reflect.Field;

public class AIJohnnyAttackEdit extends AINearestAttackableTargetEdit<EntityLivingBase>
{
    private static Field johnnyField;

    static
    {
        initReflections();
    }


    public AIJohnnyAttackEdit(EntityAINearestAttackableTarget oldAI) throws IllegalAccessException
    {
        super(oldAI);
    }

    private static void initReflections()
    {
        try
        {
            johnnyField = ReflectionTool.getField(EntityVindicator.class, "field_190643_b", "johnny");
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().exitJava(133, false);
        }
    }

    @Override
    public boolean shouldExecute()
    {
        try
        {
            return (boolean) johnnyField.get(attacker) && super.shouldExecute();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().exitJava(134, false);
            return false;
        }
    }
}
