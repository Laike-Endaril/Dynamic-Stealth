package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class AIWanderAvoidWaterEdit extends AIWanderEdit
{
    protected static final Field ENTITY_AI_WANDER_AVOID_WATER_PROBABILITY_FIELD = ReflectionTool.getField(EntityAIWanderAvoidWater.class, "field_190865_h", "probability");

    protected float probability;

    public AIWanderAvoidWaterEdit(EntityCreature entity, EntityAIWanderAvoidWater oldAI)
    {
        super(entity, oldAI);
        try
        {
            probability = (float) ENTITY_AI_WANDER_AVOID_WATER_PROBABILITY_FIELD.get(oldAI);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    @Nullable
    protected Vec3d getPosition()
    {
        if (entity.isInWater())
        {
            Vec3d vec3d = RandomPositionGenerator.getLandPos(entity, 15, 7);
            return vec3d == null ? super.getPosition() : vec3d;
        }
        else
        {
            return entity.getRNG().nextFloat() >= probability ? RandomPositionGenerator.getLandPos(entity, 10, 7) : super.getPosition();
        }
    }
}
