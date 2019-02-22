package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityVindicator;

import java.lang.reflect.Field;

public class AIJohnnyAttackEdit extends AINearestAttackableTargetEdit<EntityLivingBase>
{
    private static Field johnnyField;

    static
    {
        try
        {
            johnnyField = ReflectionTool.getField(EntityVindicator.class, "field_190643_b", "johnny");
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            MCTools.crash(e, 133, false);
        }
    }


    public AIJohnnyAttackEdit(EntityAINearestAttackableTarget oldAI) throws IllegalAccessException
    {
        super(oldAI);
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
            MCTools.crash(e, 134, false);
            return false;
        }
    }
}
