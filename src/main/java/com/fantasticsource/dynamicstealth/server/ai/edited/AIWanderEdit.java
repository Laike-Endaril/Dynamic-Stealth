package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.dynamicstealth.server.ai.EntityAIData;
import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AIWanderEdit extends EntityAIWander
{
    protected static final Field ENTITY_AI_WANDER_EXECUTION_CHANCE_FIELD = ReflectionTool.getField(EntityAIWander.class, "field_179481_f", "executionChance");
    protected static final Method PATH_NAVIGATE_IS_DIRECT_PATH_BETWEEN_POINTS_METHOD = ReflectionTool.getMethod(PathNavigate.class, "func_75493_a", "isDirectPathBetweenPoints");

    protected final EntityCreature entity;
    public double speed;
    protected int executionChance;
    protected boolean mustUpdate;

    protected Path path;
    protected boolean looked;
    protected int lookIndex = -1;
    protected Vec3d lookVec = null;
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
            executionChance = 1; //TODO remove this
        }

        Vec3d vec3d = RandomPositionGenerator.findRandomTarget(entity, 10, 7);

        if (vec3d == null) return false;

        mustUpdate = false;

        path = entity.getNavigator().getPathToXYZ(vec3d.x, vec3d.y, vec3d.z);
        lookVec = null;
        looked = false;
        prevYaw = Float.MAX_VALUE;

        updateLookVec();

        return lookVec != null;
    }

    public boolean shouldContinueExecuting()
    {
        if (entity.getAttackTarget() != null) return false;

        return !looked || !entity.getNavigator().noPath();
    }

    public void startExecuting()
    {
        entity.getNavigator().clearPath();
    }

    @Override
    public void updateTask()
    {
        updateLookVec();
        entity.getLookHelper().setLookPosition(lookVec.x, lookVec.y, lookVec.z, EntityAIData.headTurnSpeed(entity), EntityAIData.headTurnSpeed(entity));

        if (!looked)
        {
            if (entity.getRotationYawHead() == prevYaw)
            {
                looked = true;
                entity.getNavigator().setPath(path, speed);
            }
            else
            {
                prevYaw = entity.getRotationYawHead();
            }
        }
    }

    @Override
    public void resetTask()
    {
        super.resetTask();

        path = null;
        lookIndex = -1;
        lookVec = null;
        looked = false;
        prevYaw = Float.MAX_VALUE;
    }

    protected void updateLookVec()
    {
        int w = MathHelper.ceil(entity.width);
        int h = MathHelper.ceil(entity.height);

        if (path != null)
        {
            for (int i = path.getCurrentPathIndex() + 1; i < path.getCurrentPathLength(); i++)
            {
                Vec3d vec = path.getVectorFromIndex(entity, i);
                if (vec.squareDistanceTo(entity.getPositionVector()) < 1)
                {
                    lookIndex = i;
                    lookVec = vec;

                    if (i + 1 < path.getCurrentPathLength())
                    {
                        lookIndex = i + 1;
                        lookVec = path.getVectorFromIndex(entity, i + 1);
                    }
                }
                else
                {
                    try
                    {
                        if ((boolean) PATH_NAVIGATE_IS_DIRECT_PATH_BETWEEN_POINTS_METHOD.invoke(entity.getNavigator(), entity.getPositionVector(), vec, w, h, w))
                        {
                            lookIndex = i;
                            lookVec = vec;
                        }
                    }
                    catch (IllegalAccessException | InvocationTargetException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
