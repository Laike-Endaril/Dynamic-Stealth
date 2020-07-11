package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.dynamicstealth.compat.Compat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EnumHand;

public class AIAttackMeleeEdit extends EntityAIAttackMelee
{
    protected final int attackInterval = 20;
    protected int attackTick;
    Path path;
    private EntityLivingBase target;

    public AIAttackMeleeEdit(EntityAIAttackMelee oldAI)
    {
        super(oldAI.attacker, oldAI.speedTowardsTarget, false);

        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute()
    {
        target = attacker.getAttackTarget();

        if (!AITargetEdit.isSuitableTarget(attacker, target)) return false;

        if (getAttackReachSqr(target) >= attacker.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ)) return true;

        path = attacker.getNavigator().getPathToEntityLiving(target);
        return path != null;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        if (target == null || !target.isEntityAlive()) return false;

        attacker.getMoveHelper().setMoveTo(target.posX, target.posY, target.posZ, speedTowardsTarget);
        attacker.getLookHelper().setLookPositionWithEntity(target, 180, 180);

        if (!AITargetEdit.isSuitableTarget(attacker, target)) return false;

        if (getAttackReachSqr(target) >= attacker.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ)) return true;

        path = attacker.getNavigator().getPathToEntityLiving(target);
        return path != null;
    }

    @Override
    public void updateTask()
    {
        attacker.getNavigator().setPath(path, speedTowardsTarget);

        double distSquared = attacker.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);

        if (attackTick > 0) attackTick--;
        checkAndPerformAttack(target, distSquared);
    }

    @Override
    public void resetTask()
    {
        if (!AITargetEdit.isSuitableTarget(attacker, attacker.getAttackTarget())) Compat.clearAttackTargetAndCancelBadTasks(attacker);
        if (path != null && path.equals(attacker.getNavigator().getPath())) attacker.getNavigator().clearPath();
        path = null;
    }

    @Override
    protected void checkAndPerformAttack(EntityLivingBase target, double distSquared)
    {
        if (attackTick <= 0 && distSquared <= getAttackReachSqr(target))
        {
            attackTick = attackInterval;
            attacker.swingArm(EnumHand.MAIN_HAND);
            attacker.attackEntityAsMob(target);
        }
    }

    @Override
    protected double getAttackReachSqr(EntityLivingBase target)
    {
        return Math.pow(attacker.width * 2, 2) + target.width;
    }
}
