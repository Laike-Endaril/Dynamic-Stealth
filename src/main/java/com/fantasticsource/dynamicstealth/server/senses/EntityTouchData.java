package com.fantasticsource.dynamicstealth.server.senses;

import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashSet;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class EntityTouchData
{
    private static HashSet<Class<? extends Entity>> unfeelingEntities;
    private static HashSet<Pair<Class<? extends Entity>, String>> unfeelingEntitiesNamed;

    public static void update()
    {
        unfeelingEntities = new HashSet<>();
        unfeelingEntitiesNamed = new HashSet<>();

        EntityEntry entry;

        for (String string : serverSettings.senses.touch.unfeelingEntities)
        {
            if (string.equals("player")) unfeelingEntities.add(EntityPlayerMP.class);
            else if (string.indexOf(":") != string.lastIndexOf(":"))
            {
                String[] tokens = string.split(":");
                entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(tokens[0], tokens[1]));
                if (entry == null) System.err.println("ResourceLocation for entity \"" + string + "\" not found!");
                else unfeelingEntitiesNamed.add(new Pair<>(entry.getEntityClass(), tokens[2]));
            }
            else
            {
                entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(string));
                if (entry == null) System.err.println("ResourceLocation for entity \"" + string + "\" not found!");
                else unfeelingEntities.add(entry.getEntityClass());
            }
        }
    }

    public static boolean canFeelTouch(Entity feeler)
    {
        if (feeler instanceof EntityArmorStand || feeler instanceof EntityBat || feeler instanceof FakePlayer) return false;

        for (Pair pair : unfeelingEntitiesNamed)
        {
            if (pair.getKey().equals(feeler.getClass()) && pair.getValue().equals(feeler.getName())) return true;
        }

        return !unfeelingEntities.contains(feeler.getClass());
    }
}
