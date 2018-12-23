package com.fantasticsource.dynamicstealth.ai;

import com.fantasticsource.dynamicstealth.DynamicStealthConfig;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFindEntityNearest;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.List;

public class AIFindEntityNearestEdit extends EntityAIBase
{
    private static Field mobField, classToCheckField;

    static
    {
        initReflections();
    }

    public EntityLiving searcher;
    public Class<? extends EntityLivingBase> targetClass;

    public static Logger LOGGER = LogManager.getLogger();
    public EntityLivingBase target;


    public AIFindEntityNearestEdit(EntityAIFindEntityNearest oldAI) throws IllegalAccessException
    {
        searcher = (EntityLiving) mobField.get(oldAI);
        targetClass = (Class<? extends EntityLivingBase>) classToCheckField.get(oldAI);

        if (searcher instanceof EntityCreature) LOGGER.warn("Use NearestAttackableTargetGoal.class for PathfinderMob mobs!");
    }

    @Override
    public boolean shouldExecute()
    {
        double range = DynamicStealthConfig.f_distances.distanceFar;
        List<EntityLivingBase> list = searcher.world.getEntitiesWithinAABB(targetClass, searcher.getEntityBoundingBox().grow(range, 4, range));
        if (list.isEmpty()) return false;

        ExplicitPriorityQueue<EntityLivingBase> queue = new ExplicitPriorityQueue<>(list.size());
        for (EntityLivingBase entity : list)
        {
            if (entity != searcher) queue.add(entity, entity.getDistanceSq(searcher));
        }

        target = queue.poll();
        while (target != null && !AITargetEdit.isSuitableTarget(searcher, target)) target = queue.poll();
        return target != null;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return AITargetEdit.isSuitableTarget(searcher, searcher.getAttackTarget());
    }

    @Override
    public void startExecuting()
    {
        searcher.setAttackTarget(target);
        super.startExecuting();
    }

    @Override
    public void resetTask()
    {
        searcher.setAttackTarget(null);
        super.startExecuting();
    }


    private static void initReflections()
    {
        try
        {
            mobField = ReflectionTool.getField(EntityAIFindEntityNearest.class, "field_179442_b", "mob");
            classToCheckField = ReflectionTool.getField(EntityAIFindEntityNearest.class, "field_179439_f", "classToCheck");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().exitJava(120, false);
        }
    }
}