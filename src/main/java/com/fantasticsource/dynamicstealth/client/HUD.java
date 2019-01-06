package com.fantasticsource.dynamicstealth.client;

import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.opengl.GL11;

import static com.fantasticsource.dynamicstealth.common.DynamicStealthConfig.clientSettings;
import static com.fantasticsource.dynamicstealth.common.HUDData.*;

public class HUD extends Gui
{
    public HUD(Minecraft mc)
    {
        ScaledResolution sr = new ScaledResolution(mc);
        int width = sr.getScaledWidth();
        int height = sr.getScaledHeight();
        FontRenderer fontRender = mc.fontRenderer;

        drawDetailHUD(width, height, fontRender);

        GL11.glColor4f(1, 1, 1, 1);
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
    public static void entityRender(RenderLivingEvent.Specials.Post event)
    {
        EntityLivingBase livingBase = event.getEntity();
        Pair<Integer, Integer> data = onPointDataMap.get(livingBase.getEntityId());

        if (data != null) drawOnPointHUDElement(event.getRenderer().getRenderManager(), event.getX(), event.getY(), event.getZ(), livingBase, data.getKey(), data.getValue());
    }

    private static void drawOnPointHUDElement(RenderManager renderManager, double x, double y, double z, Entity entity, int color, int percent)
    {
        float viewerYaw = renderManager.playerViewY;
        float viewerPitch = renderManager.playerViewX;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.glNormal3f(0, 1, 0);
        GlStateManager.rotate(-viewerYaw, 0, 1, 0);
        GlStateManager.rotate((float) (renderManager.options.thirdPersonView == 2 ? -1 : 1) * viewerPitch, 1, 0, 0);
        GlStateManager.scale(-0.025, -0.025, 0.025);

        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);

        GlStateManager.disableDepth();

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double) (-50 - 1), -1, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        bufferbuilder.pos((double) (-50 - 1), 8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        bufferbuilder.pos((double) (50 + 1), 8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        bufferbuilder.pos((double) (50 + 1), -1, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();

//            fontRendererIn.drawString(str, -fontRendererIn.getStringWidth(str) / 2, 0, 553648127);
        GlStateManager.enableDepth();

        GlStateManager.depthMask(true);
//            fontRendererIn.drawString(str, -fontRendererIn.getStringWidth(str) / 2, 0, -1);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.popMatrix();
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
