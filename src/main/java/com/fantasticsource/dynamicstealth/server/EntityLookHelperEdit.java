package com.fantasticsource.dynamicstealth.server;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import com.fantasticsource.mctools.MCTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.util.math.MathHelper;

import java.lang.reflect.InvocationTargetException;

public class EntityLookHelperEdit extends EntityLookHelper
{
    public EntityLookHelperEdit(EntityLiving entitylivingIn)
    {
        super(entitylivingIn);
        entity = entitylivingIn;

        if (!entity.world.isRemote)
        {
            try
            {
                DynamicStealth.makeLivingLookDirection(entity, Math.random() * 360, 0);
            }
            catch (InvocationTargetException | IllegalAccessException e)
            {
                MCTools.crash(e, 146, false);
            }
        }
        else
        {
            entity.prevRotationYawHead = entity.rotationYawHead;
            entity.rotationYaw = entity.rotationYawHead;
            entity.prevRotationYaw = entity.rotationYawHead;
        }
    }

    public void setLookPositionWithEntity(Entity entityIn, float deltaYaw, float deltaPitch)
    {
        if (entityIn != null) super.setLookPositionWithEntity(entityIn, deltaYaw, deltaPitch);
    }
}