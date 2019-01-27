package com.fantasticsource.dynamicstealth.server.senses;

import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.fantasticsource.dynamicstealth.common.DynamicStealthConfig.serverSettings;

public class EntityVisionData
{
    public static int playerMaxVisionDistance = serverSettings.senses.vision.f_distances.distanceFar;

    public static ArrayList<EntityLivingBase> potionSoulSightEntities = new ArrayList<>();
    public static ArrayList<EntityLivingBase> soulSightCache = new ArrayList<>();

    private static ArrayList<Class<? extends EntityLivingBase>> naturalNightvisionEntities = new ArrayList<>();
    private static ArrayList<Class<? extends EntityLivingBase>> naturalSoulSightEntities = new ArrayList<>();
    private static LinkedHashMap<Class<? extends EntityLivingBase>, Pair<Integer, Integer>> entityAngles = new LinkedHashMap<>();
    private static LinkedHashMap<Class<? extends EntityLivingBase>, Pair<Integer, Integer>> entityDistances = new LinkedHashMap<>();
    private static LinkedHashMap<Class<? extends EntityLivingBase>, Pair<Integer, Integer>> entityLighting = new LinkedHashMap<>();
    private static LinkedHashMap<Class<? extends EntityLivingBase>, Pair<Double, Double>> entitySpeeds = new LinkedHashMap<>();

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
                else
                {
                    Class c = entry.getEntityClass();
                    if (EntityLivingBase.class.isAssignableFrom(c)) naturalNightvisionEntities.add(c);
                    else System.err.println("Entity \"" + string + "\" does not extend EntityLivingBase!");
                }
            }
        }

        for (String string : serverSettings.senses.vision.y_entityOverrides.naturalSoulSightMobs)
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
                        Class c = entry.getEntityClass();
                        if (EntityLivingBase.class.isAssignableFrom(c)) entityAngles.put(c, new Pair<>(Integer.parseInt(tokens[1].trim()), Integer.parseInt(tokens[2].trim())));
                        else System.err.println("Entity \"" + string + "\" does not extend EntityLivingBase!");
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
                        Class c = entry.getEntityClass();
                        if (EntityLivingBase.class.isAssignableFrom(c)) entityDistances.put(c, new Pair<>(Integer.parseInt(tokens[1].trim()), Integer.parseInt(tokens[2].trim())));
                        else System.err.println("Entity \"" + string + "\" does not extend EntityLivingBase!");
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
                        Class c = entry.getEntityClass();
                        if (EntityLivingBase.class.isAssignableFrom(c)) entityLighting.put(c, new Pair<>(Integer.parseInt(tokens[1].trim()), Integer.parseInt(tokens[2].trim())));
                        else System.err.println("Entity \"" + string + "\" does not extend EntityLivingBase!");
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
                        Class c = entry.getEntityClass();
                        if (EntityLivingBase.class.isAssignableFrom(c)) entitySpeeds.put(c, new Pair<>(Double.parseDouble(tokens[1].trim()), Double.parseDouble(tokens[2].trim())));
                        else System.err.println("Entity \"" + string + "\" does not extend EntityLivingBase!");
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


    public static boolean hasNightVision(EntityLivingBase searcher)
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
        return pair == null ? serverSettings.senses.vision.e_angles.angleLarge : pair.getKey();
    }

    public static int angleSmall(EntityLivingBase searcher)
    {
        Pair<Integer, Integer> pair = entityAngles.get(searcher.getClass());
        return pair == null ? serverSettings.senses.vision.e_angles.angleSmall : pair.getValue();
    }


    public static int distanceFar(EntityLivingBase searcher)
    {
        Pair<Integer, Integer> pair = entityDistances.get(searcher.getClass());
        return pair == null ? serverSettings.senses.vision.f_distances.distanceFar : pair.getKey();
    }

    public static int distanceNear(EntityLivingBase searcher)
    {
        Pair<Integer, Integer> pair = entityDistances.get(searcher.getClass());
        return pair == null ? serverSettings.senses.vision.f_distances.distanceNear : pair.getValue();
    }


    public static int lightHigh(EntityLivingBase searcher)
    {
        Pair<Integer, Integer> pair = entityLighting.get(searcher.getClass());
        return pair == null ? serverSettings.senses.vision.c_lighting.lightHigh : pair.getKey();
    }

    public static int lightLow(EntityLivingBase searcher)
    {
        Pair<Integer, Integer> pair = entityLighting.get(searcher.getClass());
        return pair == null ? serverSettings.senses.vision.c_lighting.lightLow : pair.getValue();
    }


    public static double speedHigh(EntityLivingBase searcher)
    {
        Pair<Double, Double> pair = entitySpeeds.get(searcher.getClass());
        return pair == null ? serverSettings.senses.vision.d_speeds.speedHigh : pair.getKey();
    }

    public static double speedLow(EntityLivingBase searcher)
    {
        Pair<Double, Double> pair = entitySpeeds.get(searcher.getClass());
        return pair == null ? serverSettings.senses.vision.d_speeds.speedLow : pair.getValue();
    }
}
