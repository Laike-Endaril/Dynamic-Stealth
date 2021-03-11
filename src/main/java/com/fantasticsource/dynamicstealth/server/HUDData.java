package com.fantasticsource.dynamicstealth.server;

import com.fantasticsource.mctools.MCTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.util.HashSet;
import java.util.LinkedHashMap;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class HUDData
{
    private static LinkedHashMap<Class<? extends Entity>, HashSet<String>> ungaugedEntities;

    public static void update()
    {
        ungaugedEntities = new LinkedHashMap<>();

        MCTools.populateEntityMap(serverSettings.hud.stealthGaugeBlacklist, ungaugedEntities);
    }

    public static boolean isGauged(Entity searcher)
    {
        if (!(searcher instanceof EntityLivingBase)) return false;

        return !MCTools.entityMatchesMap(searcher, ungaugedEntities);
    }
}
