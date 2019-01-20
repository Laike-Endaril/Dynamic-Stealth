package com.fantasticsource.dynamicstealth.server.senses;

import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.fantasticsource.dynamicstealth.common.DynamicStealthConfig.serverSettings;

public class EntityVisionData
{
    public static ArrayList<Class<? extends Entity>> naturalNightvisionEntities = new ArrayList<>();
    public static ArrayList<Class<? extends Entity>> naturalSoulSightEntities = new ArrayList<>();

    public static Map<Class<? extends Entity>, Pair<Integer, Integer>> entityAngles = new HashMap<>();
    public static Map<Class<? extends Entity>, Pair<Integer, Integer>> entityDistances = new HashMap<>();
    public static Map<Class<? extends Entity>, Pair<Integer, Integer>> entityLighting = new HashMap<>();
    public static Map<Class<? extends Entity>, Pair<Double, Double>> entitySpeeds = new HashMap<>();

    public static int playerMaxVisionDistance = serverSettings.senses.vision.f_distances.distanceFar;


    static
    {
        EntityEntry entry;
        String[] tokens;
        String token;

        for (String string : serverSettings.senses.vision.y_entityOverrides.naturalNightVisionMobs)
        {
            if (string.equals("player")) naturalNightvisionEntities.add(EntityPlayerMP.class);
            else
            {
                entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(string));
                if (entry == null) System.err.println("ResourceLocation for entity \"" + string + "\" not found!");
                else naturalNightvisionEntities.add(entry.getEntityClass());
            }
        }

        for (String string : serverSettings.senses.vision.y_entityOverrides.naturalSoulSightMobs)
        {
            if (string.equals("player")) naturalSoulSightEntities.add(EntityPlayerMP.class);
            else
            {
                entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(string));
                if (entry == null) System.err.println("ResourceLocation for entity \"" + string + "\" not found!");
                else naturalSoulSightEntities.add(entry.getEntityClass());
            }
        }

        for (String string : serverSettings.senses.vision.y_entityOverrides.angle)
        {
            tokens = string.split(",");
            if (tokens.length != 3) System.err.println("Wrong number of arguments for entity-specific angle override; please check example in tooltip");
            else
            {
                token = tokens[0].trim();
                if (token.equals("player")) entityAngles.put(EntityPlayerMP.class, new Pair<>(Integer.parseInt(tokens[1].trim()), Integer.parseInt(tokens[2].trim())));
                else
                {
                    entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(token));
                    if (entry == null) System.err.println("ResourceLocation for entity \"" + token + "\" not found!");
                    else
                    {
                        entityAngles.put(entry.getEntityClass(), new Pair<>(Integer.parseInt(tokens[1].trim()), Integer.parseInt(tokens[2].trim())));
                    }
                }
            }
        }

        for (String string : serverSettings.senses.vision.y_entityOverrides.distance)
        {
            tokens = string.split(",");
            if (tokens.length != 3) System.err.println("Wrong number of arguments for entity-specific distance override; please check example in tooltip");
            else
            {
                token = tokens[0].trim();
                if (token.equals("player"))
                {
                    playerMaxVisionDistance = Integer.parseInt(tokens[1].trim());
                    entityDistances.put(EntityPlayerMP.class, new Pair<>(playerMaxVisionDistance, Integer.parseInt(tokens[2].trim())));
                }
                else
                {
                    entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(token));
                    if (entry == null) System.err.println("ResourceLocation for entity \"" + token + "\" not found!");
                    else
                    {
                        entityDistances.put(entry.getEntityClass(), new Pair<>(Integer.parseInt(tokens[1].trim()), Integer.parseInt(tokens[2].trim())));
                    }
                }
            }
        }

        for (String string : serverSettings.senses.vision.y_entityOverrides.lighting)
        {
            tokens = string.split(",");
            if (tokens.length != 3) System.err.println("Wrong number of arguments for entity-specific lighting override; please check example in tooltip");
            else
            {
                token = tokens[0].trim();
                if (token.equals("player")) entityLighting.put(EntityPlayerMP.class, new Pair<>(Integer.parseInt(tokens[1].trim()), Integer.parseInt(tokens[2].trim())));
                else
                {
                    entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(token));
                    if (entry == null) System.err.println("ResourceLocation for entity \"" + token + "\" not found!");
                    else
                    {
                        entityLighting.put(entry.getEntityClass(), new Pair<>(Integer.parseInt(tokens[1].trim()), Integer.parseInt(tokens[2].trim())));
                    }
                }
            }
        }

        for (String string : serverSettings.senses.vision.y_entityOverrides.speed)
        {
            tokens = string.split(",");
            if (tokens.length != 3) System.err.println("Wrong number of arguments for entity-specific speed override; please check example in tooltip");
            else
            {
                token = tokens[0].trim();
                if (token.equals("player")) entitySpeeds.put(EntityPlayerMP.class, new Pair<>(Double.parseDouble(tokens[1].trim()), Double.parseDouble(tokens[2].trim())));
                else
                {
                    entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(token));
                    if (entry == null) System.err.println("ResourceLocation for entity \"" + token + "\" not found!");
                    else
                    {
                        entitySpeeds.put(entry.getEntityClass(), new Pair<>(Double.parseDouble(tokens[1].trim()), Double.parseDouble(tokens[2].trim())));
                    }
                }
            }
        }
    }

    public static void postInit(FMLPostInitializationEvent event)
    {
        update();
    }

    public static void postConfig(ConfigChangedEvent.PostConfigChangedEvent event)
    {
        update();
    }

    private static void update()
    {
        if (serverSettings.senses.vision.e_angles.angleSmall > serverSettings.senses.vision.e_angles.angleLarge) throw new IllegalArgumentException("angleLarge must be greater than or equal to angleSmall");
        if (serverSettings.senses.vision.f_distances.distanceNear > serverSettings.senses.vision.f_distances.distanceFar) throw new IllegalArgumentException("distanceFar must be greater than or equal to distanceNear");
        if (serverSettings.senses.vision.c_lighting.lightLow > serverSettings.senses.vision.c_lighting.lightHigh) throw new IllegalArgumentException("lightHigh must be greater than or equal to lightLow");
        if (serverSettings.senses.vision.d_speeds.speedLow > serverSettings.senses.vision.d_speeds.speedHigh) throw new IllegalArgumentException("speedHigh must be greater than or equal to speedLow");
    }


    public static boolean naturalNightVision(Entity searcher)
    {
        return naturalNightvisionEntities.contains(searcher.getClass());
    }

    public static boolean naturalSoulSight(Entity searcher)
    {
        return naturalSoulSightEntities.contains(searcher.getClass());
    }


    public static int angleLarge(Entity searcher)
    {
        Pair<Integer, Integer> pair = entityAngles.get(searcher.getClass());
        return pair == null ? serverSettings.senses.vision.e_angles.angleLarge : pair.getKey();
    }

    public static int angleSmall(Entity searcher)
    {
        Pair<Integer, Integer> pair = entityAngles.get(searcher.getClass());
        return pair == null ? serverSettings.senses.vision.e_angles.angleSmall : pair.getValue();
    }


    public static int distanceFar(Entity searcher)
    {
        Pair<Integer, Integer> pair = entityDistances.get(searcher.getClass());
        return pair == null ? serverSettings.senses.vision.f_distances.distanceFar : pair.getKey();
    }

    public static int distanceNear(Entity searcher)
    {
        Pair<Integer, Integer> pair = entityDistances.get(searcher.getClass());
        return pair == null ? serverSettings.senses.vision.f_distances.distanceNear : pair.getValue();
    }


    public static int lightHigh(Entity searcher)
    {
        Pair<Integer, Integer> pair = entityLighting.get(searcher.getClass());
        return pair == null ? serverSettings.senses.vision.c_lighting.lightHigh : pair.getKey();
    }

    public static int lightLow(Entity searcher)
    {
        Pair<Integer, Integer> pair = entityLighting.get(searcher.getClass());
        return pair == null ? serverSettings.senses.vision.c_lighting.lightLow : pair.getValue();
    }


    public static double speedHigh(Entity searcher)
    {
        Pair<Double, Double> pair = entitySpeeds.get(searcher.getClass());
        return pair == null ? serverSettings.senses.vision.d_speeds.speedHigh : pair.getKey();
    }

    public static double speedLow(Entity searcher)
    {
        Pair<Double, Double> pair = entitySpeeds.get(searcher.getClass());
        return pair == null ? serverSettings.senses.vision.d_speeds.speedLow : pair.getValue();
    }
}
