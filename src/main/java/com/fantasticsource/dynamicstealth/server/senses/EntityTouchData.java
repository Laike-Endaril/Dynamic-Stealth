package com.fantasticsource.dynamicstealth.server.senses;

import com.fantasticsource.dynamicstealth.server.GlobalDefaultsAndData;
import com.fantasticsource.mctools.MCTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.passive.EntityBat;
import net.minecraftforge.common.util.FakePlayer;

import java.util.HashSet;
import java.util.LinkedHashMap;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class EntityTouchData
{
    private static LinkedHashMap<Class<? extends Entity>, HashSet<String>> unfeelingEntities;

    public static void update()
    {
        unfeelingEntities = new LinkedHashMap<>();

        MCTools.populateEntityMap(serverSettings.senses.touch.unfeelingEntities, unfeelingEntities);
    }

    public static boolean canFeelTouch(Entity entity)
    {
        if (!(entity instanceof EntityLivingBase)) return false;

        if (GlobalDefaultsAndData.isFullBypass(entity)) return false;
        if (entity instanceof EntityArmorStand || entity instanceof EntityBat || entity instanceof FakePlayer) return false;

        return !MCTools.entityMatchesMap(entity, unfeelingEntities);
    }
}
