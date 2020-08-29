package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.dynamicstealth.server.ai.EntityAIData;
import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Field;

public class AIWanderEdit extends EntityAIWander
{
    protected static final Field ENTITY_AI_WANDER_EXECUTION_CHANCE_FIELD = ReflectionTool.getField(EntityAIWander.class, "field_179481_f", "executionChance");

    protected final EntityCreature entity;
    protected double x;
    protected double y;
    protected double z;
    public double speed;
    protected int executionChance;
    protected boolean mustUpdate;

    protected Path path;
    protected boolean looked;
    protected Vec3d targetVec;
    protected float prevYaw;

    public AIWanderEdit(EntityCreature entity, EntityAIWander oldAI)
    {
        super(entity, 1);

        this.setMutexBits(3);

        this.entity = entity;
        this.speed = oldAI.speed;
        try
        {
            executionChance = (int) ENTITY_AI_WANDER_EXECUTION_CHANCE_FIELD.get(oldAI);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }

        if (entity instanceof EntityElderGuardian) executionChance = 400;
    }

    public boolean shouldExecute()
    {
        if (entity.getAttackTarget() != null) return false;

        if (!mustUpdate)
        {
            if (entity.getIdleTime() >= 100 || entity.getRNG().nextInt(executionChance) != 0) return false;
        }

        Vec3d vec3d = RandomPositionGenerator.findRandomTarget(entity, 10, 7);

        if (vec3d == null) return false;
        else
        {
            x = vec3d.x;
            y = vec3d.y;
            z = vec3d.z;
            mustUpdate = false;
            return true;
        }
    }

    public boolean shouldContinueExecuting()
    {
        if (entity.getAttackTarget() != null) return false;

        return !looked || !entity.getNavigator().noPath();
    }

    public void startExecuting()
    {
        entity.getNavigator().clearPath();

        path = entity.getNavigator().getPathToXYZ(x, y, z);
        targetVec = null;
        looked = false;
        prevYaw = Float.MAX_VALUE;

        if (path != null)
        {
            for (int i = 0; i < path.getCurrentPathLength(); i++)
            {
                PathPoint pathPoint = path.getPathPointFromIndex(i);
                Vec3d tempVec = new Vec3d(pathPoint.x + 0.5, pathPoint.y, pathPoint.z + 0.5);
                if (entity.getPositionVector().squareDistanceTo(tempVec) >= 1)
                {
                    path.setCurrentPathIndex(i);
                    targetVec = tempVec;
                    break;
                }
            }
        }

        if (targetVec == null) path = null;
    }

    @Override
    public void updateTask()
    {
        //TODO edit this so that intermediate turns are smooth?
        if (!looked)
        {
            if (entity.getRotationYawHead() == prevYaw)
            {
                looked = true;
                entity.getNavigator().setPath(path, speed);
            }
            else
            {
                entity.getLookHelper().setLookPosition(targetVec.x, entity.posY + entity.getEyeHeight(), targetVec.z, EntityAIData.headTurnSpeed(entity), EntityAIData.headTurnSpeed(entity));
                prevYaw = entity.getRotationYawHead();
            }
        }
    }
}
