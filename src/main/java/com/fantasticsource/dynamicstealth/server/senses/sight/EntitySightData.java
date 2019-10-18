package com.fantasticsource.dynamicstealth.server.senses.sight;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashSet;
import java.util.LinkedHashMap;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class EntitySightData
{
    public static int playerMaxSightDistance;

    public static HashSet<EntityLivingBase> potionSoulSightEntities;

    public static LinkedHashMap<Integer, Integer> minimumDimensionLightLevels;

    private static LinkedHashMap<Class<? extends EntityLivingBase>, HashSet<String>> naturallyBrightEntities;
    private static LinkedHashMap<Class<? extends EntityLivingBase>, HashSet<String>> naturalNightvisionEntities;
    private static LinkedHashMap<Class<? extends EntityLivingBase>, HashSet<String>> naturalSoulSightEntities;

    private static LinkedHashMap<Class<? extends EntityLivingBase>, Pair<Integer, Integer>> entityAngles;
    private static LinkedHashMap<Class<? extends EntityLivingBase>, Pair<Integer, Integer>> entityDistances;
    private static LinkedHashMap<Class<? extends EntityLivingBase>, SpecificLighting> entityLighting;


    public static void update()
    {
        playerMaxSightDistance = serverSettings.senses.sight.f_distances.distanceFar;

        potionSoulSightEntities = new HashSet<>();

        naturallyBrightEntities = new LinkedHashMap<>();
        naturalNightvisionEntities = new LinkedHashMap<>();
        naturalSoulSightEntities = new LinkedHashMap<>();
        entityAngles = new LinkedHashMap<>();
        entityDistances = new LinkedHashMap<>();
        entityLighting = new LinkedHashMap<>();

        minimumDimensionLightLevels = new LinkedHashMap<>();

        EntityEntry entry;
        String[] tokens;
        String token;

        MCTools.populateEntityMap(serverSettings.senses.sight.y_entityOverrides.naturallyBrightEntities, naturallyBrightEntities);
        MCTools.populateEntityMap(serverSettings.senses.sight.y_entityOverrides.naturalNightvisionEntities, naturalNightvisionEntities);
        MCTools.populateEntityMap(serverSettings.senses.sight.y_entityOverrides.naturalSoulSightEntities, naturalSoulSightEntities);

        for (String string : serverSettings.senses.sight.y_entityOverrides.angle)
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
                    if (entry == null)
                    {
                        if (!EntitySightDefaults.angleDefaults.contains(string)) System.err.println("ResourceLocation for entity \"" + token + "\" not found!");
                    }
                    else
                    {
                        Class c = entry.getEntityClass();
                        if (EntityLivingBase.class.isAssignableFrom(c)) entityAngles.put(c, new Pair<>(Integer.parseInt(tokens[1].trim()), Integer.parseInt(tokens[2].trim())));
                        else System.err.println("Entity \"" + string + "\" does not extend EntityLivingBase!");
                    }
                }
            }
        }

        for (String string : serverSettings.senses.sight.y_entityOverrides.distance)
        {
            tokens = string.split(",");
            if (tokens.length != 3) System.err.println("Wrong number of arguments for entity-specific distance override; please check example in tooltip");
            else
            {
                token = tokens[0].trim();
                if (token.equals("player"))
                {
                    playerMaxSightDistance = Integer.parseInt(tokens[1].trim());
                    entityDistances.put(EntityPlayerMP.class, new Pair<>(playerMaxSightDistance, Integer.parseInt(tokens[2].trim())));
                }
                else
                {
                    entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(token));
                    if (entry == null)
                    {
                        if (!EntitySightDefaults.distanceDefaults.contains(string)) System.err.println("ResourceLocation for entity \"" + token + "\" not found!");
                    }
                    else
                    {
                        Class c = entry.getEntityClass();
                        if (EntityLivingBase.class.isAssignableFrom(c)) entityDistances.put(c, new Pair<>(Integer.parseInt(tokens[1].trim()), Integer.parseInt(tokens[2].trim())));
                        else System.err.println("Entity \"" + string + "\" does not extend EntityLivingBase!");
                    }
                }
            }
        }

        for (String string : serverSettings.senses.sight.y_entityOverrides.lighting)
        {
            tokens = string.split(",");
            if (tokens.length != 5) System.err.println("Wrong number of arguments for entity-specific lighting override; please check example in tooltip");
            else
            {
                token = tokens[0].trim();
                if (token.equals("player")) entityLighting.put(EntityPlayerMP.class, new SpecificLighting(Integer.parseInt(tokens[1].trim()), Double.parseDouble(tokens[2].trim()), Integer.parseInt(tokens[3].trim()), Double.parseDouble(tokens[4].trim())));
                else
                {
                    entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(token));
                    if (entry == null) System.err.println("ResourceLocation for entity \"" + token + "\" not found!");
                    else
                    {
                        Class c = entry.getEntityClass();
                        if (EntityLivingBase.class.isAssignableFrom(c)) entityLighting.put(c, new SpecificLighting(Integer.parseInt(tokens[1].trim()), Double.parseDouble(tokens[2].trim()), Integer.parseInt(tokens[3].trim()), Double.parseDouble(tokens[4].trim())));
                        else System.err.println("Entity \"" + string + "\" does not extend EntityLivingBase!");
                    }
                }
            }
        }

        for (String string : serverSettings.senses.sight.c_lighting.z_minimumDimensionLightLevels)
        {
            tokens = string.split(",");
            if (tokens.length != 2) System.err.println("Wrong number of arguments for minimum dimension light level; please check example in tooltip");
            else
            {
                int dim = Integer.parseInt(tokens[0].trim());
                int minLight = Integer.parseInt(tokens[1].trim());
                if (minLight < 0 || minLight > 15) System.err.println("Minimum light levels for dimensions must be from 0 to 15, inclusive!");
                else minimumDimensionLightLevels.put(dim, minLight);
            }
        }

        if (serverSettings.senses.sight.e_angles.angleSmall > serverSettings.senses.sight.e_angles.angleLarge) throw new IllegalArgumentException("angleLarge must be greater than or equal to angleSmall");
        if (serverSettings.senses.sight.f_distances.distanceNear > serverSettings.senses.sight.f_distances.distanceFar) throw new IllegalArgumentException("distanceFar must be greater than or equal to distanceNear");
        if (serverSettings.senses.sight.c_lighting.lightLevelLow > serverSettings.senses.sight.c_lighting.lightLevelHigh) throw new IllegalArgumentException("lightLevelHigh must be greater than or equal to lightLevelLow");
    }


    public static boolean isBright(EntityLivingBase target)
    {
        return MCTools.entityMatchesMap(target, naturallyBrightEntities);
    }

    public static boolean hasNightvision(EntityLivingBase searcher)
    {
        return MCTools.entityMatchesMap(searcher, naturalNightvisionEntities);
    }

    public static boolean hasSoulSight(EntityLivingBase searcher)
    {
        return MCTools.entityMatchesMap(searcher, naturalSoulSightEntities);
    }

    public static int angleLarge(EntityLivingBase searcher)
    {
        Pair<Integer, Integer> pair = entityAngles.get(searcher.getClass());
        return pair == null ? serverSettings.senses.sight.e_angles.angleLarge : pair.getKey();
    }

    public static int angleSmall(EntityLivingBase searcher)
    {
        Pair<Integer, Integer> pair = entityAngles.get(searcher.getClass());
        return pair == null ? serverSettings.senses.sight.e_angles.angleSmall : pair.getValue();
    }


    public static int distanceFar(EntityLivingBase searcher)
    {
        Pair<Integer, Integer> pair = entityDistances.get(searcher.getClass());
        return pair == null ? serverSettings.senses.sight.f_distances.distanceFar : pair.getKey();
    }

    public static int distanceNear(EntityLivingBase searcher)
    {
        Pair<Integer, Integer> pair = entityDistances.get(searcher.getClass());
        return pair == null ? serverSettings.senses.sight.f_distances.distanceNear : pair.getValue();
    }


    public static int lightLevelHigh(EntityLivingBase searcher)
    {
        SpecificLighting specificLighting = entityLighting.get(searcher.getClass());
        return specificLighting == null ? serverSettings.senses.sight.c_lighting.lightLevelHigh : specificLighting.levelHigh;
    }

    public static double lightMultHigh(EntityLivingBase searcher)
    {
        SpecificLighting specificLighting = entityLighting.get(searcher.getClass());
        return specificLighting == null ? serverSettings.senses.sight.c_lighting.lightMultHigh : specificLighting.multHigh;
    }

    public static int lightLevelLow(EntityLivingBase searcher)
    {
        SpecificLighting specificLighting = entityLighting.get(searcher.getClass());
        return specificLighting == null ? serverSettings.senses.sight.c_lighting.lightLevelLow : specificLighting.levelLow;
    }

    public static double lightMultLow(EntityLivingBase searcher)
    {
        SpecificLighting specificLighting = entityLighting.get(searcher.getClass());
        return specificLighting == null ? serverSettings.senses.sight.c_lighting.lightMultLow : specificLighting.multLow;
    }


    public static int minimumDimensionLight(int dim)
    {
        return minimumDimensionLightLevels.getOrDefault(dim, 0);
    }


    private static class SpecificLighting
    {
        int levelHigh, levelLow;
        double multHigh, multLow;

        public SpecificLighting(int levelHigh, double multHigh, int levelLow, double multLow)
        {
            this.levelHigh = levelHigh;
            this.multHigh = multHigh;
            this.levelLow = levelLow;
            this.multLow = multLow;
        }
    }
}
