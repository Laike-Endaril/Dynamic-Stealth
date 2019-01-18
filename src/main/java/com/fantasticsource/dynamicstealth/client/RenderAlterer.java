package com.fantasticsource.dynamicstealth.client;

import com.fantasticsource.dynamicstealth.common.ClientData;
import com.fantasticsource.dynamicstealth.common.DynamicStealthConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class RenderAlterer
{
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
        if (ClientData.soulSight && !livingBase.isGlowing())
        {
            livingBase.setGlowing(true);
            soulSightCache.add(livingBase);
        }


        //Entity opacity based on visibility
        if (ClientData.usePlayerSenses && livingBase != Minecraft.getMinecraft().player)
        {
            int id = livingBase.getEntityId();
            double min = DynamicStealthConfig.clientSettings.z_otherSettings.mobOpacityMin;
            double range = ClientData.visibilityMap.containsKey(id) ? ClientData.visibilityMap.get(id) : 0;

            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            GlStateManager.enableCull();
            GlStateManager.cullFace(GlStateManager.CullFace.BACK);

            GlStateManager.color(1, 1, 1, (float) (min + (1d - min) * range));
        }
    }

    @SubscribeEvent
    public static void postRender(RenderLivingEvent.Post event)
    {
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableBlend();
    }
}
