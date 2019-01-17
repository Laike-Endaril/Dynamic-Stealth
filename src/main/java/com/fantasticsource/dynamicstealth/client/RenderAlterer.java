package com.fantasticsource.dynamicstealth.client;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class RenderAlterer
{
    public static boolean soulSight = false;
    private static ArrayList<EntityLivingBase> soulSightCache = new ArrayList<>();

    @SubscribeEvent
    public static void preRender(RenderLivingEvent.Pre event)
    {
        EntityLivingBase livingBase = event.getEntity();
        livingBase.setInvisible(false);

        if (soulSightCache.contains(livingBase))
        {
            soulSightCache.remove(livingBase);
            livingBase.setGlowing(false);
        }
        if (soulSight && !livingBase.isGlowing())
        {
            livingBase.setGlowing(true);
            soulSightCache.add(livingBase);
        }
    }
}
