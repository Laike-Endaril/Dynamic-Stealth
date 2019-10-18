package com.fantasticsource.dynamicstealth.server.ai;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class EntityAIData
{
    private static LinkedHashMap<Class<? extends Entity>, Integer> entityHeadTurnSpeeds;
    private static LinkedHashMap<Pair<Class<? extends Entity>, String>, Integer> entityHeadTurnSpeedsNamed;
    private static LinkedHashMap<Class<? extends EntityLivingBase>, HashSet<String>> fearlessEntities;

    public static void update()
    {
        entityHeadTurnSpeeds = new LinkedHashMap<>();
        entityHeadTurnSpeedsNamed = new LinkedHashMap<>();
        fearlessEntities = new LinkedHashMap<>();

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
                if (token.indexOf(":") != token.lastIndexOf(":"))
                {
                    String[] tokens2 = token.split(":");
                    entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(tokens2[0], tokens2[1]));
                    if (entry == null) System.err.println("ResourceLocation for entity \"" + token + "\" not found!");
                    else entityHeadTurnSpeedsNamed.put(new Pair<>(entry.getEntityClass(), tokens2[2]), Integer.parseInt(tokens[1].trim()));
                }
                else
                {
                    entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(token));
                    if (entry == null) System.err.println("ResourceLocation for entity \"" + token + "\" not found!");
                    else
                    {
                        entityHeadTurnSpeeds.put(entry.getEntityClass(), Integer.parseInt(tokens[1].trim()));
                    }
                }
            }
        }

        MCTools.populateEntityMap(serverSettings.ai.flee.fearless, fearlessEntities);
    }


    public static int headTurnSpeed(Entity searcher)
    {
        for (Map.Entry<Pair<Class<? extends Entity>, String>, Integer> entry : entityHeadTurnSpeedsNamed.entrySet())
        {
            Pair<Class<? extends Entity>, String> pair = entry.getKey();
            if (pair.getKey().equals(searcher.getClass()) && pair.getValue().equals(searcher.getName())) return entry.getValue();
        }

        Integer headTurnSpeed = entityHeadTurnSpeeds.get(searcher.getClass());
        return headTurnSpeed == null ? serverSettings.ai.headTurnSpeed : headTurnSpeed;
    }

    public static boolean isFearless(EntityLivingBase livingBase)
    {
        return MCTools.entityMatchesMap(livingBase, fearlessEntities);
    }
}
