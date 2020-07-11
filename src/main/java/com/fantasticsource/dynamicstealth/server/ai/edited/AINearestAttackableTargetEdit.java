package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.dynamicstealth.compat.Compat;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import com.google.common.base.Predicate;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class AINearestAttackableTargetEdit<T extends EntityLivingBase> extends AITargetEdit
{
    public Class<T> targetClass;
    public Predicate<? super T> targetEntitySelector;
    public T targetEntity;

    public AINearestAttackableTargetEdit(EntityAINearestAttackableTarget oldAI)
    {
        super(oldAI);
        targetClass = oldAI.targetClass;
        targetEntitySelector = oldAI.targetEntitySelector;
    }

    @Override
    public boolean shouldExecute()
    {
        List<T> list;
        if (!EntityPlayer.class.isAssignableFrom(targetClass))
        {
            list = attacker.world.getEntitiesWithinAABB(targetClass, getTargetableArea(getFollowDistance()), targetEntitySelector);
        }
        else
        {
            list = attacker.world.getPlayers(targetClass, targetEntitySelector);
        }

        if (list.isEmpty()) return false;

        ExplicitPriorityQueue<T> queue = new ExplicitPriorityQueue<>(list.size());
        for (T entity : list)
        {
            if (entity != attacker) queue.add(entity, attacker.getDistanceSq(entity));
        }

        targetEntity = queue.poll();
        while (targetEntity != null && !isSuitableTarget(targetEntity)) targetEntity = queue.poll();

        return targetEntity != null;
    }

    public AxisAlignedBB getTargetableArea(double targetDistance)
    {
        return attacker.getEntityBoundingBox().grow(targetDistance, 4D, targetDistance);
    }

    @Override
    public void startExecuting()
    {
        if (targetEntity == null) Compat.clearAttackTargetAndCancelBadTasks(attacker);
        else attacker.setAttackTarget(targetEntity);
        super.startExecuting();
    }
}