package com.fantasticsource.dynamicstealth.server;

import com.fantasticsource.tools.datastructures.Pair;
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
    private static HashSet<Pair<Class<? extends Entity>, String>> ungaugedEntitiesNamed;

    public static void update()
    {
        ungaugedEntities = new HashSet<>();
        ungaugedEntitiesNamed = new HashSet<>();

        EntityEntry entry;

        for (String string : serverSettings.hud.stealthGaugeBlacklist)
        {
            if (string.equals("player")) ungaugedEntities.add(EntityPlayerMP.class);
            else if (string.indexOf(":") != string.lastIndexOf(":"))
            {
                String[] tokens = string.split(":");
                entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(tokens[0], tokens[1]));
                if (entry == null) System.err.println("ResourceLocation for entity \"" + string + "\" not found!");
                else ungaugedEntitiesNamed.add(new Pair<>(entry.getEntityClass(), tokens[2]));
            }
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
        for (Pair pair : ungaugedEntitiesNamed)
        {
            if (pair.getKey().equals(searcher.getClass()) && pair.getValue().equals(searcher.getName())) return true;
        }

        return !ungaugedEntities.contains(searcher.getClass());
    }
}
