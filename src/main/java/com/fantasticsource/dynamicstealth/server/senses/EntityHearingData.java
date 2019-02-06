package com.fantasticsource.dynamicstealth.server.senses;

import com.fantasticsource.dynamicstealth.server.Attributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class EntityHearingData
{
    public static double hearingRange(EntityLivingBase entity, Vec3d soundPos)
    {
        double range = entity.getEntityAttribute(Attributes.HEARING).getAttributeValue() / 100;
        if (LOS.rayTraceBlocks(entity.world, new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ), soundPos, true)) return range;
        return range * serverSettings.senses.hearing.noLOSMultiplier;
    }
}
