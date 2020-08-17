package com.fantasticsource.dynamicstealth.server.senses.hearing;

import com.fantasticsource.mctools.ImprovedRayTracing;
import com.fantasticsource.mctools.MCTools;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;
import static com.fantasticsource.dynamicstealth.server.Attributes.HEARING;

public class Hearing
{
    public static boolean canHear(EntityLivingBase helper, EntityLivingBase troubledOne, double baseRange)
    {
        if (troubledOne == null || helper == null) return false;
        if (troubledOne.world == null || troubledOne.world != helper.world) return false;
        if (troubledOne.getDistanceSq(helper) > Math.pow(baseRange * hearingRangePercentage(helper, troubledOne.getPositionVector().add(new Vec3d(0, troubledOne.getEyeHeight(), 0))), 2)) return false;

        return true;
    }

    public static double hearingRangePercentage(EntityLivingBase entity, Vec3d soundPos)
    {
        double range = MCTools.getAttribute(entity, HEARING);
        double percent = range * 0.01;
        if (ImprovedRayTracing.isUnobstructed(entity.world, new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ), soundPos, range, true)) return percent;
        return percent * serverSettings.senses.hearing.noLOSMultiplier;
    }
}
