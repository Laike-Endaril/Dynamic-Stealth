package com.fantasticsource.dynamicstealth.server;

import com.fantasticsource.dynamicstealth.server.senses.sight.EntitySightDefaults;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashSet;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class GlobalDefaultsAndData
{
    public static ArrayList<String> fullBypassDefaults = new ArrayList<>();

    private static HashSet<Class<? extends EntityLivingBase>> fullBypassEntities;
    private static HashSet<Pair<Class<? extends EntityLivingBase>, String>> fullBypassEntitiesNamed;


    static
    {
        //Compat; these should be added absolutely, not conditionally

        fullBypassDefaults.add("mowziesmobs:ferrous_wroughtnaut");
        fullBypassDefaults.add("thuttech:lift");
    }


    public static void update()
    {
        fullBypassEntities = new HashSet<>();
        fullBypassEntitiesNamed = new HashSet<>();

        EntityEntry entry;

        for (String string : serverSettings.fullBypassEntities)
        {
            if (string.equals("player")) fullBypassEntities.add(EntityPlayerMP.class);
            else if (string.indexOf(":") != string.lastIndexOf(":"))
            {
                String[] tokens2 = string.split(":");
                entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(tokens2[0], tokens2[1]));
                if (entry == null) System.err.println("ResourceLocation for entity \"" + string + "\" not found!");
                else fullBypassEntitiesNamed.add(new Pair<>((Class<? extends EntityLivingBase>) entry.getEntityClass(), tokens2[2]));
            }
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
                    if (EntityLivingBase.class.isAssignableFrom(c)) fullBypassEntities.add(c);
                    else System.err.println("Entity \"" + string + "\" does not extend EntityLivingBase!");
                }
            }
        }
    }


    public static boolean isFullBypass(EntityLivingBase target)
    {
        for (Pair pair : fullBypassEntitiesNamed)
        {
            if (pair.getKey().equals(target.getClass()) && pair.getValue().equals(target.getName())) return true;
        }

        return fullBypassEntities.contains(target.getClass());
    }
}
