package com.fantasticsource.dynamicstealth.server;

import com.fantasticsource.mctools.MCTools;
import net.minecraft.entity.EntityLivingBase;

import java.util.HashSet;
import java.util.LinkedHashMap;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class HUDData
{
    private static LinkedHashMap<Class<? extends EntityLivingBase>, HashSet<String>> ungaugedEntities;

    public static void update()
    {
        ungaugedEntities = new LinkedHashMap<>();

        MCTools.populateEntityMap(serverSettings.hud.stealthGaugeBlacklist, ungaugedEntities);
    }

    public static boolean isGauged(EntityLivingBase searcher)
    {
        return MCTools.entityMatchesMap(searcher, ungaugedEntities);
    }
}
