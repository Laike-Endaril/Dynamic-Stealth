package com.fantasticsource.dynamicstealth.client;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import com.fantasticsource.dynamicstealth.compat.Compat;
import com.fantasticsource.dynamicstealth.compat.CompatNeat;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.datastructures.Color;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.Field;

import static com.fantasticsource.dynamicstealth.common.ClientData.*;
import static com.fantasticsource.dynamicstealth.common.DynamicStealthConfig.clientSettings;
import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX_LMAP_COLOR;
import static org.lwjgl.opengl.GL11.*;

public class HUD extends Gui
{
    private static final ResourceLocation ICON_LOCATION = new ResourceLocation(DynamicStealth.MODID, "indicator.png");
    private static final int TEX_SIZE = 32;
    private static final double UV_HALF_PIXEL = 0.5 / TEX_SIZE, UV_SUBTEX_SIZE = 0.5 - UV_HALF_PIXEL * 2;
    private static Field renderManagerRenderOutlinesField;

    static
    {
        initReflections();
    }

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
    public static void entityRender(RenderLivingEvent.Post event) throws IllegalAccessException
    {
        if (!((boolean) renderManagerRenderOutlinesField.get(event.getRenderer().getRenderManager())))
        {
            EntityLivingBase livingBase = event.getEntity();
            if (livingBase != null)
            {
                OnPointData data = onPointDataMap.get(livingBase.getEntityId());

                if (data != null && data.priority < clientSettings.threat.onPointHUDMax && onPointFilter(Minecraft.getMinecraft().player, livingBase, data.color, data.percent)) drawOnPointHUDElement(event.getRenderer().getRenderManager(), event.getX(), event.getY(), event.getZ(), livingBase, data.color, data.percent);
            }
        }
    }

    private static boolean onPointFilter(EntityPlayer player, EntityLivingBase livingBase, int color, int percent)
    {
        if (livingBase.getRidingEntity() == player || player.getRidingEntity() == livingBase) return false;

        if (percent == -1) return clientSettings.threat.filter.showBypass;
        if (color == COLOR_PASSIVE) return clientSettings.threat.filter.showPassive;
        if (color == COLOR_IDLE) return clientSettings.threat.filter.showIdle;
        if (color == COLOR_ALERT) return clientSettings.threat.filter.showAlert;
        if (color == COLOR_ATTACKING_YOU) return clientSettings.threat.filter.showAttackingYou;
        if (color == COLOR_ATTACKING_OTHER) return clientSettings.threat.filter.showAttackingOther;
        if (color == COLOR_FLEEING) return clientSettings.threat.filter.showFleeing;
        return false;
    }

    private static void drawOnPointHUDElement(RenderManager renderManager, double x, double y, double z, Entity entity, int color, int percent)
    {
        float viewerYaw = renderManager.playerViewY;
        float viewerPitch = renderManager.playerViewX;
        Color c = new Color(color, true);
        int r = c.r(), g = c.g(), b = c.b();

        boolean depth = clientSettings.threat.onPointHUDStyle.depth;
        double scale = clientSettings.threat.onPointHUDStyle.scale * 0.025;
        double halfSize2D = TEX_SIZE / 4D;
        double hOff2D = clientSettings.threat.onPointHUDStyle.horizontalOffset2D;
        double vOff2D = Compat.neat ? clientSettings.threat.onPointHUDStyle.verticalOffset2D - 11 : clientSettings.threat.onPointHUDStyle.verticalOffset2D;


        GlStateManager.disableLighting();

        if (depth)
        {
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
        }
        else
        {
            GlStateManager.disableDepth();
        }

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager.enableTexture2D();
        Minecraft.getMinecraft().renderEngine.bindTexture(ICON_LOCATION);

        GlStateManager.pushMatrix();

        if (Compat.neat)
        {
            GlStateManager.translate(x, y + entity.height * clientSettings.threat.onPointHUDStyle.verticalPercent + clientSettings.threat.onPointHUDStyle.verticalOffset - 0.5 + CompatNeat.heightAboveMob, z);
            GlStateManager.rotate(-viewerYaw, 0, 1, 0);
            GlStateManager.rotate(renderManager.options.thirdPersonView == 2 ? -viewerPitch : viewerPitch, 1, 0, 0);
            GlStateManager.translate(entity.width * clientSettings.threat.onPointHUDStyle.horizontalPercent, 0, 0);
            GlStateManager.scale(-scale, -scale, scale);
        }
        else if (Compat.customnpcs && entity.getClass().getName().equals("noppes.npcs.entity.EntityCustomNpc"))
        {
            double cnpcScale = entity.height / 1.8;
            GlStateManager.translate(x, y + entity.height * clientSettings.threat.onPointHUDStyle.verticalPercent + clientSettings.threat.onPointHUDStyle.verticalOffset - 0.5 - 0.108 * cnpcScale, z);
            GlStateManager.rotate(-viewerYaw, 0, 1, 0);
            GlStateManager.rotate(renderManager.options.thirdPersonView == 2 ? -viewerPitch : viewerPitch, 1, 0, 0);
            GlStateManager.translate(entity.width * clientSettings.threat.onPointHUDStyle.horizontalPercent, 0, 0);

            scale *= cnpcScale;
            GlStateManager.scale(-scale, -scale, scale);

            vOff2D -= 45;
        }
        else
        {
            GlStateManager.translate(x, y + entity.height * clientSettings.threat.onPointHUDStyle.verticalPercent - (clientSettings.threat.onPointHUDStyle.accountForSneak && entity.isSneaking() ? 0.25 : 0) + clientSettings.threat.onPointHUDStyle.verticalOffset, z);
            GlStateManager.rotate(-viewerYaw, 0, 1, 0);
            GlStateManager.rotate(renderManager.options.thirdPersonView == 2 ? -viewerPitch : viewerPitch, 1, 0, 0);
            GlStateManager.translate(entity.width * clientSettings.threat.onPointHUDStyle.horizontalPercent, 0, 0);
            GlStateManager.scale(-scale, -scale, scale);
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL_QUADS, POSITION_TEX_LMAP_COLOR);

        //Fill
        double left = -halfSize2D + hOff2D;
        double right = halfSize2D + hOff2D;
        double top = -halfSize2D + vOff2D;
        double bottom = halfSize2D + vOff2D;
        if (color == COLOR_PASSIVE || color == COLOR_IDLE || percent == -1)
        {
            //Fill for states that are always 100%
            bufferbuilder.pos(left, top, 0).tex(UV_HALF_PIXEL, UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(left, bottom, 0).tex(UV_HALF_PIXEL, 0.5 - UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(right, bottom, 0).tex(0.5 - UV_HALF_PIXEL, 0.5 - UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(right, top, 0).tex(0.5 - UV_HALF_PIXEL, UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
        }
        else
        {
            double amount = (double) percent / 100;
            double level = bottom - halfSize2D * 2 * amount;
            double uvLevel = 0.5 - UV_HALF_PIXEL - UV_SUBTEX_SIZE * amount;

            //Background fill
            bufferbuilder.pos(left, top, 0).tex(UV_HALF_PIXEL, UV_HALF_PIXEL).lightmap(15728880, 15728880).color(255, 255, 255, 255).endVertex();
            bufferbuilder.pos(left, level, 0).tex(UV_HALF_PIXEL, uvLevel).lightmap(15728880, 15728880).color(255, 255, 255, 255).endVertex();
            bufferbuilder.pos(right, level, 0).tex(0.5 - UV_HALF_PIXEL, uvLevel).lightmap(15728880, 15728880).color(255, 255, 255, 255).endVertex();
            bufferbuilder.pos(right, top, 0).tex(0.5 - UV_HALF_PIXEL, UV_HALF_PIXEL).lightmap(15728880, 15728880).color(255, 255, 255, 255).endVertex();

            //Threat level fill
            bufferbuilder.pos(left, level, 0).tex(UV_HALF_PIXEL, uvLevel).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(left, bottom, 0).tex(UV_HALF_PIXEL, 0.5 - UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(right, bottom, 0).tex(0.5 - UV_HALF_PIXEL, 0.5 - UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(right, level, 0).tex(0.5 - UV_HALF_PIXEL, uvLevel).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
        }

        //Outline and eyes
        if (color == COLOR_ATTACKING_YOU || color == COLOR_ALERT)
        {
            bufferbuilder.pos(left, top, 0).tex(UV_HALF_PIXEL, 0.5 + UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(left, bottom, 0).tex(UV_HALF_PIXEL, 1 - UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(right, bottom, 0).tex(0.5 - UV_HALF_PIXEL, 1 - UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(right, top, 0).tex(0.5 - UV_HALF_PIXEL, 0.5 + UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
        }
        else
        {
            bufferbuilder.pos(left, top, 0).tex(0.5 + UV_HALF_PIXEL, UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(left, bottom, 0).tex(0.5 + UV_HALF_PIXEL, 0.5 - UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(right, bottom, 0).tex(1 - UV_HALF_PIXEL, 0.5 - UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(right, top, 0).tex(1 - UV_HALF_PIXEL, UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
        }

        tessellator.draw();

        GlStateManager.popMatrix();

        GlStateManager.disableBlend();

        if (!depth) GlStateManager.enableDepth();

        GlStateManager.enableLighting();

        GlStateManager.color(1, 1, 1, 1);
    }

    private static void initReflections()
    {
        try
        {
            renderManagerRenderOutlinesField = ReflectionTool.getField(RenderManager.class, "field_178639_r", "field_188301_f", "renderOutlines");
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().exitJava(147, false);
        }
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
