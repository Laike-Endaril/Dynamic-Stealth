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
    public static ArrayList<Class<? extends Entity>> isPassive = new ArrayList<>();
    public static ArrayList<Class<? extends Entity>> isNonPassive = new ArrayList<>();


    static
    {
        EntityEntry entry;
        String[] tokens;
        String token;
        int mode;

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

        for (String string : serverSettings.threat.y_entityOverrides.isPassive)
        {
            tokens = string.split(",");
            if (tokens.length != 2) System.err.println("Wrong number of arguments for entity-specific passivity override; please check example in tooltip");
            else
            {
                mode = 0;
                if (tokens[1].trim().equals("true")) mode = 1;
                else if (tokens[1].trim().equals("false")) mode = 2;
                else System.err.println("Second argument for entity-specific passivity override was not true or false; please check example in tooltip");

                if (mode > 0)
                {
                    token = tokens[0].trim();
                    if (token.equals("player"))
                    {
                        if (mode == 1) isPassive.add(EntityPlayerMP.class);
                        else isNonPassive.add(EntityPlayerMP.class);
                    }
                    else
                    {
                        entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(token));
                        if (entry == null)
                        {
                            if (!EntityThreatDefaults.passiveDefaults.contains(string)) System.err.println("ResourceLocation for entity \"" + token + "\" not found!");
                        }
                        else
                        {
                            if (mode == 1) isPassive.add(entry.getEntityClass());
                            else isNonPassive.add(entry.getEntityClass());
                        }
                    }
                }
            }
        }
    }
}
