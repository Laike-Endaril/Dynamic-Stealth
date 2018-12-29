package com.fantasticsource.dynamicstealth.server.ai;

import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import com.google.common.base.Predicate;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.lang.reflect.Field;
import java.util.List;

public class AINearestAttackableTargetEdit<T extends EntityLivingBase> extends AITargetEdit
{
    private static Field targetClassField, targetEntitySelectorField;

    static
    {
        initReflections();
    }

    public Class<T> targetClass;
    public Predicate<? super T> targetEntitySelector;
    public T targetEntity;

    public AINearestAttackableTargetEdit(EntityAINearestAttackableTarget oldAI) throws IllegalAccessException
    {
        super(oldAI);
        targetClass = (Class) targetClassField.get(oldAI);
        targetEntitySelector = (Predicate<? super T>) targetEntitySelectorField.get(oldAI);
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
        attacker.setAttackTarget(targetEntity);
        super.startExecuting();
    }


    private static void initReflections()
    {
        try
        {
            targetClassField = ReflectionTool.getField(EntityAINearestAttackableTarget.class, "field_75307_b", "targetClass");
            targetEntitySelectorField = ReflectionTool.getField(EntityAINearestAttackableTarget.class, "field_82643_g", "targetEntitySelector");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().exitJava(122, false);
        }
    }
}