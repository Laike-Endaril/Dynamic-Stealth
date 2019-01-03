package com.fantasticsource.dynamicstealth.server.ai;

import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public abstract class AITargetEdit extends EntityAIBase
{
    private static Field taskOwnerField, nearbyOnlyField;

    static
    {
        initReflections();
    }


    public final EntityCreature attacker;
    public final boolean nearbyOnly;
    public EntityLivingBase target;

    public AITargetEdit(EntityAITarget oldAI) throws IllegalAccessException
    {
        attacker = (EntityCreature) taskOwnerField.get(oldAI);
        nearbyOnly = (boolean) nearbyOnlyField.get(oldAI);
    }

    public static boolean isSuitableTarget(EntityLiving attacker, @Nullable EntityLivingBase target)
    {
        if (target == null || target == attacker || !target.isEntityAlive() || !attacker.canAttackClass(target.getClass()) || attacker.isOnSameTeam(target))
        {
            return false;
        }

        if (attacker instanceof IEntityOwnable && ((IEntityOwnable) attacker).getOwnerId() != null)
        {
            if (target instanceof IEntityOwnable && ((IEntityOwnable) attacker).getOwnerId().equals(((IEntityOwnable) target).getOwnerId()))
            {
                return false;
            }

            if (target == ((IEntityOwnable) attacker).getOwner())
            {
                return false;
            }
        }
        else if (target instanceof EntityPlayer && ((EntityPlayer) target).capabilities.disableDamage)
        {
            return false;
        }

        return attacker.getEntitySenses().canSee(target);
    }

    private static void initReflections()
    {
        try
        {
            nearbyOnlyField = ReflectionTool.getField(EntityAITarget.class, "field_75303_a", "nearbyOnly");
            taskOwnerField = ReflectionTool.getField(EntityAITarget.class, "field_75299_d", "taskOwner");
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().exitJava(128, false);
        }
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

        if (!attacker.getEntitySenses().canSee(target)) return false;

        attacker.setAttackTarget(target);
        return true;
    }

    protected double getFollowDistance()
    {
        return attacker.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();
    }

    @Override
    public void resetTask()
    {
        attacker.setAttackTarget(null);
        target = null;
    }

    protected boolean isSuitableTarget(@Nullable EntityLivingBase target)
    {
        if (target == null || (nearbyOnly && !canEasilyReach(target))) return false;

        return isSuitableTarget(attacker, target) && attacker.isWithinHomeDistanceFromPosition(new BlockPos(target));
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