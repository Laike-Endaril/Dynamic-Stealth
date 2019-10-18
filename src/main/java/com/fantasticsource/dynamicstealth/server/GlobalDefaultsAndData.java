package com.fantasticsource.dynamicstealth.server;

import com.fantasticsource.mctools.MCTools;
import net.minecraft.entity.EntityLivingBase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class GlobalDefaultsAndData
{
    public static ArrayList<String> fullBypassDefaults = new ArrayList<>();

    private static LinkedHashMap<Class<? extends EntityLivingBase>, HashSet<String>> fullBypassEntities;


    static
    {
        //Compat; these should be added absolutely, not conditionally

        fullBypassDefaults.add("mowziesmobs:ferrous_wroughtnaut");
        fullBypassDefaults.add("thuttech:lift");
    }


    public static void update()
    {
        fullBypassEntities = new LinkedHashMap<>();

        MCTools.populateEntityMap(serverSettings.fullBypassEntities, fullBypassEntities);
    }


    public static boolean isFullBypass(EntityLivingBase livingBase)
    {
        return MCTools.entityMatchesMap(livingBase, fullBypassEntities);
    }
}
