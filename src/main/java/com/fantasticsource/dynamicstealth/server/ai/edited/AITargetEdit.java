package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.dynamicstealth.compat.Compat;
import com.fantasticsource.dynamicstealth.server.senses.sight.Sight;
import com.fantasticsource.dynamicstealth.server.threat.EntityThreatData;
import com.fantasticsource.mctools.MCTools;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;

public abstract class AITargetEdit extends EntityAIBase
{
    public final EntityCreature attacker;
    public final boolean nearbyOnly;
    public EntityLivingBase target;

    public AITargetEdit(EntityAITarget oldAI)
    {
        attacker = oldAI.taskOwner;
        nearbyOnly = oldAI.nearbyOnly;
    }

    public static boolean isSuitableTarget(EntityLiving attacker, @Nullable EntityLivingBase target)
    {
        if (target == null || target == attacker || !target.isEntityAlive() || !attacker.canAttackClass(target.getClass()) || attacker.isOnSameTeam(target))
        {
            return false;
        }

        if (EntityThreatData.isPassive(attacker)) return false;

        if (attacker.isOnSameTeam(target)) return false;

        if (attacker instanceof IEntityOwnable)
        {
            Entity attackerOwner = ((IEntityOwnable) attacker).getOwner();
            if (attackerOwner != null)
            {
                //Don't attack your owner
                if (target == attackerOwner) return false;

                //Don't attack others owned by the same owner
                if (target instanceof IEntityOwnable && attackerOwner == ((IEntityOwnable) target).getOwner())
                {
                    return false;
                }
            }
        }

        if (target instanceof EntityPlayer && ((EntityPlayer) target).capabilities.disableDamage) return false;

        return Sight.canSee(attacker, target, true);
    }

    protected boolean isSuitableTarget(@Nullable EntityLivingBase target)
    {
        if (target instanceof EntityPlayerMP && ((EntityPlayerMP) target).capabilities.disableDamage) return false;

        if (target == null || (nearbyOnly && !canEasilyReach(target))) return false;

        return isSuitableTarget(attacker, target) && attacker.isWithinHomeDistanceFromPosition(new BlockPos(target));
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        EntityLivingBase target = attacker.getAttackTarget();
        if (target == null) target = this.target;
        if (target == null || !target.isEntityAlive()) return false;

        Team attackerTeam = attacker.getTeam();
        Team targetTeam = target.getTeam();
        if (attackerTeam != null && targetTeam == attackerTeam) return false;

        if (target instanceof EntityPlayer && ((EntityPlayer) target).capabilities.disableDamage) return false;

        if (!Sight.canSee(attacker, target, true)) return false;

        attacker.setAttackTarget(target);
        return true;
    }

    protected double getFollowDistance()
    {
        return MCTools.getAttribute(attacker, SharedMonsterAttributes.FOLLOW_RANGE, 0); //TODO To emulate vanilla, the default should be 16.  Unsure why I set this to 0 (possibly efficiency, mod compat, or who knows)
    }

    @Override
    public void resetTask()
    {
        Compat.clearAttackTargetAndCancelBadTasks(attacker);
        target = null;
    }

    private boolean canEasilyReach(EntityLivingBase target)
    {
        Path path = attacker.getNavigator().getPathToEntityLiving(target);

        if (path == null) return false;

        PathPoint pathpoint = path.getFinalPathPoint();
        if (pathpoint == null) return false;

        int i = pathpoint.x - MathHelper.floor(target.posX);
        int j = pathpoint.z - MathHelper.floor(target.posZ);
        return (i * i + j * j) <= 2.25;
    }
}