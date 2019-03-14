package com.fantasticsource.dynamicstealth.client;

import com.fantasticsource.dynamicstealth.common.ClientData;
import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import com.fantasticsource.dynamicstealth.compat.Compat;
import com.fantasticsource.dynamicstealth.compat.CompatNeat;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.Render;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
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
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.fantasticsource.dynamicstealth.common.ClientData.*;
import static com.fantasticsource.dynamicstealth.common.DynamicStealth.TRIG_TABLE;
import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.clientSettings;
import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX_LMAP_COLOR;
import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class HUD extends Gui
{
    private static final ResourceLocation BASIC_GAUGE_TEXTURE = new ResourceLocation(DynamicStealth.MODID, "image/basicgauge.png");
    private static final int BASIC_GAUGE_SIZE = 32;
    private static final double BASIC_GAUGE_UV_HALF_PIXEL = 0.5 / BASIC_GAUGE_SIZE, BASIC_GAUGE_UV_SUBTEX_SIZE = 0.5 - BASIC_GAUGE_UV_HALF_PIXEL * 2;

    private static final ResourceLocation ARROW_TEXTURE = new ResourceLocation(DynamicStealth.MODID, "image/arrow.png");
    private static final float ARROW_WIDTH = 152, ARROW_HEIGHT = 87;
    private static final float ARROW_UV_HALF_PIXEL_W = 0.5f / ARROW_WIDTH, ARROW_UV_HALF_PIXEL_H = 0.5f / ARROW_HEIGHT;
    private static final float ARROW_ORIGIN_X = 151, ARROW_ORIGIN_Y = 43;
    private static final float ARROW_LEFT = ARROW_ORIGIN_X, ARROW_RIGHT = ARROW_WIDTH - ARROW_ORIGIN_X;
    private static final float ARROW_ABOVE = ARROW_ORIGIN_Y, ARROW_BELOW = ARROW_HEIGHT - ARROW_ORIGIN_Y;
    private static final float ARROW_CENTER_ORIGIN_X = 45;
    private static final float ARROW_CENTER_LEFT = ARROW_CENTER_ORIGIN_X, ARROW_CENTER_RIGHT = ARROW_WIDTH - ARROW_CENTER_ORIGIN_X;

    private static final ResourceLocation STEALTH_GAUGE_TEXTURE = new ResourceLocation(DynamicStealth.MODID, "image/stealthgauge.png");
    private static final int STEALTH_GAUGE_SIZE = 128;
    private static final float STEALTH_GAUGE_UV_HALF_PIXEL = 0.5f / STEALTH_GAUGE_SIZE;

    private static final ResourceLocation STEALTH_GAUGE_RIM_TEXTURE = new ResourceLocation(DynamicStealth.MODID, "image/stealthgaugerim.png");
    private static final int STEALTH_GAUGE_RIM_SIZE = 256;
    private static final float STEALTH_GAUGE_RIM_UV_HALF_PIXEL = 0.5f / STEALTH_GAUGE_RIM_SIZE;

    private static Field renderManagerRenderOutlinesField;
    private static TextureManager textureManager = Minecraft.getMinecraft().renderEngine;

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

                OnPointData data = opMap.get(id);
                if (data == null && targetData != null && targetData.searcherID == id) data = targetData;
                if (data != null && onPointFilter(data.color))
                {
                    //Normal OPHUD
                    drawNormalOPHUD(event.getRenderer().getRenderManager(), event.getX(), event.getY(), event.getZ(), livingBase, data);
                }
            }
        }
    }

    private static boolean onPointFilter(int color)
    {
        if (color == COLOR_BYPASS) return clientSettings.hudSettings.ophudFilter.showBypass;
        else if (color == COLOR_PASSIVE) return clientSettings.hudSettings.ophudFilter.showPassive;
        else if (color == COLOR_IDLE) return clientSettings.hudSettings.ophudFilter.showIdle;
        else if (color == COLOR_ALERT) return clientSettings.hudSettings.ophudFilter.showAlert;
        else if (color == COLOR_ATTACKING_YOU) return clientSettings.hudSettings.ophudFilter.showAttackingYou;
        else if (color == COLOR_ATTACKING_OTHER) return clientSettings.hudSettings.ophudFilter.showAttackingOther;
        else if (color == COLOR_FLEEING) return clientSettings.hudSettings.ophudFilter.showFleeing;
        return false;
    }

    private static void drawNormalOPHUD(RenderManager renderManager, double x, double y, double z, Entity entity, OnPointData data)
    {
        float viewerYaw = renderManager.playerViewY; //"playerViewY" is LITERALLY the yaw...interpolated over the partialtick
        float viewerPitch = renderManager.playerViewX; //"playerViewX" is LITERALLY the pitch...interpolated over the partialtick
        int color = data.color;
        Color c = new Color(color, true);
        int r = c.r(), g = c.g(), b = c.b();

        boolean depth = clientSettings.hudSettings.ophudStyle.depth;
        double scale = clientSettings.hudSettings.ophudStyle.scale * 0.025;
        double halfSize2D = BASIC_GAUGE_SIZE / 4D;
        double hOff2D = clientSettings.hudSettings.ophudStyle.horizontalOffset2D;
        double vOff2D = Compat.neat ? clientSettings.hudSettings.ophudStyle.verticalOffset2D - 11 : clientSettings.hudSettings.ophudStyle.verticalOffset2D;


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
        textureManager.bindTexture(BASIC_GAUGE_TEXTURE);

        GlStateManager.pushMatrix();

        if (Compat.neat)
        {
            GlStateManager.translate(x, y + entity.height * clientSettings.hudSettings.ophudStyle.verticalPercent + clientSettings.hudSettings.ophudStyle.verticalOffset - 0.5 + CompatNeat.heightAboveMob, z);
            GlStateManager.rotate(-viewerYaw, 0, 1, 0);
            GlStateManager.rotate(renderManager.options.thirdPersonView == 2 ? -viewerPitch : viewerPitch, 1, 0, 0);
            GlStateManager.translate(entity.width * clientSettings.hudSettings.ophudStyle.horizontalPercent, 0, 0);
            GlStateManager.scale(-scale, -scale, scale);
        }
        else if (Compat.customnpcs && entity.getClass().getName().equals("noppes.npcs.entity.EntityCustomNpc"))
        {
            double cnpcScale = entity.height / 1.8;
            GlStateManager.translate(x, y + entity.height * clientSettings.hudSettings.ophudStyle.verticalPercent + clientSettings.hudSettings.ophudStyle.verticalOffset - 0.5 - 0.108 * cnpcScale, z);
            GlStateManager.rotate(-viewerYaw, 0, 1, 0);
            GlStateManager.rotate(renderManager.options.thirdPersonView == 2 ? -viewerPitch : viewerPitch, 1, 0, 0);
            GlStateManager.translate(entity.width * clientSettings.hudSettings.ophudStyle.horizontalPercent, 0, 0);

            scale *= cnpcScale;
            GlStateManager.scale(-scale, -scale, scale);

            vOff2D -= 45;
        }
        else
        {
            GlStateManager.translate(x, y + entity.height * clientSettings.hudSettings.ophudStyle.verticalPercent - (clientSettings.hudSettings.ophudStyle.accountForSneak && entity.isSneaking() ? 0.25 : 0) + clientSettings.hudSettings.ophudStyle.verticalOffset, z);
            GlStateManager.rotate(-viewerYaw, 0, 1, 0);
            GlStateManager.rotate(renderManager.options.thirdPersonView == 2 ? -viewerPitch : viewerPitch, 1, 0, 0);
            GlStateManager.translate(entity.width * clientSettings.hudSettings.ophudStyle.horizontalPercent, 0, 0);
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
            bufferbuilder.pos(left, top, 0).tex(BASIC_GAUGE_UV_HALF_PIXEL, BASIC_GAUGE_UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(left, bottom, 0).tex(BASIC_GAUGE_UV_HALF_PIXEL, 0.5 - BASIC_GAUGE_UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(right, bottom, 0).tex(0.5 - BASIC_GAUGE_UV_HALF_PIXEL, 0.5 - BASIC_GAUGE_UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(right, top, 0).tex(0.5 - BASIC_GAUGE_UV_HALF_PIXEL, BASIC_GAUGE_UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
        }
        else
        {
            double amount = (double) data.percent / 100;
            double level = bottom - halfSize2D * 2 * amount;
            double uvLevel = 0.5 - BASIC_GAUGE_UV_HALF_PIXEL - BASIC_GAUGE_UV_SUBTEX_SIZE * amount;

            //Background fill
            bufferbuilder.pos(left, top, 0).tex(BASIC_GAUGE_UV_HALF_PIXEL, BASIC_GAUGE_UV_HALF_PIXEL).lightmap(15728880, 15728880).color(255, 255, 255, 255).endVertex();
            bufferbuilder.pos(left, level, 0).tex(BASIC_GAUGE_UV_HALF_PIXEL, uvLevel).lightmap(15728880, 15728880).color(255, 255, 255, 255).endVertex();
            bufferbuilder.pos(right, level, 0).tex(0.5 - BASIC_GAUGE_UV_HALF_PIXEL, uvLevel).lightmap(15728880, 15728880).color(255, 255, 255, 255).endVertex();
            bufferbuilder.pos(right, top, 0).tex(0.5 - BASIC_GAUGE_UV_HALF_PIXEL, BASIC_GAUGE_UV_HALF_PIXEL).lightmap(15728880, 15728880).color(255, 255, 255, 255).endVertex();

            //Threat level fill
            bufferbuilder.pos(left, level, 0).tex(BASIC_GAUGE_UV_HALF_PIXEL, uvLevel).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(left, bottom, 0).tex(BASIC_GAUGE_UV_HALF_PIXEL, 0.5 - BASIC_GAUGE_UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(right, bottom, 0).tex(0.5 - BASIC_GAUGE_UV_HALF_PIXEL, 0.5 - BASIC_GAUGE_UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(right, level, 0).tex(0.5 - BASIC_GAUGE_UV_HALF_PIXEL, uvLevel).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
        }

        //Outline and eyes
        if (color == COLOR_ATTACKING_YOU || color == COLOR_ALERT || color == COLOR_BYPASS)
        {
            //Angry, lit up eyes
            bufferbuilder.pos(left, top, 0).tex(BASIC_GAUGE_UV_HALF_PIXEL, 0.5 + BASIC_GAUGE_UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(left, bottom, 0).tex(BASIC_GAUGE_UV_HALF_PIXEL, 1 - BASIC_GAUGE_UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(right, bottom, 0).tex(0.5 - BASIC_GAUGE_UV_HALF_PIXEL, 1 - BASIC_GAUGE_UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(right, top, 0).tex(0.5 - BASIC_GAUGE_UV_HALF_PIXEL, 0.5 + BASIC_GAUGE_UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
        }
        else
        {
            //Normal, empty eyes
            bufferbuilder.pos(left, top, 0).tex(0.5 + BASIC_GAUGE_UV_HALF_PIXEL, BASIC_GAUGE_UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(left, bottom, 0).tex(0.5 + BASIC_GAUGE_UV_HALF_PIXEL, 0.5 - BASIC_GAUGE_UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(right, bottom, 0).tex(1 - BASIC_GAUGE_UV_HALF_PIXEL, 0.5 - BASIC_GAUGE_UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
            bufferbuilder.pos(right, top, 0).tex(1 - BASIC_GAUGE_UV_HALF_PIXEL, BASIC_GAUGE_UV_HALF_PIXEL).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
        }

        tessellator.draw();

        GlStateManager.popMatrix();

        GlStateManager.disableBlend();

        if (!depth) GlStateManager.enableDepth();

        GlStateManager.enableLighting();

        GlStateManager.color(1, 1, 1, 1);
    }

    public static boolean targetingFilter(int color)
    {
        if (color == COLOR_BYPASS) return clientSettings.hudSettings.targetingFilter.showBypass;
        else if (color == COLOR_PASSIVE) return clientSettings.hudSettings.targetingFilter.showPassive;
        else if (color == COLOR_IDLE) return clientSettings.hudSettings.targetingFilter.showIdle;
        else if (color == COLOR_ALERT) return clientSettings.hudSettings.targetingFilter.showAlert;
        else if (color == COLOR_ATTACKING_YOU) return clientSettings.hudSettings.targetingFilter.showAttackingYou;
        else if (color == COLOR_ATTACKING_OTHER) return clientSettings.hudSettings.targetingFilter.showAttackingOther;
        else if (color == COLOR_FLEEING) return clientSettings.hudSettings.targetingFilter.showFleeing;
        return false;
    }

    private static void drawArrow(float x, float y, float angleDeg, float scale)
    {
        drawArrow(x, y, angleDeg, scale, false);
    }

    private static void drawArrow(float x, float y, float angleDeg, float scale, boolean fromCenter)
    {
        GlStateManager.enableTexture2D();
        textureManager.bindTexture(ARROW_TEXTURE);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.rotate(angleDeg, 0, 0, 1);
        GlStateManager.scale(scale, scale, 1);

        float l, r;
        if (fromCenter)
        {
            l = -ARROW_CENTER_LEFT;
            r = ARROW_CENTER_RIGHT;
        }
        else
        {
            l = -ARROW_LEFT;
            r = ARROW_RIGHT;
        }

        GlStateManager.glBegin(GL_QUADS);
        GlStateManager.glTexCoord2f(ARROW_UV_HALF_PIXEL_W, ARROW_UV_HALF_PIXEL_H);
        GlStateManager.glVertex3f(l, -ARROW_ABOVE, 0);
        GlStateManager.glTexCoord2f(ARROW_UV_HALF_PIXEL_W, 1f - ARROW_UV_HALF_PIXEL_H);
        GlStateManager.glVertex3f(l, ARROW_BELOW, 0);
        GlStateManager.glTexCoord2f(1f - ARROW_UV_HALF_PIXEL_W, 1f - ARROW_UV_HALF_PIXEL_H);
        GlStateManager.glVertex3f(r, ARROW_BELOW, 0);
        GlStateManager.glTexCoord2f(1f - ARROW_UV_HALF_PIXEL_W, ARROW_UV_HALF_PIXEL_H);
        GlStateManager.glVertex3f(r, -ARROW_ABOVE, 0);
        GlStateManager.glEnd();

        GlStateManager.popMatrix();
    }

    private static void drawReticle(float x, float y)
    {
        int spacing = clientSettings.hudSettings.targetingStyle.reticleSpacing;
        float scale = clientSettings.hudSettings.targetingStyle.reticleSize / Tools.max(ARROW_WIDTH, ARROW_HEIGHT);

        drawArrow(x - spacing, y + spacing, -45, scale);
        drawArrow(x + spacing, y + spacing, -135, scale);
        drawArrow(x - spacing, y - spacing, 45, scale);
        drawArrow(x + spacing, y - spacing, 135, scale);
    }

    private void drawHUD(Minecraft mc)
    {
        //Detailed OPHUD
        if (targetData != null)
        {
            Entity entity = mc.player.world.getEntityByID(targetData.searcherID);
            if (entity != null) drawTargetingHUD(entity, mc.fontRenderer);
        }


        //Main HUD below this point =============================================


        //Stealth Gauge
        int stealth = ClientData.stealthLevel;
        float alpha = (float) clientSettings.hudSettings.mainStyle.stealthGaugeAlpha;
        if (stealth != Byte.MIN_VALUE && alpha > 0)
        {
            GlStateManager.enableBlend();
            GlStateManager.enableTexture2D();

            int radius = clientSettings.hudSettings.mainStyle.stealthGaugeSize / 2;
            ScaledResolution sr = new ScaledResolution(mc);
            float theta = 0.9f * stealth;

            GlStateManager.pushMatrix();
            GlStateManager.translate(sr.getScaledWidth() - radius, sr.getScaledHeight() - radius, 0);
            GlStateManager.rotate(theta, 0, 0, 1);

            //Fill
            textureManager.bindTexture(STEALTH_GAUGE_TEXTURE);
            Color c = new Color(Integer.parseInt(clientSettings.hudSettings.mainStyle.stealthGaugeColor, 16), true);
            GlStateManager.color(c.rf(), c.gf(), c.bf(), alpha);

            GlStateManager.glBegin(GL_QUADS);
            GlStateManager.glTexCoord2f(STEALTH_GAUGE_UV_HALF_PIXEL, STEALTH_GAUGE_UV_HALF_PIXEL);
            GlStateManager.glVertex3f(-radius, -radius, 0);
            GlStateManager.glTexCoord2f(STEALTH_GAUGE_UV_HALF_PIXEL, 1f - STEALTH_GAUGE_UV_HALF_PIXEL);
            GlStateManager.glVertex3f(-radius, radius, 0);
            GlStateManager.glTexCoord2f(1f - STEALTH_GAUGE_UV_HALF_PIXEL, 1f - STEALTH_GAUGE_UV_HALF_PIXEL);
            GlStateManager.glVertex3f(radius, radius, 0);
            GlStateManager.glTexCoord2f(1f - STEALTH_GAUGE_UV_HALF_PIXEL, STEALTH_GAUGE_UV_HALF_PIXEL);
            GlStateManager.glVertex3f(radius, -radius, 0);
            GlStateManager.glEnd();

            //Rim
            GlStateManager.rotate(-theta, 0, 0, 1);

            textureManager.bindTexture(STEALTH_GAUGE_RIM_TEXTURE);
            c = new Color(Integer.parseInt(clientSettings.hudSettings.mainStyle.stealthGaugeRimColor, 16), true);
            GlStateManager.color(c.rf(), c.gf(), c.bf(), 1);

            GlStateManager.glBegin(GL_QUADS);
            GlStateManager.glTexCoord2f(STEALTH_GAUGE_RIM_UV_HALF_PIXEL, STEALTH_GAUGE_RIM_UV_HALF_PIXEL);
            GlStateManager.glVertex3f(-radius, -radius, 0);
            GlStateManager.glTexCoord2f(STEALTH_GAUGE_RIM_UV_HALF_PIXEL, 1f - STEALTH_GAUGE_RIM_UV_HALF_PIXEL);
            GlStateManager.glVertex3f(-radius, radius, 0);
            GlStateManager.glTexCoord2f(1f - STEALTH_GAUGE_RIM_UV_HALF_PIXEL, 1f - STEALTH_GAUGE_RIM_UV_HALF_PIXEL);
            GlStateManager.glVertex3f(radius, radius, 0);
            GlStateManager.glTexCoord2f(1f - STEALTH_GAUGE_RIM_UV_HALF_PIXEL, STEALTH_GAUGE_RIM_UV_HALF_PIXEL);
            GlStateManager.glVertex3f(radius, -radius, 0);
            GlStateManager.glEnd();

            //Arrow
            drawArrow(0, -radius, 90, 0.06f, true);

            GlStateManager.popMatrix();
        }
    }

    public void drawTargetingHUD(Entity entity, FontRenderer fontRenderer)
    {
        try
        {
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();


            //General Setup
            Pair<Float, Float> pos = Render.getEntityXYInWindow(entity, 0, entity.height * 0.5, 0);
            float originX = pos.getKey(), originY = pos.getValue();

            int portW = Render.getViewportWidth();
            int portH = Render.getViewportHeight();

            boolean offScreen = false;
            float boundX = originX, boundY = originY;
            if (boundX < 1)
            {
                boundX = 1;
                offScreen = true;
            }
            else if (boundX > portW - 1)
            {
                boundX = portW - 1;
                offScreen = true;
            }
            if (boundY < 1)
            {
                boundY = 1;
                offScreen = true;
            }
            else if (boundY > portH - 1)
            {
                boundY = portH - 1;
                offScreen = true;
            }


            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

            if (offScreen)
            {
                //Offscreen indicator
                Color c;
                if (clientSettings.hudSettings.targetingStyle.stateColoredArrow)
                {
                    c = new Color(targetData.color, true);
                }
                else c = new Color(Integer.parseInt(clientSettings.hudSettings.targetingStyle.defaultArrowColor, 16), true);
                GlStateManager.color(c.rf(), c.gf(), c.bf(), (float) clientSettings.hudSettings.targetingStyle.arrowAlpha);

                double centerX = Render.getViewportWidth() * 0.5, centerY = Render.getViewportHeight() * 0.5;
                double angleRad = TRIG_TABLE.arctanFullcircle(centerX, centerY, originX, originY);
                double dist = Tools.min(Render.getViewportWidth(), Render.getViewportHeight());
                double originDrawX = (centerX + dist * 0.4 * TRIG_TABLE.cos(angleRad)) / sr.getScaleFactor();
                double originDrawY = (centerY - dist * 0.4 * TRIG_TABLE.sin(angleRad)) / sr.getScaleFactor();

                drawArrow((float) originDrawX, (float) originDrawY, 360f - (float) Tools.radtodeg(angleRad), clientSettings.hudSettings.targetingStyle.arrowSize / Tools.max(ARROW_WIDTH, ARROW_HEIGHT));
            }
            else
            {
                //Onscreen reticle
                float originDrawX = boundX / sr.getScaleFactor();
                float originDrawY = boundY / sr.getScaleFactor();

                Color c;
                if (clientSettings.hudSettings.targetingStyle.stateColoredReticle)
                {
                    c = new Color(targetData.color, true);
                }
                else c = new Color(Integer.parseInt(clientSettings.hudSettings.targetingStyle.defaultReticleColor, 16), true);
                GlStateManager.color(c.rf(), c.gf(), c.bf(), (float) clientSettings.hudSettings.targetingStyle.reticleAlpha);

                drawReticle(originDrawX, originDrawY);


                //Text setup
                int targetID = targetData.targetID;
                Entity target = (targetID == -1 || targetID == -2) ? null : entity.world.getEntityByID(targetID);

                float padding = 1;
                ArrayList<String> elements = new ArrayList<>();

                if (clientSettings.hudSettings.targetingStyle.components.name) elements.add(entity.getName());
                if (clientSettings.hudSettings.targetingStyle.components.target)
                {
                    if (targetData.color == COLOR_FLEEING)
                    {
                        elements.add("Fleeing from " + (target == null ? UNKNOWN : target.getName()));
                    }
                    else
                    {
                        if (targetID != -1 && targetID != -2) elements.add("Targeting " + (target == null ? UNKNOWN : target.getName()));
                        else if (targetData.percent > 0) elements.add("Searching for target");
                    }
                }
                if (clientSettings.hudSettings.targetingStyle.components.threat)
                {
                    if (targetData.color == COLOR_BYPASS) elements.add("Threat: §k000");
                    else if (targetData.percent > 0) elements.add("Threat: " + targetData.percent + "%");
                }
                if (clientSettings.hudSettings.targetingStyle.components.distance)
                {
                    elements.add("Distance: " + oneDecimal.format(entity.getDistance(Minecraft.getMinecraft().player)));
                }

                float width = 0;
                for (String string : elements)
                {
                    width = Tools.max(width, fontRenderer.getStringWidth(string));
                }
                float height = fontRenderer.FONT_HEIGHT * elements.size() + padding * (elements.size() - 1);


                //Targeting HUD text
                float textScale = (float) clientSettings.hudSettings.targetingStyle.textScale;

                float offX = 20;
                float alpha = (float) clientSettings.hudSettings.targetingStyle.textAlpha;
                int color = clientSettings.hudSettings.targetingStyle.stateColoredText ? targetData.color : Integer.parseInt(clientSettings.hudSettings.targetingStyle.defaultTextColor, 16);
                color |= ((int) (0xFF * alpha) << 24);
                GlStateManager.disableTexture2D();

                boolean toRight = originX < portW >> 1;
                if (!toRight) offX = -offX;

                pos = Render.getEntityXYInWindow(entity, 0, entity.height * 0.5, 0);
                float drawX = pos.getKey() / sr.getScaleFactor() + offX, drawY = pos.getValue() / sr.getScaleFactor();

                GlStateManager.pushMatrix();
                GlStateManager.translate(drawX, drawY, 0);
                GlStateManager.scale(textScale, textScale, 1);
                GlStateManager.color(0, 0, 0, alpha);

                if (toRight)
                {
                    //Text background
                    GlStateManager.glBegin(GL_QUADS);
                    GlStateManager.glVertex3f(-padding, -height / 2 - padding, 0);
                    GlStateManager.glVertex3f(-padding, height / 2 + padding - 1, 0);
                    GlStateManager.glVertex3f(width + padding - 1, height / 2 + padding - 1, 0);
                    GlStateManager.glVertex3f(width + padding - 1, -height / 2 - padding, 0);
                    GlStateManager.glEnd();

                    //Text elements
                    GlStateManager.enableTexture2D();
                    for (int i = 0; i < elements.size(); i++)
                    {
                        fontRenderer.drawString(elements.get(i), 0, -height / 2 + height * i / elements.size(), color, false);
                    }
                }
                else
                {
                    //Text background
                    GlStateManager.glBegin(GL_QUADS);
                    GlStateManager.glVertex3f(-width - padding, -height / 2 - padding, 0);
                    GlStateManager.glVertex3f(-width - padding, height / 2 + padding - 1, 0);
                    GlStateManager.glVertex3f(padding - 1, height / 2 + padding - 1, 0);
                    GlStateManager.glVertex3f(padding - 1, -height / 2 - padding, 0);
                    GlStateManager.glEnd();

                    //Text elements
                    GlStateManager.enableTexture2D();
                    for (int i = 0; i < elements.size(); i++)
                    {
                        fontRenderer.drawString(elements.get(i), -width, -height / 2 + height * i / elements.size(), color, false);
                    }
                }

                GlStateManager.popMatrix();
            }
        }
        catch (IllegalAccessException e)
        {
            MCTools.crash(e, 155, false);
        }
    }
}
