package com.fantasticsource.dynamicstealth.client;

import com.fantasticsource.dynamicstealth.common.ClientData;
import com.fantasticsource.dynamicstealth.compat.Compat;
import com.fantasticsource.dynamicstealth.config.DynamicStealthConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class RenderAlterer
{
    private static ArrayList<EntityLivingBase> soulSightCache = new ArrayList<>();
    private static EntityLivingBase detailTarget = null;


    @SubscribeEvent
    public static void preRender(RenderLivingEvent.Pre event)
    {
        //Don't draw seen entities as invisible, because they've been SEEN
        EntityLivingBase livingBase = event.getEntity();
        if (Compat.statues && livingBase.getClass().getName().contains("party.lemons.statue")) return;
        livingBase.setInvisible(false);


        //Remove detail target and soul sight glowing effects; the glowing part of the render seems to be outside these events, which is why this is in pre and not post
        if (detailTarget != null)
        {
            detailTarget.setGlowing(false);
            detailTarget = null;
        }
        if (soulSightCache.contains(livingBase))
        {
            soulSightCache.remove(livingBase);
            livingBase.setGlowing(false);
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

            GlStateManager.color(1, 1, 1, 1);
            GL11.glColor4f(1, 1, 1, (float) (min + (1d - min) * visibility));
        }
    }

    @SubscribeEvent
    public static void postRender(RenderLivingEvent.Post event)
    {
        EntityLivingBase livingBase = event.getEntity();


        //Add detail target and soul sight glowing effects; the glowing part of the render seems to be outside these events, which is why this is in post and not pre
        if (ClientData.detailData != null && ClientData.detailData.searcherID == livingBase.getEntityId())
        {
            livingBase.setGlowing(true);
            detailTarget = livingBase;
        }
        else if (ClientData.soulSight && !livingBase.isGlowing())
        {
            livingBase.setGlowing(true);
            soulSightCache.add(livingBase);
        }


        if (Compat.statues && livingBase.getClass().getName().contains("party.lemons.statue")) return;
        GlStateManager.color(1, 1, 1, 1);
        GL11.glColor4f(1, 1, 1, 1);
        GlStateManager.disableBlend();
    }
}
