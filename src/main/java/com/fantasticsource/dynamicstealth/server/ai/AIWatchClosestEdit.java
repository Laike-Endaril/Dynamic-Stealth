package com.fantasticsource.dynamicstealth.server.ai;

import com.fantasticsource.dynamicstealth.server.configdata.EntityVisionData;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.lang.reflect.Field;
import java.util.List;

public class AIWatchClosestEdit extends EntityAIBase
{
    private static Field entityField, watchedClassField, chanceField;

    static
    {
        initReflections();
    }

    public EntityLiving entity;
    public Entity target;
    public int lookTime;
    public final float chance;
    public Class<? extends Entity> watchedClass;

    public AIWatchClosestEdit(EntityAIWatchClosest oldAI) throws IllegalAccessException
    {
        entity = (EntityLiving) entityField.get(oldAI);
        watchedClass = (Class<? extends Entity>) watchedClassField.get(oldAI);
        chance = (float) chanceField.get(oldAI);
        setMutexBits(2);
    }

    public AIWatchClosestEdit(EntityAIWatchClosest oldAI, boolean isEntityAIWatchClosest2) throws IllegalAccessException
    {
        this(oldAI);
        if (isEntityAIWatchClosest2) setMutexBits(3);
    }


    @Override
    public boolean shouldExecute()
    {
        if (entity.getRNG().nextFloat() >= chance) return false;

        if (entity.getAttackTarget() != null)
        {
            target = entity.getAttackTarget();
            if (AITargetEdit.isSuitableTarget(entity, (EntityLivingBase) target)) return true;

            target = null;
            entity.setAttackTarget(null);
            return false;
        }

        List<Entity> list;
        ExplicitPriorityQueue<Entity> queue;
        double range = EntityVisionData.distanceFar(entity);
        if (watchedClass == EntityPlayer.class)
        {
            list = entity.world.getEntitiesWithinAABB(EntityPlayer.class, entity.getEntityBoundingBox().grow(range, 4D, range));
        }
        else
        {
            list = entity.world.getEntitiesWithinAABB(watchedClass, entity.getEntityBoundingBox().grow(range, 3, range), Predicates.and(EntitySelectors.NOT_SPECTATING, EntitySelectors.notRiding(entity)));
        }

        if (list.isEmpty()) return false;

        queue = new ExplicitPriorityQueue<>(list.size());
        for (Entity e : list)
        {
            if (entity != e) queue.add(e, entity.getDistanceSq(e));
        }

        target = queue.poll();
        while (target != null && !entity.getEntitySenses().canSee(target)) //Doesn't need isSuitableTarget because it's not always used for attacking
        {
            target = queue.poll();
        }

        return target != null;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return (target.isEntityAlive() && entity.getEntitySenses().canSee(target) && lookTime > 0);
    }

    @Override
    public void startExecuting()
    {
        lookTime = 40 + entity.getRNG().nextInt(40);
    }

    @Override
    public void resetTask()
    {
        target = null;
    }

    @Override
    public void updateTask()
    {
        entity.getLookHelper().setLookPosition(target.posX, target.posY + target.getEyeHeight(), target.posZ, entity.getHorizontalFaceSpeed(), entity.getVerticalFaceSpeed());
        --lookTime;
    }


    private static void initReflections()
    {
        try
        {
            entityField = ReflectionTool.getField(EntityAIWatchClosest.class, "field_75332_b", "entity");
            watchedClassField = ReflectionTool.getField(EntityAIWatchClosest.class, "field_75329_f", "watchedClass");
            chanceField = ReflectionTool.getField(EntityAIWatchClosest.class, "field_75331_e", "chance");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().exitJava(123, false);
        }
    }
}