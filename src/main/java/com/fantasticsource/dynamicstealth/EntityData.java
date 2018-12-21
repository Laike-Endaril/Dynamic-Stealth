package com.fantasticsource.dynamicstealth;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.fantasticsource.dynamicstealth.DynamicStealthConfig.*;

public class EntityData
{
    private static int angleRange = e_angles.angleLarge - e_angles.angleSmall;
    private static int lightRange = c_lighting.lightHigh - c_lighting.lightLow;
    private static double speedRange = d_speeds.speedHigh - d_speeds.speedLow;
    private static int distanceRange = f_distances.distanceFar - f_distances.distanceNear;
    private static int distanceFarSquared = (int) Math.pow(f_distances.distanceFar, 2);

    public static ArrayList<Class<? extends Entity>> naturalNightvisionEntities = new ArrayList<>();
    public static Map<Class<? extends Entity>, Pair<Integer, Integer>> entityAngles = new HashMap<>();
    public static Map<Class<? extends Entity>, Pair<Integer, Integer>> entityDistances = new HashMap<>();
    public static Map<Class<? extends Entity>, Pair<Integer, Integer>> entityLighting = new HashMap<>();
    public static Map<Class<? extends Entity>, Pair<Double, Double>> entitySpeeds = new HashMap<>();

    static
    {
        EntityEntry entry;
        String[] tokens;

        for (String string : y_entityOverrides.naturalNightVisionMobs)
        {
            entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(string));
            if (entry == null) System.err.println("ResourceLocation for entity \"" + string + "\" not found!");
            else naturalNightvisionEntities.add(entry.getEntityClass());
        }

        for (String string : y_entityOverrides.angle)
        {
            tokens = string.split(",");
            if (tokens.length != 3) System.err.println("Wrong number of arguments for entity-specific angle override; please check example in tooltip");
            else
            {
                entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(tokens[0].trim()));
                if (entry == null) System.err.println("ResourceLocation for entity \"" + string + "\" not found!");
                else
                {
                    entityAngles.put(entry.getEntityClass(), new Pair<>(Integer.parseInt(tokens[1].trim()), Integer.parseInt(tokens[2].trim())));
                }
            }
        }

        for (String string : y_entityOverrides.distance)
        {
            tokens = string.split(",");
            if (tokens.length != 3) System.err.println("Wrong number of arguments for entity-specific distance override; please check example in tooltip");
            else
            {
                entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(tokens[0].trim()));
                if (entry == null) System.err.println("ResourceLocation for entity \"" + string + "\" not found!");
                else
                {
                    entityDistances.put(entry.getEntityClass(), new Pair<>(Integer.parseInt(tokens[1].trim()), Integer.parseInt(tokens[2].trim())));
                }
            }
        }

        for (String string : y_entityOverrides.lighting)
        {
            tokens = string.split(",");
            if (tokens.length != 3) System.err.println("Wrong number of arguments for entity-specific lighting override; please check example in tooltip");
            else
            {
                entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(tokens[0].trim()));
                if (entry == null) System.err.println("ResourceLocation for entity \"" + string + "\" not found!");
                else
                {
                    entityLighting.put(entry.getEntityClass(), new Pair<>(Integer.parseInt(tokens[1].trim()), Integer.parseInt(tokens[2].trim())));
                }
            }
        }

        for (String string : y_entityOverrides.speed)
        {
            tokens = string.split(",");
            if (tokens.length != 3) System.err.println("Wrong number of arguments for entity-specific speed override; please check example in tooltip");
            else
            {
                entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(tokens[0].trim()));
                if (entry == null) System.err.println("ResourceLocation for entity \"" + string + "\" not found!");
                else
                {
                    entitySpeeds.put(entry.getEntityClass(), new Pair<>(Double.parseDouble(tokens[1].trim()), Double.parseDouble(tokens[2].trim())));
                }
            }
        }
    }



    public static boolean naturalNightVision(Entity searcher)
    {
        return naturalNightvisionEntities.contains(searcher.getClass());
    }



    public static int angleLarge(Entity searcher)
    {
        Pair<Integer, Integer> pair = entityAngles.get(searcher.getClass());
        return pair == null ? e_angles.angleLarge : pair.getKey();
    }

    public static int angleSmall(Entity searcher)
    {
        Pair<Integer, Integer> pair = entityAngles.get(searcher.getClass());
        return pair == null ? e_angles.angleSmall : pair.getValue();
    }

    public static int angleRange(Entity searcher)
    {
        Pair<Integer, Integer> pair = entityAngles.get(searcher.getClass());
        return pair == null ? angleRange : pair.getKey() - pair.getValue();
    }



    public static int distanceFar(Entity searcher)
    {
        Pair<Integer, Integer> pair = entityDistances.get(searcher.getClass());
        return pair == null ? f_distances.distanceFar : pair.getKey();
    }

    public static int distanceNear(Entity searcher)
    {
        Pair<Integer, Integer> pair = entityDistances.get(searcher.getClass());
        return pair == null ? f_distances.distanceNear : pair.getValue();
    }

    public static int distanceRange(Entity searcher)
    {
        Pair<Integer, Integer> pair = entityDistances.get(searcher.getClass());
        return pair == null ? distanceRange : pair.getKey() - pair.getValue();
    }

    public static int distanceFarSquared(Entity searcher)
    {
        Pair<Integer, Integer> pair = entityDistances.get(searcher.getClass());
        return pair == null ? distanceFarSquared : (int) Math.pow(pair.getKey(), 2);
    }



    public static int lightHigh(Entity searcher)
    {
        Pair<Integer, Integer> pair = entityLighting.get(searcher.getClass());
        return pair == null ? c_lighting.lightHigh : pair.getKey();
    }

    public static int lightLow(Entity searcher)
    {
        Pair<Integer, Integer> pair = entityLighting.get(searcher.getClass());
        return pair == null ? c_lighting.lightLow : pair.getValue();
    }

    public static int lightRange(Entity searcher)
    {
        Pair<Integer, Integer> pair = entityLighting.get(searcher.getClass());
        return pair == null ? lightRange : pair.getKey() - pair.getValue();
    }



    public static double speedHigh(Entity searcher)
    {
        Pair<Double, Double> pair = entitySpeeds.get(searcher.getClass());
        return pair == null ? d_speeds.speedHigh : pair.getKey();
    }

    public static double speedLow(Entity searcher)
    {
        Pair<Double, Double> pair = entitySpeeds.get(searcher.getClass());
        return pair == null ? d_speeds.speedLow : pair.getValue();
    }

    public static double speedRange(Entity searcher)
    {
        Pair<Double, Double> pair = entitySpeeds.get(searcher.getClass());
        return pair == null ? speedRange : pair.getKey() - pair.getValue();
    }
}
