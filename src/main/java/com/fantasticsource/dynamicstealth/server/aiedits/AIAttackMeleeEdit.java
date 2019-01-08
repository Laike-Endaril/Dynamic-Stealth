package com.fantasticsource.dynamicstealth.server.aiedits;

import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.lang.reflect.Field;

public class AIAttackMeleeEdit extends EntityAIAttackMelee
{
    private static Field attackerField, speedTowardsTargetField;

    static
    {
        initReflections();
    }

    protected final int attackInterval = 20;
    protected EntityCreature attacker;
    protected int attackTick;
    double speed;
    Path path;
    private EntityLivingBase target;

    public AIAttackMeleeEdit(EntityAIAttackMelee oldAI) throws IllegalAccessException
    {
        super((EntityCreature) attackerField.get(oldAI), 0, false);
        attacker = (EntityCreature) attackerField.get(oldAI);
        speed = (double) speedTowardsTargetField.get(oldAI);

        setMutexBits(3);
    }

    private static void initReflections()
    {
        try
        {
            attackerField = ReflectionTool.getField(EntityAIAttackMelee.class, "field_75441_b", "attacker");
            speedTowardsTargetField = ReflectionTool.getField(EntityAIAttackMelee.class, "field_75440_e", "speedTowardsTarget");
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().exitJava(126, false);
        }
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

        attacker.getMoveHelper().setMoveTo(target.posX, target.posY, target.posZ, speed);
        attacker.getLookHelper().setLookPositionWithEntity(target, 180, 180);

        if (!AITargetEdit.isSuitableTarget(attacker, target)) return false;

        if (getAttackReachSqr(target) >= attacker.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ)) return true;

        path = attacker.getNavigator().getPathToEntityLiving(target);
        return path != null;
    }

    @Override
    public void updateTask()
    {
        attacker.getNavigator().setPath(path, speed);

        double distSquared = attacker.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);

        if (attackTick > 0) attackTick--;
        checkAndPerformAttack(target, distSquared);
    }

    @Override
    public void resetTask()
    {
        if (!AITargetEdit.isSuitableTarget(attacker, attacker.getAttackTarget())) attacker.setAttackTarget(null);
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
