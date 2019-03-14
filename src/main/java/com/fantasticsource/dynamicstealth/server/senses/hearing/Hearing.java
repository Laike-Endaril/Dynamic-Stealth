package com.fantasticsource.dynamicstealth.server.senses.hearing;

import com.fantasticsource.dynamicstealth.server.Attributes;
import com.fantasticsource.dynamicstealth.server.senses.sight.LOS;
import com.fantasticsource.mctools.WorldEventDistributor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class Hearing
{
    public static boolean canHear(EntityLivingBase helper, EntityLivingBase troubledOne, double baseRange)
    {
        if (troubledOne == null || helper == null) return false;
        if (troubledOne.world == null || troubledOne.world != helper.world) return false;
        if (troubledOne.getDistanceSq(helper) > Math.pow(baseRange * hearingRange(helper, troubledOne.getPositionVector().add(new Vec3d(0, troubledOne.getEyeHeight(), 0))), 2)) return false;

        return true;
    }

    public static double hearingRange(EntityLivingBase entity, Vec3d soundPos)
    {
        double range = entity.getEntityAttribute(Attributes.HEARING).getAttributeValue() / 100;
        if (LOS.rayTraceBlocks(entity.world, new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ), soundPos, true)) return range;
        return range * serverSettings.senses.hearing.noLOSMultiplier;
    }


    public static void checkSound(WorldEventDistributor.DSoundEvent event)
    {
        //TODO
    }
}
