package com.fantasticsource.dynamicstealth.server.senses.sight;

import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class EntitySightData
{
    public static int playerMaxSightDistance;

    public static ArrayList<EntityLivingBase> potionSoulSightEntities;
    public static ArrayList<EntityLivingBase> soulSightCache;

    private static ArrayList<Class<? extends EntityLivingBase>> naturallyBrightEntities;
    private static ArrayList<Class<? extends EntityLivingBase>> naturalNightvisionEntities;
    private static ArrayList<Class<? extends EntityLivingBase>> naturalSoulSightEntities;
    private static LinkedHashMap<Class<? extends EntityLivingBase>, Pair<Integer, Integer>> entityAngles;
    private static LinkedHashMap<Class<? extends EntityLivingBase>, Pair<Integer, Integer>> entityDistances;
    private static LinkedHashMap<Class<? extends EntityLivingBase>, SpecificLighting> entityLighting;

    public static void update()
    {
        playerMaxSightDistance = serverSettings.senses.sight.f_distances.distanceFar;

        potionSoulSightEntities = new ArrayList<>();
        soulSightCache = new ArrayList<>();

        naturallyBrightEntities = new ArrayList<>();
        naturalNightvisionEntities = new ArrayList<>();
        naturalSoulSightEntities = new ArrayList<>();
        entityAngles = new LinkedHashMap<>();
        entityDistances = new LinkedHashMap<>();
        entityLighting = new LinkedHashMap<>();

        EntityEntry entry;
        String[] tokens;
        String token;

        for (String string : serverSettings.senses.sight.y_entityOverrides.naturallyBrightEntities)
        {
            if (string.equals("player")) naturallyBrightEntities.add(EntityPlayerMP.class);
            else
            {
                entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(string));

                if (entry == null)
                {
                    if (!EntitySightDefaults.naturallyBrightDefaults.contains(string)) System.err.println("ResourceLocation for entity \"" + string + "\" not found!");
                }
                else
                {
                    Class c = entry.getEntityClass();
                    if (EntityLivingBase.class.isAssignableFrom(c)) naturallyBrightEntities.add(c);
                    else System.err.println("Entity \"" + string + "\" does not extend EntityLivingBase!");
                }
            }
        }

        for (String string : serverSettings.senses.sight.y_entityOverrides.naturalNightvisionMobs)
        {
            if (string.equals("player")) naturalNightvisionEntities.add(EntityPlayerMP.class);
            else
            {
                entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(string));
                if (entry == null) System.err.println("ResourceLocation for entity \"" + string + "\" not found!");
                else
                {
                    Class c = entry.getEntityClass();
                    if (EntityLivingBase.class.isAssignableFrom(c)) naturalNightvisionEntities.add(c);
                    else System.err.println("Entity \"" + string + "\" does not extend EntityLivingBase!");
                }
            }
        }

        for (String string : serverSettings.senses.sight.y_entityOverrides.naturalSoulSightMobs)
        {
            if (string.equals("player")) naturalSoulSightEntities.add(EntityPlayerMP.class);
            else
            {
                entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(string));
                if (entry == null) System.err.println("ResourceLocation for entity \"" + string + "\" not found!");
                else
                {
                    Class c = entry.getEntityClass();
                    if (EntityLivingBase.class.isAssignableFrom(c)) naturalSoulSightEntities.add(c);
                    else System.err.println("Entity \"" + string + "\" does not extend EntityLivingBase!");
                }
            }
        }

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
                    if (entry == null) System.err.println("ResourceLocation for entity \"" + token + "\" not found!");
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
                    if (entry == null) System.err.println("ResourceLocation for entity \"" + token + "\" not found!");
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

        if (serverSettings.senses.sight.e_angles.angleSmall > serverSettings.senses.sight.e_angles.angleLarge) throw new IllegalArgumentException("angleLarge must be greater than or equal to angleSmall");
        if (serverSettings.senses.sight.f_distances.distanceNear > serverSettings.senses.sight.f_distances.distanceFar) throw new IllegalArgumentException("distanceFar must be greater than or equal to distanceNear");
        if (serverSettings.senses.sight.c_lighting.lightLevelLow > serverSettings.senses.sight.c_lighting.lightLevelHigh) throw new IllegalArgumentException("lightLevelHigh must be greater than or equal to lightLevelLow");
    }


    public static boolean isBright(EntityLivingBase target)
    {
        return (serverSettings.senses.sight.g_absolutes.seeBurning && target.isBurning()) || naturallyBrightEntities.contains(target.getClass());
    }

    public static boolean hasNightvision(EntityLivingBase searcher)
    {
        return naturalNightvisionEntities.contains(searcher.getClass()) || searcher.getActivePotionEffect(MobEffects.NIGHT_VISION) != null;
    }

    public static boolean hasSoulSight(EntityLivingBase searcher)
    {
        return naturalSoulSightEntities.contains(searcher.getClass()) || potionSoulSightEntities.contains(searcher);
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
