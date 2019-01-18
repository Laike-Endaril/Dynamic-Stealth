package com.fantasticsource.dynamicstealth.client;

import com.fantasticsource.dynamicstealth.common.DynamicStealthConfig;
import com.fantasticsource.dynamicstealth.common.HUDData;
import net.minecraft.client.renderer.GlStateManager;
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
        //Don't draw seen entities as invisible, because they've been SEEN
        EntityLivingBase livingBase = event.getEntity();
        livingBase.setInvisible(false);


        //Soul sight glowing effect for when the player has soul sight
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


        HUDData.OnPointData data = HUDData.onPointDataMap.get(livingBase);
        if (data != null)
        {
            double min = DynamicStealthConfig.clientSettings.z_otherSettings.mobOpacityMin;
            GlStateManager.enableBlend();
            GlStateManager.color(1, 1, 1, (float) (min + (1 - min) * 1 /*TODO repace this 1 with visibility float/double*/));
        }
    }

    @SubscribeEvent
    public static void postRender(RenderLivingEvent.Post event)
    {
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableBlend();
    }
}
