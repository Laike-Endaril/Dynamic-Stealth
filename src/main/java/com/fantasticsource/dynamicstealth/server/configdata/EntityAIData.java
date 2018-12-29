package com.fantasticsource.dynamicstealth.server.configdata;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

import static com.fantasticsource.dynamicstealth.common.DynamicStealthConfig.serverSettings;

public class EntityAIData
{
    public static Map<Class<? extends Entity>, Integer> entityHeadTurnSpeeds = new HashMap<>();

    static
    {
        EntityEntry entry;
        String[] tokens;

        for (String string : serverSettings.ai.y_entityOverrides.headTurnSpeed)
        {
            tokens = string.split(",");
            if (tokens.length != 2) System.err.println("Wrong number of arguments for entity-specific head turn speed override; please check example in tooltip");
            else
            {
                entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(tokens[0].trim()));
                if (entry == null) System.err.println("ResourceLocation for entity \"" + string + "\" not found!");
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
