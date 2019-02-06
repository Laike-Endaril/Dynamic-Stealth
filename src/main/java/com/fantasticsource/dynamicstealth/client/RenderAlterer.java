package com.fantasticsource.dynamicstealth.client;

import com.fantasticsource.dynamicstealth.common.ClientData;
import com.fantasticsource.dynamicstealth.compat.Compat;
import com.fantasticsource.dynamicstealth.config.DynamicStealthConfig;
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
        if (Compat.statues && livingBase.getClass().getName().contains("party.lemons.statue")) return;
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
            double min = DynamicStealthConfig.clientSettings.entityFading.mobOpacityMin;
            double visibility = ClientData.visibilityMap.containsKey(id) ? ClientData.visibilityMap.get(id) : 0;
            double maxOpacityAt = DynamicStealthConfig.clientSettings.entityFading.fullOpacityAt;
            if (visibility != 0)
            {
                if (maxOpacityAt == 0) visibility = 1;
                else visibility /= maxOpacityAt;
            }

            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            GlStateManager.enableCull();
            GlStateManager.cullFace(GlStateManager.CullFace.BACK);

            GlStateManager.color(1, 1, 1, (float) (min + (1d - min) * visibility));
        }
    }

    @SubscribeEvent
    public static void postRender(RenderLivingEvent.Post event)
    {
        if (Compat.statues && event.getEntity().getClass().getName().contains("party.lemons.statue")) return;
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableBlend();
    }
}
