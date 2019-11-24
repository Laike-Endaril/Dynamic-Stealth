package com.fantasticsource.dynamicstealth.server.ai;

import com.fantasticsource.mctools.MCTools;
import net.minecraft.entity.EntityLivingBase;

import java.util.LinkedHashMap;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class EntityAIData
{
    private static LinkedHashMap<Class<? extends EntityLivingBase>, LinkedHashMap<String, Integer>> entityHeadTurnSpeeds;
    private static LinkedHashMap<Class<? extends EntityLivingBase>, LinkedHashMap<String, Integer>> entityFleePercentages;

    public static void update()
    {
        entityHeadTurnSpeeds = new LinkedHashMap<>();
        entityFleePercentages = new LinkedHashMap<>();

        MCTools.populateEntityIntMap(serverSettings.ai.y_entityOverrides.headTurnSpeed, entityHeadTurnSpeeds);
        MCTools.populateEntityIntMap(serverSettings.ai.y_entityOverrides.specificFleeThresholds, entityFleePercentages);
    }


    public static int headTurnSpeed(EntityLivingBase searcher)
    {
        return MCTools.entityMatchesIntMapOrDefault(searcher, entityHeadTurnSpeeds, serverSettings.ai.headTurnSpeed);
    }

    public static int fleeThreshold(EntityLivingBase searcher)
    {
        return MCTools.entityMatchesIntMapOrDefault(searcher, entityFleePercentages, serverSettings.ai.flee.threshold);
    }
}
