package com.fantasticsource.dynamicstealth.server.senses;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class EntityTouchData
{
    private static ArrayList<Class<? extends Entity>> unfeelingEntities = new ArrayList<>();

    static
    {
        EntityEntry entry;

        for (String string : serverSettings.senses.touch.unfeelingEntities)
        {
            if (string.equals("player")) unfeelingEntities.add(EntityPlayerMP.class);
            else
            {
                entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(string));
                if (entry == null) System.err.println("ResourceLocation for entity \"" + string + "\" not found!");
                else unfeelingEntities.add(entry.getEntityClass());
            }
        }
    }

    public static boolean canFeel(Entity feeler)
    {
        return !unfeelingEntities.contains(feeler.getClass());
    }
}
