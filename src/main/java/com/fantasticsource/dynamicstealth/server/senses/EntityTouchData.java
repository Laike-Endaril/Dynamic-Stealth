package com.fantasticsource.dynamicstealth.server.senses;

import com.fantasticsource.mctools.MCTools;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.passive.EntityBat;
import net.minecraftforge.common.util.FakePlayer;

import java.util.HashSet;
import java.util.LinkedHashMap;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class EntityTouchData
{
    private static LinkedHashMap<Class<? extends EntityLivingBase>, HashSet<String>> unfeelingEntities;

    public static void update()
    {
        unfeelingEntities = new LinkedHashMap<>();

        MCTools.populateEntityMap(serverSettings.senses.touch.unfeelingEntities, unfeelingEntities);
    }

    public static boolean canFeelTouch(EntityLivingBase feeler)
    {
        if (feeler instanceof EntityArmorStand || feeler instanceof EntityBat || feeler instanceof FakePlayer) return false;

        return MCTools.entityMatchesMap(feeler, unfeelingEntities);
    }
}
