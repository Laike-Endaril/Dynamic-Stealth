package com.fantasticsource.dynamicstealth.server.ai;

import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.lang.reflect.Field;


public class AIGuardianAttackEdit extends EntityAIBase
{
    private static Field targetEntityField, wanderField;

    static
    {
        initReflections();
    }


    private final EntityGuardian guardian;
    private int tickCounter;
    private final boolean isElder;
    private EntityLivingBase target = null;

    public AIGuardianAttackEdit(EntityGuardian guardianIn)
    {
        guardian = guardianIn;
        isElder = guardian instanceof EntityElderGuardian;
        setMutexBits(3);
    }

    public boolean shouldExecute()
    {
        target = guardian.getAttackTarget();
        return target != null && target.isEntityAlive();
    }

    public boolean shouldContinueExecuting()
    {
        return shouldExecute() && (isElder || guardian.getDistanceSq(target) > 9.0D);
    }

    public void startExecuting()
    {
        tickCounter = -10;
        guardian.getNavigator().clearPath();
        guardian.getLookHelper().setLookPositionWithEntity(target, 90.0F, 90.0F);
        guardian.isAirBorne = true;
    }

    public void resetTask()
    {
        setTargetedEntity(0);
        guardian.setAttackTarget(null);
        try
        {
            ((EntityAIWander) wanderField.get(guardian)).makeUpdate();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().exitJava(142, false);
        }
    }

    private void setTargetedEntity(int entityId)
    {
        try
        {
            guardian.getDataManager().set((DataParameter<Integer>) targetEntityField.get(guardian), entityId);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().exitJava(141, false);
        }
    }

    public void updateTask()
    {
        guardian.getNavigator().clearPath();
        guardian.getLookHelper().setLookPositionWithEntity(target, 90.0F, 90.0F);

        if (!guardian.canEntityBeSeen(target)) guardian.setAttackTarget(null);

        ++tickCounter;

        if (tickCounter == 0)
        {
            try
            {
                guardian.getDataManager().set((DataParameter<Integer>) targetEntityField.get(guardian), target.getEntityId());
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
                FMLCommonHandler.instance().exitJava(143, false);
            }
            guardian.world.setEntityState(guardian, (byte) 21);
        }
        else if (tickCounter >= guardian.getAttackDuration())
        {
            float f = 1.0F;
            if (guardian.world.getDifficulty() == EnumDifficulty.HARD) f += 2.0F;
            if (isElder) f += 2.0F;

            target.attackEntityFrom(DamageSource.causeIndirectMagicDamage(guardian, guardian), f);
            target.attackEntityFrom(DamageSource.causeMobDamage(guardian), (float) guardian.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
            guardian.setAttackTarget(null);
        }
    }


    private static void initReflections()
    {
        try
        {
            targetEntityField = ReflectionTool.getField(EntityGuardian.class, "field_184723_b", "TARGET_ENTITY");
            wanderField = ReflectionTool.getField(EntityGuardian.class, "field_175481_bq", "wander");
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().exitJava(140, false);
        }
    }
}
