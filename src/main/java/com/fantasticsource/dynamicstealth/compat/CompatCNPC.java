package com.fantasticsource.dynamicstealth.compat;

import com.fantasticsource.dynamicstealth.server.ai.AIDynamicStealth;
import com.fantasticsource.mctools.MCTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.fantasticsource.dynamicstealth.common.DynamicStealth.entityJoinWorldInit;

public class CompatCNPC
{
    @SubscribeEvent
    public static void livingUpdate(LivingEvent.LivingUpdateEvent event) throws Exception
    {
        Entity entity = event.getEntity();
        if (entity instanceof EntityLiving && !MCTools.isClient(entity.world))
        {
            EntityLiving living = (EntityLiving) entity;
            if (AIDynamicStealth.getStealthAI(living) == null) entityJoinWorldInit(living);
        }
    }
}
