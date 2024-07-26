package com.fantasticsource.dynamicstealth.server;

import com.fantasticsource.dynamicstealth.server.senses.HidingData;
import com.fantasticsource.mctools.MCTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

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

    public static boolean isGauged(Entity target, Entity searcher)
    {
        if (!(searcher instanceof EntityLivingBase)) return false;
        if (searcher instanceof EntityPlayerMP)
        {
            if (((EntityPlayerMP) searcher).isCreative()) return false;
            if (target instanceof EntityPlayerMP && !HidingData.isHidingFrom((EntityPlayer) target, searcher.getUniqueID())) return false;
        }

        return !MCTools.entityMatchesMap(searcher, ungaugedEntities);
    }
}
