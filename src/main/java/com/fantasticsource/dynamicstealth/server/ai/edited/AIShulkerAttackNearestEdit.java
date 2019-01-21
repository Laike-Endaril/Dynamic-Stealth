package com.fantasticsource.dynamicstealth.server.ai.edited;

import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.EnumDifficulty;

public class AIShulkerAttackNearestEdit extends AINearestAttackableTargetEdit<EntityPlayer>
{
    public AIShulkerAttackNearestEdit(EntityAINearestAttackableTarget<EntityPlayer> oldAI) throws IllegalAccessException
    {
        super(oldAI);
    }

    public boolean shouldExecute()
    {
        return attacker.world.getDifficulty() != EnumDifficulty.PEACEFUL && super.shouldExecute();
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
