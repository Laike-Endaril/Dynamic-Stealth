package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.dynamicstealth.server.senses.sight.Sight;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import com.google.common.base.Predicate;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFollow;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.pathfinding.*;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.lang.reflect.Field;
import java.util.List;

public class AIParrotFollowEdit extends EntityAIBase //In vanilla, this is only used by parrots, to randomly follow other mobs that are not parrots
{
    private static Field entityField, speedModifierField, stopDistanceField, areaSizeField;

    static
    {
        try
        {
            entityField = ReflectionTool.getField(EntityAIFollow.class, "field_192372_a", "entity");
            speedModifierField = ReflectionTool.getField(EntityAIFollow.class, "field_192375_d", "speedModifier");
            stopDistanceField = ReflectionTool.getField(EntityAIFollow.class, "field_192378_g", "stopDistance");
            areaSizeField = ReflectionTool.getField(EntityAIFollow.class, "field_192380_i", "areaSize");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().exitJava(121, false);
        }
    }


    private final EntityLiving searcher;
    private final Predicate<EntityLiving> followPredicate;
    private final double speedModifier;
    private final PathNavigate navigator;
    private final float stopDistance;
    private final float areaSize;
    private EntityLiving target;
    private Path path = null;
    private int timeToRecalcPath;
    private float oldWaterCost;

    public AIParrotFollowEdit(EntityAIFollow oldAI) throws IllegalAccessException
    {
        searcher = (EntityLiving) entityField.get(oldAI);
        speedModifier = (double) speedModifierField.get(oldAI);
        stopDistance = (float) stopDistanceField.get(oldAI);
        areaSize = (float) areaSizeField.get(oldAI);

        followPredicate = target -> target != null && searcher.getClass() != target.getClass();
        navigator = searcher.getNavigator();

        if (!(navigator instanceof PathNavigateGround) && !(navigator instanceof PathNavigateFlying))
        {
            throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
        }

        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute()
    {
        List<EntityLiving> list = searcher.world.<EntityLiving>getEntitiesWithinAABB(EntityLiving.class, searcher.getEntityBoundingBox().grow(areaSize), followPredicate);

        if (list.isEmpty()) return false;

        ExplicitPriorityQueue<EntityLiving> queue = new ExplicitPriorityQueue<>(list.size());
        for (EntityLiving entity : list)
        {
            queue.add(entity, entity.getDistanceSq(searcher));
        }

        target = queue.poll();
        while (target != null && !Sight.canSee(searcher, target)) //Doesn't need isSuitableTarget because it's not always used for attacking
        {
            target = queue.poll();
        }
        return target != null;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return Sight.canSee(searcher, target) && !navigator.noPath() && searcher.getDistanceSq(target) > Math.pow(stopDistance, 2);
    }

    @Override
    public void startExecuting()
    {
        timeToRecalcPath = 0;
        oldWaterCost = searcher.getPathPriority(PathNodeType.WATER);
        searcher.setPathPriority(PathNodeType.WATER, 0);
    }

    @Override
    public void resetTask()
    {
        target = null;
        searcher.setPathPriority(PathNodeType.WATER, oldWaterCost);
        if (path != null && path.equals(navigator.getPath())) navigator.clearPath();
        path = null;
    }

    @Override
    public void updateTask()
    {
        if (target != null && !searcher.getLeashed())
        {
            searcher.getLookHelper().setLookPositionWithEntity(target, 10, (float) searcher.getVerticalFaceSpeed());

            if (--timeToRecalcPath <= 0)
            {
                timeToRecalcPath = 10;
                double d0 = searcher.posX - target.posX;
                double d1 = searcher.posY - target.posY;
                double d2 = searcher.posZ - target.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                if (d3 > (double) (stopDistance * stopDistance))
                {
                    path = navigator.getPathToEntityLiving(target);
                    navigator.setPath(path, speedModifier);
                }
                else
                {
                    if (path != null && path.equals(navigator.getPath())) navigator.clearPath();
                    EntityLookHelper entitylookhelper = target.getLookHelper();

                    if (d3 <= (double) stopDistance || entitylookhelper.getLookPosX() == searcher.posX && entitylookhelper.getLookPosY() == searcher.posY && entitylookhelper.getLookPosZ() == searcher.posZ)
                    {
                        double d4 = target.posX - searcher.posX;
                        double d5 = target.posZ - searcher.posZ;
                        path = navigator.getPathToXYZ(searcher.posX - d4, searcher.posY, searcher.posZ - d5);
                        navigator.setPath(path, speedModifier);
                    }
                }
            }
        }
    }
}
