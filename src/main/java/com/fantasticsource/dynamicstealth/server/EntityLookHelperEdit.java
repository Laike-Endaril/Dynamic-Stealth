package com.fantasticsource.dynamicstealth.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.util.math.Vec3d;

public class EntityLookHelperEdit extends EntityLookHelper
{
    public EntityLookHelperEdit(EntityLiving entitylivingIn)
    {
        super(entitylivingIn);

        if (!entity.world.isRemote)
        {
            Vec3d vec = entitylivingIn.getPositionVector();
            setLookPosition(vec.x + Math.random() - 0.5, vec.y + entitylivingIn.getEyeHeight(), vec.z + Math.random() - 0.5, 180, 180);
        }
        else
        {
            entity.prevRotationYawHead = entity.rotationYawHead;
            entity.rotationYaw = entity.rotationYawHead;
            entity.prevRotationYaw = entity.rotationYawHead;
        }

        EntityLookHelper old = entitylivingIn.lookHelper;
        entity = old.entity;
        deltaLookYaw = old.deltaLookYaw;
        deltaLookPitch = old.deltaLookPitch;
        isLooking = old.isLooking;
        posX = old.posX;
        posY = old.posY;
        posZ = old.posZ;
    }

    public void setLookPositionWithEntity(Entity entityIn, float deltaYaw, float deltaPitch)
    {
        if (entityIn != null) super.setLookPositionWithEntity(entityIn, deltaYaw, deltaPitch);
    }
}