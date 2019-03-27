package com.fantasticsource.dynamicstealth.server.ai;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.LinkedHashMap;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class EntityAIData
{
    public static LinkedHashMap<Class<? extends Entity>, Integer> entityHeadTurnSpeeds;

    public static void update()
    {
        entityHeadTurnSpeeds = new LinkedHashMap<>();

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
    }


    public static int headTurnSpeed(Entity searcher)
    {
        Integer headTurnSpeed = entityHeadTurnSpeeds.get(searcher.getClass());
        return headTurnSpeed == null ? serverSettings.ai.headTurnSpeed : headTurnSpeed;
    }
}
