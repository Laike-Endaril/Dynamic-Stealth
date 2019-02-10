package com.fantasticsource.dynamicstealth.compat;

import com.fantasticsource.dynamicstealth.server.ai.AIDynamicStealth;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;

import static com.fantasticsource.dynamicstealth.common.DynamicStealth.livingJoinWorld;

public class CompatCNPC
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void updateCNPC(LivingEvent.LivingUpdateEvent event)
    {
        Entity entity = event.getEntity();
        if (entity instanceof EntityLiving)
        {
            EntityLiving living = (EntityLiving) entity;

            if (Compat.customnpcs && (NpcAPI.Instance().getIEntity(living) instanceof ICustomNpc))
            {
                if (AIDynamicStealth.getStealthAI(living) == null) livingJoinWorld(living);
            }
        }
    }
}
