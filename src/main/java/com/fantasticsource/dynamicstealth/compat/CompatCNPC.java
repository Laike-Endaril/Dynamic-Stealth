package com.fantasticsource.dynamicstealth.compat;

import com.fantasticsource.dynamicstealth.server.ai.AIStealthTargetingAndSearch;
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
        if (entity instanceof EntityLiving)
        {
            EntityLiving living = (EntityLiving) entity;
            if (AIStealthTargetingAndSearch.getStealthAI(living) == null) entityJoinWorldInit(living);
        }
    }
}
