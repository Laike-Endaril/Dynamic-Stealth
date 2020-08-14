package com.fantasticsource.dynamicstealth.compat;

import com.fantasticsource.dynamicstealth.server.GlobalDefaultsAndData;
import com.fantasticsource.dynamicstealth.server.ai.AIDynamicStealth;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;

import static com.fantasticsource.dynamicstealth.DynamicStealth.livingJoinWorld;

public class CompatCNPC
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void updateCNPC(LivingEvent.LivingUpdateEvent event)
    {
        EntityLivingBase livingBase = event.getEntityLiving();
        if (livingBase instanceof EntityLiving && !GlobalDefaultsAndData.isFullBypass(livingBase) && Compat.customnpcs && (NpcAPI.Instance().getIEntity(livingBase) instanceof ICustomNpc))
        {
            EntityLiving living = (EntityLiving) livingBase;
            if (AIDynamicStealth.getStealthAI(living) == null) livingJoinWorld(living);
        }
    }
}
