package com.fantasticsource.dynamicstealth.client;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import com.fantasticsource.dynamicstealth.compat.Compat;
import com.fantasticsource.dynamicstealth.compat.CompatNeat;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.fantasticsource.dynamicstealth.common.ClientData.*;
import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.clientSettings;
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
        try
        {
            renderManagerRenderOutlinesField = ReflectionTool.getField(RenderManager.class, "field_178639_r", "renderOutlines");
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            MCTools.crash(e, 147, false);
        }
    }

    private DecimalFormat oneDecimal = new DecimalFormat("0.0");

    public HUD(Minecraft mc)
    {
        drawHUD(mc);
        GlStateManager.color(1, 1, 1, 1);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void drawHUD(RenderGameOverlayEvent.Pre event)
    {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL)
        {
            new HUD(Minecraft.getMinecraft());
        }
    }

    @SubscribeEvent
    public static void entityRender(RenderLivingEvent.Post event) throws IllegalAccessException
    {
        if (!((boolean) renderManagerRenderOutlinesField.get(event.getRenderer().getRenderManager())))
        {
            EntityLivingBase livingBase = event.getEntity();
            if (livingBase != null)
            {
                int id = livingBase.getEntityId();

                if (detailData == null || id != detailData.searcherID)
                {
                    OnPointData data = opMap.get(id);
                    if (data != null && (data == detailData || onPointFilter(data.color)))
                    {
                        //Normal OPHUD
                        drawNormalOPHUD(event.getRenderer().getRenderManager(), event.getX(), event.getY(), event.getZ(), livingBase, data);
                    }
                }
            }
        }
    }

    private static boolean onPointFilter(int color)
    {
        if (color == COLOR_BYPASS) return clientSettings.hudSettings.filterOP.showBypass;
        else if (color == COLOR_PASSIVE) return clientSettings.hudSettings.filterOP.showPassive;
        else if (color == COLOR_IDLE) return clientSettings.hudSettings.filterOP.showIdle;
        else if (color == COLOR_ALERT) return clientSettings.hudSettings.filterOP.showAlert;
        else if (color == COLOR_ATTACKING_YOU) return clientSettings.hudSettings.filterOP.showAttackingYou;
        else if (color == COLOR_ATTACKING_OTHER) return clientSettings.hudSettings.filterOP.showAttackingOther;
        else if (color == COLOR_FLEEING) return clientSettings.hudSettings.filterOP.showFleeing;
        return false;
    }

    private static void drawNormalOPHUD(RenderManager renderManager, double x, double y, double z, Entity entity, OnPointData data)
    {
        float viewerYaw = renderManager.playerViewY; //"playerViewY" is LITERALLY the yaw...interpolated over the partialtick
        float viewerPitch = renderManager.playerViewX; //"playerViewX" is LITERALLY the pitch...interpolated over the partialtick
        int color = data.color;
        Color c = new Color(color, true);
        int r = c.r(), g = c.g(), b = c.b();

        boolean depth = clientSettings.hudSettings.styleOP.depth;
        double scale = clientSettings.hudSettings.styleOP.scale * 0.025;
        double halfSize2D = TEX_SIZE / 4D;
        double hOff2D = clientSettings.hudSettings.styleOP.horizontalOffset2D;
        double vOff2D = Compat.neat ? clientSettings.hudSettings.styleOP.verticalOffset2D - 11 : clientSettings.hudSettings.styleOP.verticalOffset2D;


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
            GlStateManager.translate(x, y + entity.height * clientSettings.hudSettings.styleOP.verticalPercent + clientSettings.hudSettings.styleOP.verticalOffset - 0.5 + CompatNeat.heightAboveMob, z);
            GlStateManager.rotate(-viewerYaw, 0, 1, 0);
            GlStateManager.rotate(renderManager.options.thirdPersonView == 2 ? -viewerPitch : viewerPitch, 1, 0, 0);
            GlStateManager.translate(entity.width * clientSettings.hudSettings.styleOP.horizontalPercent, 0, 0);
            GlStateManager.scale(-scale, -scale, scale);
        }
        else if (Compat.customnpcs && entity.getClass().getName().equals("noppes.npcs.entity.EntityCustomNpc"))
        {
            double cnpcScale = entity.height / 1.8;
            GlStateManager.translate(x, y + entity.height * clientSettings.hudSettings.styleOP.verticalPercent + clientSettings.hudSettings.styleOP.verticalOffset - 0.5 - 0.108 * cnpcScale, z);
            GlStateManager.rotate(-viewerYaw, 0, 1, 0);
            GlStateManager.rotate(renderManager.options.thirdPersonView == 2 ? -viewerPitch : viewerPitch, 1, 0, 0);
            GlStateManager.translate(entity.width * clientSettings.hudSettings.styleOP.horizontalPercent, 0, 0);

            scale *= cnpcScale;
            GlStateManager.scale(-scale, -scale, scale);

            vOff2D -= 45;
        }
        else
        {
            GlStateManager.translate(x, y + entity.height * clientSettings.hudSettings.styleOP.verticalPercent - (clientSettings.hudSettings.styleOP.accountForSneak && entity.isSneaking() ? 0.25 : 0) + clientSettings.hudSettings.styleOP.verticalOffset, z);
            GlStateManager.rotate(-viewerYaw, 0, 1, 0);
            GlStateManager.rotate(renderManager.options.thirdPersonView == 2 ? -viewerPitch : viewerPitch, 1, 0, 0);
            GlStateManager.translate(entity.width * clientSettings.hudSettings.styleOP.horizontalPercent, 0, 0);
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
        if (color == COLOR_PASSIVE || color == COLOR_IDLE || data.percent == -1)
        {
            //Fill for states that are always 100%
            bufferbuilder.pos(left, top, 0).tex(UV_HALF_PIXEL, UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(left, bottom, 0).tex(UV_HALF_PIXEL, 0.5 - UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(right, bottom, 0).tex(0.5 - UV_HALF_PIXEL, 0.5 - UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(right, top, 0).tex(0.5 - UV_HALF_PIXEL, UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
        }
        else
        {
            double amount = (double) data.percent / 100;
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
        if (color == COLOR_ATTACKING_YOU || color == COLOR_ALERT || color == COLOR_BYPASS)
        {
            //Angry, lit up eyes
            bufferbuilder.pos(left, top, 0).tex(UV_HALF_PIXEL, 0.5 + UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(left, bottom, 0).tex(UV_HALF_PIXEL, 1 - UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(right, bottom, 0).tex(0.5 - UV_HALF_PIXEL, 1 - UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(right, top, 0).tex(0.5 - UV_HALF_PIXEL, 0.5 + UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
        }
        else
        {
            //Normal, empty eyes
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

    public static boolean detailFilter(int color)
    {
        if (color == COLOR_BYPASS) return clientSettings.hudSettings.filterDetail.showBypass;
        else if (color == COLOR_PASSIVE) return clientSettings.hudSettings.filterDetail.showPassive;
        else if (color == COLOR_IDLE) return clientSettings.hudSettings.filterDetail.showIdle;
        else if (color == COLOR_ALERT) return clientSettings.hudSettings.filterDetail.showAlert;
        else if (color == COLOR_ATTACKING_YOU) return clientSettings.hudSettings.filterDetail.showAttackingYou;
        else if (color == COLOR_ATTACKING_OTHER) return clientSettings.hudSettings.filterDetail.showAttackingOther;
        else if (color == COLOR_FLEEING) return clientSettings.hudSettings.filterDetail.showFleeing;
        return false;
    }

    private void drawHUD(Minecraft mc)
    {
        //TODO main HUD

        //Detailed OPHUD
        if (detailData != null)
        {
            Entity entity = mc.player.world.getEntityByID(detailData.searcherID);
            if (entity != null) drawDetailedOPHUD(entity, mc.fontRenderer);
        }
    }

    public void drawDetailedOPHUD(Entity entity, FontRenderer fontRenderer)
    {
        try
        {
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();


            //General Setup
            float alpha = 0.6f;
            int color = detailData.color | ((int) (0xFF * alpha) << 24);

            Pair<Float, Float> pos = MCTools.getEntityXYInWindow(entity, 0, entity.height * 0.5, 0);
            float originX = pos.getKey(), originY = pos.getValue();
            int portW = MCTools.getViewportWidth();
            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            boolean offScreen = originX < 0 || originX > portW || originY < 0 || originY > MCTools.getViewportHeight();


            //Reticle (if any)
            if (!offScreen)
            {
                float originDrawX = originX / sr.getScaleFactor(), originDrawY = originY / sr.getScaleFactor();
                GlStateManager.color(1, 1, 1, 1);
                GlStateManager.glBegin(GL_LINES);
                GlStateManager.glVertex3f(originDrawX - 10, originDrawY, 0);
                GlStateManager.glVertex3f(originDrawX + 10, originDrawY, 0);
                GlStateManager.glVertex3f(originDrawX, originDrawY - 10, 0);
                GlStateManager.glVertex3f(originDrawX, originDrawY + 10, 0);
                GlStateManager.glEnd();
            }


            //Text setup
            int targetID = detailData.targetID;
            Entity target = (targetID == -1 || targetID == -2) ? null : entity.world.getEntityByID(targetID);

            float padding = 1;
            ArrayList<String> elements = new ArrayList<>();

            if (!Compat.neat) elements.add(entity.getName());
            if (targetID != -1 && targetID != -2) elements.add("Targeting " + (target == null ? UNKNOWN : target.getName()));
            else if (detailData.percent > 0) elements.add("Searching for target");
            if (detailData.color == COLOR_BYPASS) elements.add("Threat: §k000");
            else if (detailData.percent > 0) elements.add("Threat: " + detailData.percent + "%");
            elements.add("Distance: " + oneDecimal.format(entity.getDistance(Minecraft.getMinecraft().player)));

            float width = 0;
            for (String string : elements)
            {
                width = Tools.max(width, fontRenderer.getStringWidth(string));
            }
            float height = fontRenderer.FONT_HEIGHT * elements.size() + padding * (elements.size() - 1);


            //Main detailed OPHUD
            double offDist = entity.width / 2 + 0.6;
            float offX = 30;
            double scaledW = sr.getScaledWidth_double(), scaledH = sr.getScaledHeight_double();
            if (originX < portW >> 1)
            {
                pos = MCTools.getEntityXYInWindow(entity, offDist * -ActiveRenderInfo.getRotationX(), entity.height * 0.5, offDist * -ActiveRenderInfo.getRotationZ());
                float drawX = (pos.getKey() + offX) / sr.getScaleFactor(), drawY = pos.getValue() / sr.getScaleFactor();
                if (drawX + width + padding - 1 > scaledW) drawX = (float) scaledW - width - padding + 1;
                if (drawY - height / 2 - padding < 0) drawY = height / 2 + padding;
                else if (drawY + height / 2 + padding - 1 > scaledH) drawY = (float) scaledH - height / 2 - padding + 1;

                //TODO threat gauge

                //Text background
                GlStateManager.color(0, 0, 0, alpha);
                GlStateManager.glBegin(GL_QUADS);
                GlStateManager.glVertex3f(drawX - padding, drawY - height / 2 - padding, 0);
                GlStateManager.glVertex3f(drawX - padding, drawY + height / 2 + padding - 1, 0);
                GlStateManager.glVertex3f(drawX + width + padding - 1, drawY + height / 2 + padding - 1, 0);
                GlStateManager.glVertex3f(drawX + width + padding - 1, drawY - height / 2 - padding, 0);
                GlStateManager.glEnd();

                //Text elements
                GlStateManager.enableTexture2D();
                for (int i = 0; i < elements.size(); i++)
                {
                    fontRenderer.drawString(elements.get(i), drawX, drawY - height / 2 + height * i / elements.size(), color, false);
                }
            }
            else
            {
                pos = MCTools.getEntityXYInWindow(entity, offDist * ActiveRenderInfo.getRotationX(), entity.height * 0.5, offDist * ActiveRenderInfo.getRotationZ());
                float drawX = (pos.getKey() - offX) / sr.getScaleFactor(), drawY = pos.getValue() / sr.getScaleFactor();
                if (drawX - width - padding < 0) drawX = width + padding;
                if (drawY - height / 2 - padding < 0) drawY = height / 2 + padding;
                else if (drawY + height / 2 + padding - 1 > scaledH) drawY = (float) scaledH - height / 2 - padding + 1;

                //TODO threat gauge

                //Text background
                GlStateManager.color(0, 0, 0, alpha);
                GlStateManager.glBegin(GL_QUADS);
                GlStateManager.glVertex3f(drawX - width - padding, drawY - height / 2 - padding, 0);
                GlStateManager.glVertex3f(drawX - width - padding, drawY + height / 2 + padding - 1, 0);
                GlStateManager.glVertex3f(drawX + padding - 1, drawY + height / 2 + padding - 1, 0);
                GlStateManager.glVertex3f(drawX + padding - 1, drawY - height / 2 - padding, 0);
                GlStateManager.glEnd();

                //Text elements
                GlStateManager.enableTexture2D();
                for (int i = 0; i < elements.size(); i++)
                {
                    fontRenderer.drawString(elements.get(i), drawX - width, drawY - height / 2 + height * i / elements.size(), color, false);
                }
            }
        }
        catch (IllegalAccessException e)
        {
            MCTools.crash(e, 155, false);
        }
    }
}
