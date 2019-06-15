package com.fantasticsource.dynamicstealth.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashSet;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class HUDData
{
    private static HashSet<Class<? extends Entity>> ungaugedEntities;

    public static void update()
    {
        ungaugedEntities = new HashSet<>();

        EntityEntry entry;

        for (String string : serverSettings.hud.stealthGaugeBlacklist)
        {
            if (string.equals("player")) ungaugedEntities.add(EntityPlayerMP.class);
            else
            {
                entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(string));
                if (entry == null) System.err.println("ResourceLocation for entity \"" + string + "\" not found!");
                else ungaugedEntities.add(entry.getEntityClass());
            }
        }
    }

    public static boolean isGauged(Entity searcher)
    {
        return !ungaugedEntities.contains(searcher.getClass());
    }
}
