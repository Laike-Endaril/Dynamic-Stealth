package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.dynamicstealth.compat.Compat;
import com.fantasticsource.mctools.MCTools;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;


public class AIGuardianAttackEdit extends EntityAIBase
{
    private final EntityGuardian guardian;
    private final boolean isElder;
    private int tickCounter;
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
        Compat.clearAttackTargetAndReplaceAITasks(guardian);
        guardian.wander.makeUpdate();
    }

    private void setTargetedEntity(int entityId)
    {
        guardian.getDataManager().set(guardian.TARGET_ENTITY, entityId);
    }

    public void updateTask()
    {
        guardian.getNavigator().clearPath();
        guardian.getLookHelper().setLookPositionWithEntity(target, 90.0F, 90.0F);

        if (!guardian.canEntityBeSeen(target)) Compat.clearAttackTargetAndReplaceAITasks(guardian);

        ++tickCounter;

        if (tickCounter == 0)
        {
            guardian.getDataManager().set(guardian.TARGET_ENTITY, target.getEntityId());
            guardian.world.setEntityState(guardian, (byte) 21);
        }
        else if (tickCounter >= guardian.getAttackDuration())
        {
            float f = 1.0F;
            if (guardian.world.getDifficulty() == EnumDifficulty.HARD) f += 2.0F;
            if (isElder) f += 2.0F;

            target.attackEntityFrom(DamageSource.causeIndirectMagicDamage(guardian, guardian), f);
            target.attackEntityFrom(DamageSource.causeMobDamage(guardian), (float) MCTools.getAttribute(guardian, SharedMonsterAttributes.ATTACK_DAMAGE, 0));
            Compat.clearAttackTargetAndReplaceAITasks(guardian);
        }
    }
}
