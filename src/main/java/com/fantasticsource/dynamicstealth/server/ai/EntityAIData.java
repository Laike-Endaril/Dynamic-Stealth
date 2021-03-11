package com.fantasticsource.dynamicstealth.server.ai;

import com.fantasticsource.mctools.MCTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.util.LinkedHashMap;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class EntityAIData
{
    private static LinkedHashMap<Class<? extends Entity>, LinkedHashMap<String, Integer>> entityHeadTurnSpeeds, entityFleePercentages;

    public static void update()
    {
        entityHeadTurnSpeeds = new LinkedHashMap<>();
        entityFleePercentages = new LinkedHashMap<>();

        MCTools.populateEntityIntMap(serverSettings.ai.y_entityOverrides.headTurnSpeed, entityHeadTurnSpeeds);
        MCTools.populateEntityIntMap(serverSettings.ai.y_entityOverrides.specificFleeThresholds, entityFleePercentages);
    }


    public static int headTurnSpeed(Entity entity)
    {
        if (!(entity instanceof EntityLivingBase)) return 0;

        return MCTools.entityMatchesIntMapOrDefault(entity, entityHeadTurnSpeeds, serverSettings.ai.headTurnSpeed);
    }

    public static int fleeThreshold(Entity entity)
    {
        if (!(entity instanceof EntityLivingBase)) return 0;

        return MCTools.entityMatchesIntMapOrDefault(entity, entityFleePercentages, serverSettings.ai.flee.threshold);
    }
}
