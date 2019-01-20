package com.fantasticsource.dynamicstealth.server.aiedits;

import com.fantasticsource.dynamicstealth.server.senses.EntityVisionData;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.List;

public class AIFindEntityNearestPlayerEdit extends EntityAIBase
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static Field entityLivingField;

    static
    {
        initReflections();
    }

    private final EntityLiving searcher;
    private EntityLivingBase target;

    public AIFindEntityNearestPlayerEdit(EntityAIFindEntityNearestPlayer oldAI) throws IllegalAccessException
    {
        searcher = (EntityLiving) entityLivingField.get(oldAI);

        if (searcher instanceof EntityCreature) LOGGER.warn("Use NearestAttackableTargetGoal.class for PathfinderMob mobs!");
    }

    private static void initReflections()
    {
        try
        {
            entityLivingField = ReflectionTool.getField(EntityAIFindEntityNearestPlayer.class, "field_179434_b", "entityLiving");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().exitJava(125, false);
        }
    }

    @Override
    public boolean shouldExecute()
    {
        double range = EntityVisionData.distanceFar(searcher);
        List<EntityPlayer> list = searcher.world.getEntitiesWithinAABB(EntityPlayer.class, searcher.getEntityBoundingBox().grow(range, 4D, range));

        if (list.isEmpty()) return false;

        ExplicitPriorityQueue<EntityPlayer> queue = new ExplicitPriorityQueue<>(list.size());
        for (EntityPlayer entity : list) queue.add(entity, searcher.getDistanceSq(entity));

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
}
