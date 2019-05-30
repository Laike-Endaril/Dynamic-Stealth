package com.fantasticsource.dynamicstealth.server.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class EntityAIData
{
    private static LinkedHashMap<Class<? extends Entity>, Integer> entityHeadTurnSpeeds;
    private static ArrayList<Class<? extends EntityLivingBase>> isFearless;

    public static void update()
    {
        entityHeadTurnSpeeds = new LinkedHashMap<>();
        isFearless = new ArrayList<>();

        EntityEntry entry;
        String[] tokens;
        String token;

        for (String string : serverSettings.ai.y_entityOverrides.headTurnSpeed)
        {
            tokens = string.split(",");
            if (tokens.length != 2) System.err.println("Wrong number of arguments for entity-specific head turn speed override; please check example in tooltip");
            else
            {
                token = tokens[0].trim();
                entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(token));
                if (entry == null) System.err.println("ResourceLocation for entity \"" + token + "\" not found!");
                else
                {
                    entityHeadTurnSpeeds.put(entry.getEntityClass(), Integer.parseInt(tokens[1].trim()));
                }
            }
        }

        for (String string : serverSettings.ai.flee.fearless)
        {
            if (string.equals("player")) isFearless.add(EntityPlayerMP.class);
            else
            {
                entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(string));

                if (entry == null)
                {
                    if (!EntityAIDefaults.fearlessDefaults.contains(string)) System.err.println("ResourceLocation for entity \"" + string + "\" not found!");
                }
                else
                {
                    Class c = entry.getEntityClass();
                    if (EntityLivingBase.class.isAssignableFrom(c)) isFearless.add(c);
                    else System.err.println("Entity \"" + string + "\" does not extend EntityLivingBase!");
                }
            }
        }
    }


    public static int headTurnSpeed(Entity searcher)
    {
        Integer headTurnSpeed = entityHeadTurnSpeeds.get(searcher.getClass());
        return headTurnSpeed == null ? serverSettings.ai.headTurnSpeed : headTurnSpeed;
    }

    public static boolean isFearless(EntityLivingBase livingBase)
    {
        for (Class<? extends Entity> clss : isFearless)
        {
            if (livingBase.getClass() == clss) return true;
        }
        return false;
    }
}
