package com.fantasticsource.dynamicstealth.server.ai;

import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.lang.reflect.Field;

public class AIAttackRangedEdit extends EntityAIBase
{
    private static Field rangedAttackEntityHostField, entityMoveSpeedField, attackIntervalMinField, maxRangedAttackTimeField, attackRadiusField;

    static
    {
        initReflections();
    }


    private final EntityLiving attacker;
    private final IRangedAttackMob rangedAttacker;
    private EntityLivingBase target;
    private int timer;
    private final double entityMoveSpeed;
    private int seeTime;
    private final int attackIntervalMin, attackIntervalRange;
    private final float attackRadius;
    private final float attackRadiusSquared;
    private Path path = null;

    public AIAttackRangedEdit(EntityAIAttackRanged oldAI) throws IllegalAccessException
    {
        rangedAttacker = (IRangedAttackMob) rangedAttackEntityHostField.get(oldAI);
        entityMoveSpeed = (double) entityMoveSpeedField.get(oldAI);
        attackIntervalMin = (int) attackIntervalMinField.get(oldAI);
        attackRadius = (float) attackRadiusField.get(oldAI);

        attackIntervalRange = (int) maxRangedAttackTimeField.get(oldAI) - attackIntervalMin;

        attackRadiusSquared = attackRadius * attackRadius;
        attacker = (EntityLiving) rangedAttacker;
        timer = -1;

        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute()
    {
        EntityLivingBase testTarget = attacker.getAttackTarget();
        if (AITargetEdit.isSuitableTarget(attacker, testTarget))
        {
            target = testTarget;
            return true;
        }

        return false;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return shouldExecute();
    }

    @Override
    public void resetTask()
    {
        target = null;
        seeTime = 0;
        timer = -1;

        if (path != null && path.equals(attacker.getNavigator().getPath())) attacker.getNavigator().clearPath();
        path = null;
    }

    @Override
    public void updateTask()
    {
        double distSquared = attacker.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);

        seeTime++;

        if (distSquared <= attackRadiusSquared && seeTime >= 20)
        {
            if (path != null && path.equals(attacker.getNavigator().getPath())) attacker.getNavigator().clearPath();
            path = null;
        }
        else
        {
            path = attacker.getNavigator().getPathToEntityLiving(target);
            attacker.getNavigator().setPath(path, entityMoveSpeed);
        }

        attacker.getLookHelper().setLookPositionWithEntity(target, 30, 30);

        if (--timer == 0)
        {
            float distanceFactor = MathHelper.sqrt(distSquared) / attackRadius;
            rangedAttacker.attackEntityWithRangedAttack(target, MathHelper.clamp(distanceFactor, 0.1F, 1));
            timer = MathHelper.floor(attackIntervalMin + distanceFactor * attackIntervalRange);
        }
        else if (timer < 0)
        {
            float distanceFactor = MathHelper.sqrt(distSquared) / attackRadius;
            timer = MathHelper.floor(attackIntervalMin + distanceFactor * attackIntervalRange);
        }
    }


    private static void initReflections()
    {
        try
        {
            rangedAttackEntityHostField = ReflectionTool.getField(EntityAIAttackRanged.class, "field_82641_b", "rangedAttackEntityHost");
            entityMoveSpeedField = ReflectionTool.getField(EntityAIAttackRanged.class, "field_75321_e", "entityMoveSpeed");
            attackRadiusField = ReflectionTool.getField(EntityAIAttackRanged.class, "field_96562_i", "attackRadius");
            attackIntervalMinField = ReflectionTool.getField(EntityAIAttackRanged.class, "field_96561_g", "attackIntervalMin");
            maxRangedAttackTimeField = ReflectionTool.getField(EntityAIAttackRanged.class, "field_75325_h", "maxRangedAttackTime");
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().exitJava(138, false);
        }
    }
}