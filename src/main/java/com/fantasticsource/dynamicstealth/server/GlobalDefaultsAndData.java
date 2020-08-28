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

        fullBypassDefaults.add("artifact:mimic");
        fullBypassDefaults.add("dimdoors:mob_monolith");
        fullBypassDefaults.add("iceandfire:deathworm");
        fullBypassDefaults.add("iceandfire:firedragon");
        fullBypassDefaults.add("iceandfire:gorgon");
        fullBypassDefaults.add("iceandfire:icedragon");
        fullBypassDefaults.add("iceandfire:troll");
        fullBypassDefaults.add("thaumcraft:cultistportalgreater");
        fullBypassDefaults.add("thaumcraft:cultistportallesser");
        fullBypassDefaults.add("thaumcraft:taintacle");
        fullBypassDefaults.add("thaumcraft:taintaclegaint");
        fullBypassDefaults.add("thaumcraft:taintacletiny");
        fullBypassDefaults.add("thaumcraft:taintseed");
        fullBypassDefaults.add("thaumcraft:taintseedprime");
        fullBypassDefaults.add("thaumcraft:taintswarm");
        fullBypassDefaults.add("thuttech:lift");
        fullBypassDefaults.add("mowziesmobs:foliaath");
        fullBypassDefaults.add("mowziesmobs:baby_foliaath");
        fullBypassDefaults.add("mowziesmobs:ferrous_wroughtnaut");
        fullBypassDefaults.add("mowziesmobs:barakoan_barakoana");
        fullBypassDefaults.add("mowziesmobs:barakoana");
        fullBypassDefaults.add("mowziesmobs:barakoaya");
        fullBypassDefaults.add("mowziesmobs:barakoan_player");
        fullBypassDefaults.add("mowziesmobs:barako");
        fullBypassDefaults.add("mowziesmobs:frostmaw");
        fullBypassDefaults.add("mowziesmobs:grottol");
        fullBypassDefaults.add("mowziesmobs:lantern");
        fullBypassDefaults.add("mowziesmobs:naga");
        fullBypassDefaults.add("mowziesmobs:sunstrike");
        fullBypassDefaults.add("mowziesmobs:solar_beam");
        fullBypassDefaults.add("mowziesmobs:boulder");
        fullBypassDefaults.add("mowziesmobs:axe_attack");
        fullBypassDefaults.add("mowziesmobs:icebreath");
        fullBypassDefaults.add("mowziesmobs:ice_ball");
        fullBypassDefaults.add("mowziesmobs:frozen_controller");
        fullBypassDefaults.add("mowziesmobs:ring");
        fullBypassDefaults.add("mowziesmobs:dart");
        fullBypassDefaults.add("mowziesmobs:poison_ball");
        fullBypassDefaults.add("mowziesmobs:super_nova");
        fullBypassDefaults.add("mowziesmobs:block_swapper");
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
