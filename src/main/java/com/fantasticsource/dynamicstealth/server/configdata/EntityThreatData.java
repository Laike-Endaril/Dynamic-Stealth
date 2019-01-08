package com.fantasticsource.dynamicstealth.server.configdata;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;

import static com.fantasticsource.dynamicstealth.common.DynamicStealthConfig.serverSettings;

public class EntityThreatData
{
    public static ArrayList<Class<? extends Entity>> threatBypass = new ArrayList<>();


    static
    {
        EntityEntry entry;

        for (String string : serverSettings.threat.y_entityOverrides.threatBypass)
        {
            if (string.equals("player")) threatBypass.add(EntityPlayerMP.class);
            else
            {
                entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(string));

                if (entry == null)
                {
                    if (!EntityThreatDefaults.threatBypassDefaults.contains(string)) System.err.println("ResourceLocation for entity \"" + string + "\" not found!");
                }
                else
                {
                    threatBypass.add(entry.getEntityClass());
                }
            }
        }
    }
}
