package com.fantasticsource.dynamicstealth.client;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import com.fantasticsource.tools.datastructures.Color;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import static com.fantasticsource.dynamicstealth.common.DynamicStealthConfig.clientSettings;
import static com.fantasticsource.dynamicstealth.common.HUDData.*;
import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.*;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

public class HUD extends Gui
{
    private static final ResourceLocation ICON_LOCATION = new ResourceLocation(DynamicStealth.MODID, "indicator.png");
    private static final int TEX_SIZE = 32;

    private static final double UV_HALF_PIXEL = 0.5 / TEX_SIZE, UV_SUBTEX_SIZE = 0.5 - UV_HALF_PIXEL * 2;


    public HUD(Minecraft mc)
    {
        ScaledResolution sr = new ScaledResolution(mc);
        int width = sr.getScaledWidth();
        int height = sr.getScaledHeight();
        FontRenderer fontRender = mc.fontRenderer;

        drawDetailHUD(width, height, fontRender);

        GlStateManager.color(1, 1, 1, 1);
    }

    @SubscribeEvent
    public static void clearHUD(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        detailColor = COLOR_NULL;
        detailSearcher = EMPTY;
        detailTarget = EMPTY;
        detailPercent = 0;

        onPointDataMap.clear();
    }

    @SubscribeEvent
    public static void entityRender(RenderLivingEvent.Post event)
    {
        EntityLivingBase livingBase = event.getEntity();
        Pair<Integer, Integer> data = onPointDataMap.get(livingBase.getEntityId());

        if (data != null) drawOnPointHUDElement(event.getRenderer().getRenderManager(), event.getX(), event.getY(), event.getZ(), livingBase, data.getKey(), data.getValue());
    }

    private static void drawOnPointHUDElement(RenderManager renderManager, double x, double y, double z, Entity entity, int color, int percent)
    {
        float viewerYaw = renderManager.playerViewY;
        float viewerPitch = renderManager.playerViewX;
        Color c = new Color(color, true);
        int r = c.r(), g = c.g(), b = c.b();

        GlStateManager.disableLighting();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager.enableTexture2D();
        Minecraft.getMinecraft().renderEngine.bindTexture(ICON_LOCATION);

        GlStateManager.pushMatrix();

        GlStateManager.translate(x, y + entity.height / 2, z);
        GlStateManager.rotate(-viewerYaw, 0, 1, 0);
        GlStateManager.rotate((float) (renderManager.options.thirdPersonView == 2 ? -1 : 1) * viewerPitch, 1, 0, 0);
        GlStateManager.translate(entity.width * 1.415, 0, 0);
        GlStateManager.scale(-0.025, -0.025, 0.025);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL_QUADS, POSITION_TEX_COLOR);

        //Fill
        if (color == COLOR_PASSIVE || color == COLOR_IDLE || percent == -1)
        {
            //Fill for states that are always 100%
            bufferbuilder.pos(-8, -4, 0).tex(UV_HALF_PIXEL, UV_HALF_PIXEL).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(-8, 4, 0).tex(UV_HALF_PIXEL, 0.5 - UV_HALF_PIXEL).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(0, 4, 0).tex(0.5 - UV_HALF_PIXEL, 0.5 - UV_HALF_PIXEL).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(0, -4, 0).tex(0.5 - UV_HALF_PIXEL, UV_HALF_PIXEL).color(r, g, b, 255).endVertex();
        }
        else
        {
            double amount = (double) percent / 100;
            double level = 4D - 8D * amount;
            double uvLevel = 0.5 - UV_HALF_PIXEL - UV_SUBTEX_SIZE * amount;

            //Background fill
            bufferbuilder.pos(-8, -4, 0).tex(UV_HALF_PIXEL, UV_HALF_PIXEL).color(255, 255, 255, 255).endVertex();
            bufferbuilder.pos(-8, level, 0).tex(UV_HALF_PIXEL, uvLevel).color(255, 255, 255, 255).endVertex();
            bufferbuilder.pos(0, level, 0).tex(0.5 - UV_HALF_PIXEL, uvLevel).color(255, 255, 255, 255).endVertex();
            bufferbuilder.pos(0, -4, 0).tex(0.5 - UV_HALF_PIXEL, UV_HALF_PIXEL).color(255, 255, 255, 255).endVertex();

            //Threat level fill
            bufferbuilder.pos(-8, level, 0).tex(UV_HALF_PIXEL, uvLevel).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(-8, 4, 0).tex(UV_HALF_PIXEL, 0.5 - UV_HALF_PIXEL).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(0, 4, 0).tex(0.5 - UV_HALF_PIXEL, 0.5 - UV_HALF_PIXEL).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(0, level, 0).tex(0.5 - UV_HALF_PIXEL, uvLevel).color(r, g, b, 255).endVertex();
        }

        //Outline and eyes
        if (color == COLOR_ATTACKING_YOU || color == COLOR_ALERT)
        {
            bufferbuilder.pos(-8, -4, 0).tex(UV_HALF_PIXEL, 0.5 + UV_HALF_PIXEL).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(-8, 4, 0).tex(UV_HALF_PIXEL, 1 - UV_HALF_PIXEL).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(0, 4, 0).tex(0.5 - UV_HALF_PIXEL, 1 - UV_HALF_PIXEL).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(0, -4, 0).tex(0.5 - UV_HALF_PIXEL, 0.5 + UV_HALF_PIXEL).color(r, g, b, 255).endVertex();
        }
        else
        {
            bufferbuilder.pos(-8, -4, 0).tex(0.5 + UV_HALF_PIXEL, UV_HALF_PIXEL).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(-8, 4, 0).tex(0.5 + UV_HALF_PIXEL, 0.5 - UV_HALF_PIXEL).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(0, 4, 0).tex(1 - UV_HALF_PIXEL, 0.5 - UV_HALF_PIXEL).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(0, -4, 0).tex(1 - UV_HALF_PIXEL, UV_HALF_PIXEL).color(r, g, b, 255).endVertex();
        }

        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();

        GlStateManager.popMatrix();

        GlStateManager.color(1, 1, 1, 1);
    }

    private void drawDetailHUD(int width, int height, FontRenderer fontRender)
    {
        if (clientSettings.threat.displayDetailHUD)
        {
            if (detailSearcher.equals(EMPTY))
            {
                drawString(fontRender, EMPTY, (int) (width * 0.75), height - 30, detailColor);
                drawString(fontRender, EMPTY, (int) (width * 0.75), height - 20, detailColor);
                drawString(fontRender, EMPTY, (int) (width * 0.75), height - 10, detailColor);
            }
            else
            {
                if (detailPercent == -1) //Special code for threat bypass mode
                {
                    drawString(fontRender, detailSearcher, (int) (width * 0.75), height - 30, COLOR_ALERT);
                    drawString(fontRender, UNKNOWN, (int) (width * 0.75), height - 20, COLOR_ALERT);
                    drawString(fontRender, UNKNOWN, (int) (width * 0.75), height - 10, COLOR_ALERT);
                }
                else if (detailPercent == 0)
                {
                    drawString(fontRender, detailSearcher, (int) (width * 0.75), height - 30, detailColor);
                    drawString(fontRender, EMPTY, (int) (width * 0.75), height - 20, detailColor);
                    drawString(fontRender, EMPTY, (int) (width * 0.75), height - 10, detailColor);
                }
                else if (detailTarget.equals(EMPTY))
                {
                    drawString(fontRender, detailSearcher, (int) (width * 0.75), height - 30, detailColor);
                    drawString(fontRender, EMPTY, (int) (width * 0.75), height - 20, detailColor);
                    drawString(fontRender, detailPercent + "%", (int) (width * 0.75), height - 10, detailColor);
                }
                else
                {
                    drawString(fontRender, detailSearcher, (int) (width * 0.75), height - 30, detailColor);
                    drawString(fontRender, detailTarget, (int) (width * 0.75), height - 20, detailColor);
                    drawString(fontRender, detailPercent + "%", (int) (width * 0.75), height - 10, detailColor);
                }
            }
        }
    }
}
