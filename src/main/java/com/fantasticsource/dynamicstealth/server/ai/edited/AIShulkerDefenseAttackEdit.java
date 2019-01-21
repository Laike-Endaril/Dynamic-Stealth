package com.fantasticsource.dynamicstealth.server.ai.edited;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

public class AIShulkerDefenseAttackEdit extends AINearestAttackableTargetEdit<EntityLivingBase>
{
    public AIShulkerDefenseAttackEdit(EntityAINearestAttackableTarget<EntityLivingBase> oldAI) throws IllegalAccessException
    {
        super(oldAI);
    }

    public boolean shouldExecute()
    {
        return attacker.getTeam() != null && super.shouldExecute();
    }

    public AxisAlignedBB getTargetableArea(double targetDistance)
    {
        EnumFacing enumfacing = ((EntityShulker) attacker).getAttachmentFacing();

        if (enumfacing.getAxis() == EnumFacing.Axis.X)
        {
            return attacker.getEntityBoundingBox().grow(4, targetDistance, targetDistance);
        }
        else
        {
            return enumfacing.getAxis() == EnumFacing.Axis.Z ? attacker.getEntityBoundingBox().grow(targetDistance, targetDistance, 4) : attacker.getEntityBoundingBox().grow(targetDistance, 4, targetDistance);
        }
    }
}
