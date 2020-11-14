package com.fantasticsource.dynamicstealth.client;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import com.fantasticsource.dynamicstealth.client.event.RenderLightGaugeEvent;
import com.fantasticsource.dynamicstealth.client.event.RenderOPHUDEvent;
import com.fantasticsource.dynamicstealth.client.event.RenderStealthGaugeEvent;
import com.fantasticsource.dynamicstealth.client.event.RenderTargetingHUDEvent;
import com.fantasticsource.dynamicstealth.common.ClientData;
import com.fantasticsource.dynamicstealth.compat.Compat;
import com.fantasticsource.dynamicstealth.compat.CompatNeat;
import com.fantasticsource.dynamicstealth.config.DynamicStealthConfig;
import com.fantasticsource.mctools.ClientTickTimer;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.OutlinedFontRenderer;
import com.fantasticsource.mctools.Render;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.fantasticsource.dynamicstealth.DynamicStealth.TRIG_TABLE;
import static com.fantasticsource.dynamicstealth.common.ClientData.*;
import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.clientSettings;
import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX;
import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX_LMAP_COLOR;
import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class HUD
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
    private static final ResourceLocation STEALTH_GAUGE_TEXTURE_2 = new ResourceLocation(DynamicStealth.MODID, "image/stealthgauge2.png");
    private static final float STEALTH_GAUGE_2_UV_HALF_PIXEL_W = 0.5f / (STEALTH_GAUGE_SIZE * 10);
    private static final float STEALTH_GAUGE_2_UV_HALF_PIXEL_H = 0.5f / (STEALTH_GAUGE_SIZE * 5);
    private static final float STEALTH_GAUGE_2_UV_W = 1f / 10;
    private static final float STEALTH_GAUGE_2_UV_H = 1f / 5;
    private static final ResourceLocation STEALTH_GAUGE_TEXTURE_3 = new ResourceLocation(DynamicStealth.MODID, "image/stealthgauge3.png");
    private static final float STEALTH_GAUGE_3_UV_HALF_PIXEL_W = 0.5f / (STEALTH_GAUGE_SIZE * 10);
    private static final float STEALTH_GAUGE_3_UV_HALF_PIXEL_H = 0.5f / (STEALTH_GAUGE_SIZE * 7);
    private static final float STEALTH_GAUGE_3_UV_W = 1f / 10;
    private static final float STEALTH_GAUGE_3_UV_H = 1f / 7;

    private static final ResourceLocation STEALTH_GAUGE_RIM_TEXTURE = new ResourceLocation(DynamicStealth.MODID, "image/stealthgaugerim.png");
    private static final int STEALTH_GAUGE_RIM_SIZE = 256;
    private static final float STEALTH_GAUGE_RIM_UV_HALF_PIXEL = 0.5f / STEALTH_GAUGE_RIM_SIZE;

    private static final ResourceLocation CRYSTAL_TEXTURE = new ResourceLocation(DynamicStealth.MODID, "image/crystal.png");
    private static final float CRYSTAL_WIDTH = 63, CRYSTAL_HEIGHT = 25;
    private static final float CRYSTAL_UV_HALF_PIXEL_W = 0.5f / CRYSTAL_WIDTH, CRYSTAL_UV_HALF_PIXEL_H = 0.5f / CRYSTAL_HEIGHT;
    private static final float CRYSTAL_ORIGIN_X = 0, CRYSTAL_ORIGIN_Y = 12;
    private static final float CRYSTAL_LEFT = CRYSTAL_ORIGIN_X, CRYSTAL_RIGHT = CRYSTAL_WIDTH - CRYSTAL_ORIGIN_X;
    private static final float CRYSTAL_ABOVE = CRYSTAL_ORIGIN_Y, CRYSTAL_BELOW = CRYSTAL_HEIGHT - CRYSTAL_ORIGIN_Y;

    private static final ResourceLocation HP_BACKGROUND_TEXTURE = new ResourceLocation(DynamicStealth.MODID, "image/hpbackground.png");
    private static final float HP_BACKGROUND_WIDTH = 142, HP_BACKGROUND_HEIGHT = 18;

    private static final ResourceLocation HP_FILL_TEXTURE = new ResourceLocation(DynamicStealth.MODID, "image/hpfill.png");
    private static final float HP_FILL_WIDTH = 140, HP_FILL_HEIGHT = 16;

    private static final DecimalFormat
            ONE_DECIMAL = new DecimalFormat("0.0"),
            NO_DECIMAL = new DecimalFormat("0");

    private static TextureManager textureManager = Minecraft.getMinecraft().renderEngine;
    private static float prevPartialTickExtended = 0;

    public static void draw(RenderGameOverlayEvent.Pre event, Minecraft mc)
    {
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);

        drawHUD(event, mc);

        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void clientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            ClientData.targetData = null;
            ClientData.targetPriority = Integer.MAX_VALUE;

            for (OnPointData data : opMap.values())
            {
                makeTargetIfBetter(data);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void drawHUD(Render.RenderHUDEvent event)
    {
        draw(event.getParentEvent(), Minecraft.getMinecraft());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void replaceCursor(RenderGameOverlayEvent.Pre event)
    {
        if (event.getType() != RenderGameOverlayEvent.ElementType.CROSSHAIRS || DynamicStealthConfig.clientSettings.hudSettings.mainStyle.stealthGaugeMode != 3) return;

        if (!MinecraftForge.EVENT_BUS.post(new RenderStealthGaugeEvent(event))) drawStealthGauge(Minecraft.getMinecraft(), 3, event);
    }

    @SubscribeEvent
    public static void entityRender(RenderLivingEvent.Post event)
    {
        if (!event.getRenderer().getRenderManager().renderOutlines)
        {
            GlStateManager.color(1, 1, 1, 1);

            EntityLivingBase livingBase = event.getEntity();
            if (livingBase != null)
            {
                if (!MCTools.isRidingOrRiddenBy(Minecraft.getMinecraft().player, livingBase))
                {
                    int id = livingBase.getEntityId();

                    OnPointData data = opMap.get(id);
                    if (data == null && targetData != null && targetData.searcherID == id) data = targetData;
                    if (data != null && onPointFilter(data.color))
                    {
                        //Normal OPHUD
                        drawOPHUD(event, livingBase, data);
                    }
                }
            }
        }
    }

    private static boolean onPointFilter(int color)
    {
        if (color == COLOR_BYPASS) return clientSettings.hudSettings.ophudFilter.bypass;
        else if (color == COLOR_IDLE_PASSIVE) return clientSettings.hudSettings.ophudFilter.idlePassive;
        else if (color == COLOR_IDLE_NON_PASSIVE) return clientSettings.hudSettings.ophudFilter.idleNonPassive;
        else if (color == COLOR_SEARCHING) return clientSettings.hudSettings.ophudFilter.alert;
        else if (color == COLOR_ATTACKING_YOU) return clientSettings.hudSettings.ophudFilter.attackingYou;
        else if (color == COLOR_ATTACKING_OTHER) return clientSettings.hudSettings.ophudFilter.attackingOther;
        else if (color == COLOR_FLEEING_N0N_PASSIVE) return clientSettings.hudSettings.ophudFilter.fleeingNonPassive;
        else if (color == COLOR_FLEEING_PASSIVE) return clientSettings.hudSettings.ophudFilter.fleeingPassive;
        else if (color == COLOR_DAZED) return clientSettings.hudSettings.ophudFilter.dazed;
        return false;
    }

    private static void drawOPHUD(RenderLivingEvent.Post event, EntityLivingBase livingBase, OnPointData data)
    {
        RenderManager renderManager = event.getRenderer().getRenderManager();
        double x = event.getX(), y = event.getY(), z = event.getZ();

        float viewerYaw = renderManager.playerViewY; //"playerViewY" is LITERALLY the yaw...interpolated over the partialtick
        float viewerPitch = renderManager.playerViewX; //"playerViewX" is LITERALLY the pitch...interpolated over the partialtick
        int color = data.color;
        Color c = new Color(color, true);
        int r = c.r(), g = c.g(), b = c.b();

        boolean depth = clientSettings.hudSettings.ophudStyle.depth;
        double scale = clientSettings.hudSettings.ophudStyle.scale * 0.025;
        double halfSize2D = BASIC_GAUGE_SIZE / 4D;
        double hOff2D = clientSettings.hudSettings.ophudStyle.horizontalOffset2D;
        double vOff2D = Compat.neat ? clientSettings.hudSettings.ophudStyle.verticalOffset2D - 11 : clientSettings.hudSettings.ophudStyle.verticalOffset2D - 22;


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


        MinecraftForge.EVENT_BUS.post(new RenderOPHUDEvent.Untransformed(event, data));


        GlStateManager.pushMatrix();

        if (Compat.neat)
        {
            GlStateManager.translate(x, y + livingBase.height * clientSettings.hudSettings.ophudStyle.verticalPercent + clientSettings.hudSettings.ophudStyle.verticalOffset - 0.5 + CompatNeat.heightAboveMob, z);
            GlStateManager.rotate(-viewerYaw, 0, 1, 0);
            GlStateManager.rotate(renderManager.options.thirdPersonView == 2 ? -viewerPitch : viewerPitch, 1, 0, 0);
            GlStateManager.translate(livingBase.width * clientSettings.hudSettings.ophudStyle.horizontalPercent, 0, 0);
            GlStateManager.scale(-scale, -scale, scale);
        }
        else if (Compat.customnpcs && livingBase.getClass().getName().equals("noppes.npcs.entity.EntityCustomNpc"))
        {
            double cnpcScale = livingBase.height / 1.8;
            GlStateManager.translate(x, y + livingBase.height * clientSettings.hudSettings.ophudStyle.verticalPercent + clientSettings.hudSettings.ophudStyle.verticalOffset - 0.5 - 0.108 * cnpcScale, z);
            GlStateManager.rotate(-viewerYaw, 0, 1, 0);
            GlStateManager.rotate(renderManager.options.thirdPersonView == 2 ? -viewerPitch : viewerPitch, 1, 0, 0);
            GlStateManager.translate(livingBase.width * clientSettings.hudSettings.ophudStyle.horizontalPercent, 0, 0);

            scale *= cnpcScale;
            GlStateManager.scale(-scale, -scale, scale);

            vOff2D -= 56;
        }
        else
        {
            GlStateManager.translate(x, y + livingBase.height * clientSettings.hudSettings.ophudStyle.verticalPercent - (clientSettings.hudSettings.ophudStyle.accountForSneak && livingBase.isSneaking() ? 0.25 : 0) + clientSettings.hudSettings.ophudStyle.verticalOffset, z);
            GlStateManager.rotate(-viewerYaw, 0, 1, 0);
            GlStateManager.rotate(renderManager.options.thirdPersonView == 2 ? -viewerPitch : viewerPitch, 1, 0, 0);
            GlStateManager.translate(livingBase.width * clientSettings.hudSettings.ophudStyle.horizontalPercent, 0, 0);
            GlStateManager.scale(-scale, -scale, scale);
        }


        if (!MinecraftForge.EVENT_BUS.post(new RenderOPHUDEvent.Transformed(event, data)))
        {
            //Threat gauge
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(GL_QUADS, POSITION_TEX_LMAP_COLOR);

            double left = -halfSize2D + hOff2D;
            double right = halfSize2D + hOff2D;
            double top = -halfSize2D + vOff2D;
            double bottom = halfSize2D + vOff2D;
            if (!canHaveThreat(color))
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
            if (color == COLOR_ATTACKING_YOU || color == COLOR_SEARCHING || color == COLOR_BYPASS)
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


            //HP gauge
            if (!Compat.neat && clientSettings.hudSettings.ophudStyle.hpGauge)
            {
                //Background
                top = bottom + 4;
                bottom = top + HP_BACKGROUND_HEIGHT;
                right = HP_BACKGROUND_WIDTH / 2;
                left = -right;

                GlStateManager.depthMask(false);

                textureManager.bindTexture(HP_BACKGROUND_TEXTURE);
                bufferbuilder.begin(GL_QUADS, POSITION_TEX_LMAP_COLOR);
                bufferbuilder.pos(left, top, 0).tex(0, 0).lightmap(15728880, 15728880).color(255, 255, 255, 255).endVertex();
                bufferbuilder.pos(left, bottom, 0).tex(0, 1).lightmap(15728880, 15728880).color(255, 255, 255, 255).endVertex();
                bufferbuilder.pos(right, bottom, 0).tex(1, 1).lightmap(15728880, 15728880).color(255, 255, 255, 255).endVertex();
                bufferbuilder.pos(right, top, 0).tex(1, 0).lightmap(15728880, 15728880).color(255, 255, 255, 255).endVertex();
                tessellator.draw();


                //Fill
                double ratio = Tools.min(Tools.max(livingBase.getHealth() / livingBase.getMaxHealth(), 0), 1);

                Color color2;
                if (livingBase.getIsInvulnerable())
                {
                    color2 = Color.PURPLE.copy().setVF(0.7f);
                }
                else
                {
                    g = (int) (255 * ratio);
                    r = 255 - g;
                    b = 0;
                    color2 = new Color(r, g, b).setVF((float) (0.3 + 0.5 * (0.5 - Math.abs(0.5 - ratio))));
                }

                r = color2.r();
                g = color2.g();
                b = color2.b();

                double halfWDif = (HP_FILL_WIDTH - HP_BACKGROUND_WIDTH) / 2;
                double halfHDif = (HP_FILL_HEIGHT - HP_BACKGROUND_HEIGHT) / 2;
                double left2 = left - halfWDif;
                double right2 = right + halfWDif;
                double top2 = top - halfHDif;
                double bottom2 = bottom + halfHDif;
                double separation = left2 + (right2 - left2) * ratio;

                textureManager.bindTexture(HP_FILL_TEXTURE);
                bufferbuilder.begin(GL_QUADS, POSITION_TEX_LMAP_COLOR);
                bufferbuilder.pos(left2, top2, 0).tex(0, 0).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
                bufferbuilder.pos(left2, bottom2, 0).tex(0, 1).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
                bufferbuilder.pos(separation, bottom2, 0).tex(ratio, 1).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
                bufferbuilder.pos(separation, top2, 0).tex(ratio, 0).lightmap(15728880, 15728880).color(r, g, b, 255).endVertex();
                tessellator.draw();


                //Text
                if (clientSettings.hudSettings.ophudStyle.hpGaugePercent || clientSettings.hudSettings.ophudStyle.hpGaugeCurrentAndMax)
                {
                    float spacing = (float) ((bottom - top) / 2 - OutlinedFontRenderer.LINE_HEIGHT / 2 + 0.5);

                    if (clientSettings.hudSettings.ophudStyle.hpGaugeCurrentAndMax)
                    {
                        String text = ONE_DECIMAL.format(livingBase.getHealth());
                        OutlinedFontRenderer.draw(text, (float) (left + 3 + spacing), (float) (top + spacing), Color.WHITE, Color.BLACK);
                        text = ONE_DECIMAL.format(livingBase.getMaxHealth());
                        OutlinedFontRenderer.draw(text, (float) (right - 3 - spacing - OutlinedFontRenderer.getStringWidth(text)), (float) (top + spacing), Color.WHITE, Color.BLACK);
                    }

                    if (clientSettings.hudSettings.ophudStyle.hpGaugePercent)
                    {
                        String text = NO_DECIMAL.format(100 * livingBase.getHealth() / livingBase.getMaxHealth()) + "%";
                        OutlinedFontRenderer.draw(text, (float) (left + (right - left) / 2 - OutlinedFontRenderer.getStringWidth(text)) / 2, (float) (top + spacing), Color.WHITE, Color.BLACK);
                    }
                }


                GlStateManager.depthMask(true);


                //Set depth of drawn area (redraw background in depth buffer only)
                GlStateManager.colorMask(false, false, false, false);

                textureManager.bindTexture(HP_BACKGROUND_TEXTURE);
                bufferbuilder.begin(GL_QUADS, POSITION_TEX);
                bufferbuilder.pos(left, top, 0).tex(0, 0).endVertex();
                bufferbuilder.pos(left, bottom, 0).tex(0, 1).endVertex();
                bufferbuilder.pos(right, bottom, 0).tex(1, 1).endVertex();
                bufferbuilder.pos(right, top, 0).tex(1, 0).endVertex();
                tessellator.draw();

                GlStateManager.colorMask(true, true, true, true);
            }
        }


        GlStateManager.popMatrix();

        GlStateManager.disableBlend();

        if (!depth) GlStateManager.enableDepth();

        GlStateManager.enableLighting();
    }

    private static void makeTargetIfBetter(OnPointData data)
    {
        int color = data.color;
        if (color == COLOR_BYPASS && !clientSettings.hudSettings.targetingFilter.bypass) return;
        if (color == COLOR_IDLE_PASSIVE && !clientSettings.hudSettings.targetingFilter.idlePassive) return;
        if (color == COLOR_IDLE_NON_PASSIVE && !clientSettings.hudSettings.targetingFilter.idleNonPassive) return;
        if (color == COLOR_SEARCHING && !clientSettings.hudSettings.targetingFilter.alert) return;
        if (color == COLOR_ATTACKING_YOU && !clientSettings.hudSettings.targetingFilter.attackingYou) return;
        if (color == COLOR_ATTACKING_OTHER && !clientSettings.hudSettings.targetingFilter.attackingOther) return;
        if (color == COLOR_FLEEING_N0N_PASSIVE && !clientSettings.hudSettings.targetingFilter.fleeingNonPassive) return;
        if (color == COLOR_FLEEING_PASSIVE && !clientSettings.hudSettings.targetingFilter.fleeingPassive) return;
        if (color == COLOR_DAZED && !clientSettings.hudSettings.targetingFilter.dazed) return;

        int maxDist = clientSettings.hudSettings.targetingFilter.maxDist;
        int maxAngle = clientSettings.hudSettings.targetingFilter.maxAngle;
        if (maxDist <= 0 || maxAngle < 0) return;

        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) return;

        World world = player.world;
        if (world == null) return;

        Entity entity = world.getEntityByID(data.searcherID);
        if (entity == null) return;

        double distSquared = player.getDistanceSq(entity);
        if (distSquared > Math.pow(maxDist, 2)) return;

        double angleDif = Vec3d.fromPitchYaw(player.rotationPitch, player.rotationYawHead).normalize().dotProduct(new Vec3d(entity.posX - player.posX, (entity.posY + entity.height * 0.5) - (player.posY + player.eyeHeight), entity.posZ - player.posZ).normalize());
        //And because Vec3d.fromPitchYaw occasionally returns values barely out of the range of (-1, 1)...
        if (angleDif < -1) angleDif = -1;
        else if (angleDif > 1) angleDif = 1;
        angleDif = Tools.radtodeg(TRIG_TABLE.arccos(angleDif)); //0 in front, pi in back
        if (angleDif > maxAngle) return;

        double priority = Math.pow(angleDif, 4) * distSquared;

        if (priority < targetPriority)
        {
            targetData = data;
            targetPriority = priority;
        }
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
        GlStateManager.rotate(angleDeg, 0, 0, -1);
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


    private static void drawCrystals(float x, float y, float scale, float... anglesDeg)
    {
        textureManager.bindTexture(CRYSTAL_TEXTURE);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, 1);
        for (float angleDeg : anglesDeg)
        {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(angleDeg, 0, 0, -1);

            GlStateManager.glBegin(GL_QUADS);
            GlStateManager.glTexCoord2f(CRYSTAL_UV_HALF_PIXEL_W, CRYSTAL_UV_HALF_PIXEL_H);
            GlStateManager.glVertex3f(-CRYSTAL_LEFT, -CRYSTAL_ABOVE, 0);
            GlStateManager.glTexCoord2f(CRYSTAL_UV_HALF_PIXEL_W, 1f - CRYSTAL_UV_HALF_PIXEL_H);
            GlStateManager.glVertex3f(-CRYSTAL_LEFT, CRYSTAL_BELOW, 0);
            GlStateManager.glTexCoord2f(1f - CRYSTAL_UV_HALF_PIXEL_W, 1f - CRYSTAL_UV_HALF_PIXEL_H);
            GlStateManager.glVertex3f(CRYSTAL_RIGHT, CRYSTAL_BELOW, 0);
            GlStateManager.glTexCoord2f(1f - CRYSTAL_UV_HALF_PIXEL_W, CRYSTAL_UV_HALF_PIXEL_H);
            GlStateManager.glVertex3f(CRYSTAL_RIGHT, -CRYSTAL_ABOVE, 0);
            GlStateManager.glEnd();

            GlStateManager.popMatrix();
        }

        GlStateManager.popMatrix();
    }


    private static void drawReticle(float x, float y)
    {
        int spacing = clientSettings.hudSettings.targetingStyle.reticleSpacing;
        float scale = clientSettings.hudSettings.targetingStyle.reticleSize / Tools.max(ARROW_WIDTH, ARROW_HEIGHT);

        drawArrow(x - spacing, y + spacing, 45, scale);
        drawArrow(x + spacing, y + spacing, 135, scale);
        drawArrow(x - spacing, y - spacing, -45, scale);
        drawArrow(x + spacing, y - spacing, -135, scale);
    }


    private static void drawLightGauge(Minecraft mc)
    {
        if (!clientSettings.hudSettings.lightGauge.showLightGauge) return;

        float alpha = (float) clientSettings.hudSettings.lightGauge.lightGaugeAlpha;
        if (alpha <= 0) return;

        float scale = (clientSettings.hudSettings.lightGauge.lightGaugeSize >> 1) / Tools.max(CRYSTAL_WIDTH, CRYSTAL_HEIGHT);

        float maxOffset = Tools.max(CRYSTAL_LEFT, CRYSTAL_RIGHT, CRYSTAL_ABOVE, CRYSTAL_BELOW);
        ScaledResolution sr = new ScaledResolution(mc);
        double x = maxOffset * scale + (sr.getScaledWidth() - maxOffset * 2 * scale) * clientSettings.hudSettings.lightGauge.lightGaugeX;
        double y = maxOffset * scale + (sr.getScaledHeight() - maxOffset * 2 * scale) * clientSettings.hudSettings.lightGauge.lightGaugeY;

        int light = ClientData.lightLevel;

        //Filled Crystals
        Color c = new Color(Integer.parseInt(clientSettings.hudSettings.lightGauge.lightGaugeColorFull, 16), true);
        GlStateManager.color(c.rf(), c.gf(), c.bf(), alpha);
        int i = 0, iLight = light * 24;
        for (; i < iLight; i += 24)
        {
            drawCrystals((float) x, (float) y, scale, i + 90);
        }

        //Empty Crystals
        c = new Color(Integer.parseInt(clientSettings.hudSettings.lightGauge.lightGaugeColorEmpty, 16), true);
        GlStateManager.color(c.rf(), c.gf(), c.bf(), alpha);
        for (; i < 360; i += 24)
        {
            drawCrystals((float) x, (float) y, scale, i + 90);
        }
    }

    private static void drawStealthGauge(Minecraft mc, int mode, RenderGameOverlayEvent.Pre event)
    {
        if (mode == 0) return;

        float alpha = (float) clientSettings.hudSettings.mainStyle.stealthGaugeAlpha;
        if (alpha <= 0) return;

        if (ClientData.stealthLevel == Byte.MIN_VALUE) return;

        float partialTick = mc.getRenderPartialTicks(), partialTickExtended = (float) ClientTickTimer.currentTick() + partialTick, partialTickDelta = partialTickExtended - prevPartialTickExtended;
        prevPartialTickExtended = partialTickExtended;


        GlStateManager.pushMatrix();
        ScaledResolution sr = new ScaledResolution(mc);
        int halfSize = clientSettings.hudSettings.mainStyle.stealthGaugeSize / 2;
        double x = halfSize + (sr.getScaledWidth() - halfSize * 2) * clientSettings.hudSettings.mainStyle.stealthGaugeX;
        double y = halfSize + (sr.getScaledHeight() - halfSize * 2) * clientSettings.hudSettings.mainStyle.stealthGaugeY;
        GlStateManager.translate(x, y, 0);

        Color c = new Color(Integer.parseInt(clientSettings.hudSettings.mainStyle.stealthGaugeColor, 16), true);
        if (mode == 1)
        {
            float stealth = partialTick * (ClientData.stealthLevel - ClientData.prevStealthLevel) + ClientData.prevStealthLevel;
            float theta = 0.9f * stealth;
            GlStateManager.rotate(theta, 0, 0, 1);

            //Fill
            textureManager.bindTexture(STEALTH_GAUGE_TEXTURE);
            GlStateManager.color(c.rf(), c.gf(), c.bf(), alpha);

            GlStateManager.glBegin(GL_QUADS);
            GlStateManager.glTexCoord2f(STEALTH_GAUGE_UV_HALF_PIXEL, STEALTH_GAUGE_UV_HALF_PIXEL);
            GlStateManager.glVertex3f(-halfSize, -halfSize, 0);
            GlStateManager.glTexCoord2f(STEALTH_GAUGE_UV_HALF_PIXEL, 1f - STEALTH_GAUGE_UV_HALF_PIXEL);
            GlStateManager.glVertex3f(-halfSize, halfSize, 0);
            GlStateManager.glTexCoord2f(1f - STEALTH_GAUGE_UV_HALF_PIXEL, 1f - STEALTH_GAUGE_UV_HALF_PIXEL);
            GlStateManager.glVertex3f(halfSize, halfSize, 0);
            GlStateManager.glTexCoord2f(1f - STEALTH_GAUGE_UV_HALF_PIXEL, STEALTH_GAUGE_UV_HALF_PIXEL);
            GlStateManager.glVertex3f(halfSize, -halfSize, 0);
            GlStateManager.glEnd();

            //Rim
            GlStateManager.rotate(-theta, 0, 0, 1);

            textureManager.bindTexture(STEALTH_GAUGE_RIM_TEXTURE);
            c = new Color(Integer.parseInt(clientSettings.hudSettings.mainStyle.stealthGaugeRimColor, 16), true);
            GlStateManager.color(c.rf(), c.gf(), c.bf(), 1);

            GlStateManager.glBegin(GL_QUADS);
            GlStateManager.glTexCoord2f(STEALTH_GAUGE_RIM_UV_HALF_PIXEL, STEALTH_GAUGE_RIM_UV_HALF_PIXEL);
            GlStateManager.glVertex3f(-halfSize, -halfSize, 0);
            GlStateManager.glTexCoord2f(STEALTH_GAUGE_RIM_UV_HALF_PIXEL, 1f - STEALTH_GAUGE_RIM_UV_HALF_PIXEL);
            GlStateManager.glVertex3f(-halfSize, halfSize, 0);
            GlStateManager.glTexCoord2f(1f - STEALTH_GAUGE_RIM_UV_HALF_PIXEL, 1f - STEALTH_GAUGE_RIM_UV_HALF_PIXEL);
            GlStateManager.glVertex3f(halfSize, halfSize, 0);
            GlStateManager.glTexCoord2f(1f - STEALTH_GAUGE_RIM_UV_HALF_PIXEL, STEALTH_GAUGE_RIM_UV_HALF_PIXEL);
            GlStateManager.glVertex3f(halfSize, -halfSize, 0);
            GlStateManager.glEnd();

            //Arrow
            drawArrow(0, -halfSize, -90, 0.06f, true);
        }
        else if (mode == 2)
        {
            textureManager.bindTexture(STEALTH_GAUGE_TEXTURE_2);

            //Intentionally interpolated by stealth level and not frame, for a smoother and more consistent animation
            float dif = (float) ClientData.stealthLevel - ClientData.prevStealthDisplayed;
            int direction = dif > 0 ? 1 : -1;
            float absLimitedDif = Math.min(partialTickDelta * clientSettings.hudSettings.mainStyle.stealthGaugeSpeed, Math.abs(dif));
            float displayedStealth = ClientData.prevStealthDisplayed + absLimitedDif * direction;
            int index = Tools.min((int) (100 - displayedStealth) >> 2, 49);


            GlStateManager.color(c.rf(), c.gf(), c.bf(), alpha);

            int gridX = index % 10;
            int gridY = index / 10;

            float uvleft = gridX * STEALTH_GAUGE_2_UV_W + STEALTH_GAUGE_2_UV_HALF_PIXEL_W;
            float uvright = (gridX + 1) * STEALTH_GAUGE_2_UV_W - STEALTH_GAUGE_2_UV_HALF_PIXEL_W;
            float uvtop = gridY * STEALTH_GAUGE_2_UV_H + STEALTH_GAUGE_2_UV_HALF_PIXEL_H;
            float uvbottom = (gridY + 1) * STEALTH_GAUGE_2_UV_H - STEALTH_GAUGE_2_UV_HALF_PIXEL_H;

            GlStateManager.glBegin(GL_QUADS);
            GlStateManager.glTexCoord2f(uvleft, uvtop);
            GlStateManager.glVertex3f(-halfSize, -halfSize, 0);
            GlStateManager.glTexCoord2f(uvleft, uvbottom);
            GlStateManager.glVertex3f(-halfSize, halfSize, 0);
            GlStateManager.glTexCoord2f(uvright, uvbottom);
            GlStateManager.glVertex3f(halfSize, halfSize, 0);
            GlStateManager.glTexCoord2f(uvright, uvtop);
            GlStateManager.glVertex3f(halfSize, -halfSize, 0);
            GlStateManager.glEnd();


            ClientData.prevStealthFrameIndex = index;
            ClientData.prevStealthDisplayed = displayedStealth;
        }
        else if (mode == 3)
        {
            event.setCanceled(true);

            textureManager.bindTexture(STEALTH_GAUGE_TEXTURE_3);

            //Intentionally interpolated by stealth level and not frame, for a smoother and more consistent animation
            float dif = (float) ClientData.stealthLevel - ClientData.prevStealthDisplayed;
            if (ClientData.stealthLevel == 100) dif += 80; //When fully stealthed, target stealth display stealth level becomes 140 to account for cursor transition frames (2/7 of frames)
            int direction = dif > 0 ? 1 : -1;
            float absLimitedDif = Math.min(partialTickDelta * clientSettings.hudSettings.mainStyle.stealthGaugeSpeed, Math.abs(dif));
            float displayedStealth = ClientData.prevStealthDisplayed + absLimitedDif * direction;
            int index = Tools.min((int) (180 - displayedStealth) >> 2, 69);


            GlStateManager.color(c.rf(), c.gf(), c.bf(), alpha);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableBlend();

            int gridX = index % 10;
            int gridY = index / 10;

            float uvleft = gridX * STEALTH_GAUGE_3_UV_W + STEALTH_GAUGE_3_UV_HALF_PIXEL_W;
            float uvright = (gridX + 1) * STEALTH_GAUGE_3_UV_W - STEALTH_GAUGE_3_UV_HALF_PIXEL_W;
            float uvtop = gridY * STEALTH_GAUGE_3_UV_H + STEALTH_GAUGE_3_UV_HALF_PIXEL_H;
            float uvbottom = (gridY + 1) * STEALTH_GAUGE_3_UV_H - STEALTH_GAUGE_3_UV_HALF_PIXEL_H;

            GlStateManager.glBegin(GL_QUADS);
            GlStateManager.glTexCoord2f(uvleft, uvtop);
            GlStateManager.glVertex3f(-halfSize, -halfSize, 0);
            GlStateManager.glTexCoord2f(uvleft, uvbottom);
            GlStateManager.glVertex3f(-halfSize, halfSize, 0);
            GlStateManager.glTexCoord2f(uvright, uvbottom);
            GlStateManager.glVertex3f(halfSize, halfSize, 0);
            GlStateManager.glTexCoord2f(uvright, uvtop);
            GlStateManager.glVertex3f(halfSize, -halfSize, 0);
            GlStateManager.glEnd();


            ClientData.prevStealthFrameIndex = index;
            ClientData.prevStealthDisplayed = displayedStealth;
        }

        GlStateManager.popMatrix();
    }

    private static void drawHUD(RenderGameOverlayEvent.Pre event, Minecraft mc)
    {
        //Targeting HUD
        if (targetData != null)
        {
            Entity entity = mc.player.world.getEntityByID(targetData.searcherID);
            if (entity != null) drawTargetingHUD(event, entity, mc.fontRenderer);
        }


        //Main HUD below this point =============================================


        GlStateManager.enableBlend();
        GlStateManager.enableTexture2D();


        if (!MinecraftForge.EVENT_BUS.post(new RenderLightGaugeEvent(event))) drawLightGauge(mc);
        if (DynamicStealthConfig.clientSettings.hudSettings.mainStyle.stealthGaugeMode != 3 && !MinecraftForge.EVENT_BUS.post(new RenderStealthGaugeEvent(event))) drawStealthGauge(mc, clientSettings.hudSettings.mainStyle.stealthGaugeMode, null);
    }

    public static void drawTargetingHUD(RenderGameOverlayEvent.Pre event, Entity entity, FontRenderer fontRenderer)
    {
        try
        {
            //Early cancel in rare case that Render.getEntityXYInWindow returns null
            //TODO can remove this if the method is changed to never return null
            Pair<Float, Float> pos = Render.getEntityXYInWindow(entity, 0, entity.height * 0.5, 0);
            if (pos == null) return;


            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();


            //General Setup
            float originX = pos.getKey(), originY = pos.getValue();

            int portW = Render.getStoredViewportWidth();
            int portH = Render.getStoredViewportHeight();

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
                int viewW = Render.getStoredViewportWidth(), viewH = Render.getStoredViewportHeight();
                double halfViewW = viewW * 0.5, halfViewH = viewH * 0.5;
                double angleRad = TRIG_TABLE.arctanFullcircle(halfViewW, halfViewH, originX, originY);

                double dist = Tools.min(viewW, viewH);
                double originDrawX = (halfViewW + dist * 0.4 * TRIG_TABLE.cos(angleRad)) / sr.getScaleFactor();
                double originDrawY = (halfViewH - dist * 0.4 * TRIG_TABLE.sin(angleRad)) / sr.getScaleFactor();

                if (!MinecraftForge.EVENT_BUS.post(new RenderTargetingHUDEvent.Offscreen(event, boundX / sr.getScaleFactor(), boundY / sr.getScaleFactor(), originDrawX, originDrawY, angleRad, targetData)))
                {
                    Color c;
                    if (clientSettings.hudSettings.targetingStyle.stateColoredArrow)
                    {
                        c = new Color(targetData.color, true);
                    }
                    else c = new Color(Integer.parseInt(clientSettings.hudSettings.targetingStyle.defaultArrowColor, 16), true);
                    GlStateManager.color(c.rf(), c.gf(), c.bf(), (float) clientSettings.hudSettings.targetingStyle.arrowAlpha);

                    drawArrow((float) originDrawX, (float) originDrawY, (float) Tools.radtodeg(angleRad), clientSettings.hudSettings.targetingStyle.arrowSize / Tools.max(ARROW_WIDTH, ARROW_HEIGHT));
                }
            }
            else
            {
                //Onscreen reticle
                float originDrawX = boundX / sr.getScaleFactor();
                float originDrawY = boundY / sr.getScaleFactor();

                if (!MinecraftForge.EVENT_BUS.post(new RenderTargetingHUDEvent.Onscreen(event, originDrawX, originDrawY, targetData)))
                {
                    int color = targetData.color;
                    Color c;
                    if (clientSettings.hudSettings.targetingStyle.stateColoredReticle)
                    {
                        c = new Color(color, true);
                    }
                    else c = new Color(Integer.parseInt(clientSettings.hudSettings.targetingStyle.defaultReticleColor, 16), true);
                    GlStateManager.color(c.rf(), c.gf(), c.bf(), (float) clientSettings.hudSettings.targetingStyle.reticleAlpha);


                    drawReticle(originDrawX, originDrawY);


                    //Text setup
                    int targetID = targetData.targetID;
                    Entity target = (targetID == -1 || targetID == -2) ? null : entity.world.getEntityByID(targetID);

                    float padding = 1;
                    ArrayList<String> elements = new ArrayList<>();

                    if (ClientData.allowTargetingName && clientSettings.hudSettings.targetingStyle.components.name) elements.add(entity.getName());

                    if (ClientData.allowTargetingHP && clientSettings.hudSettings.targetingStyle.components.hp && entity instanceof EntityLivingBase)
                    {
                        EntityLivingBase livingBase = (EntityLivingBase) entity;
                        float hp = livingBase.getHealth();
                        float max = livingBase.getMaxHealth();
                        elements.add(I18n.translateToLocalFormatted(DynamicStealth.MODID + ".hud.hp", ONE_DECIMAL.format(hp), ONE_DECIMAL.format(max), (int) (hp / max * 100)));
                    }

                    if (clientSettings.hudSettings.targetingStyle.components.action)
                    {
                        String action;
                        switch (color)
                        {
                            case COLOR_DAZED:
                                action = I18n.translateToLocalFormatted(DynamicStealth.MODID + ".hud.dazed");
                                break;
                            case COLOR_FLEEING_N0N_PASSIVE:
                            case COLOR_FLEEING_PASSIVE:
                                action = I18n.translateToLocalFormatted(DynamicStealth.MODID + ".hud.fleeFrom", target == null ? I18n.translateToLocalFormatted(DynamicStealth.MODID + ".hud.unknown") : target.getName());
                                break;
                            case COLOR_SEARCHING:
                                action = I18n.translateToLocalFormatted(DynamicStealth.MODID + ".hud.search");
                                break;
                            case COLOR_IDLE_PASSIVE:
                                action = I18n.translateToLocalFormatted(DynamicStealth.MODID + ".hud.idlePassive");
                                break;
                            case COLOR_IDLE_NON_PASSIVE:
                                action = I18n.translateToLocalFormatted(DynamicStealth.MODID + ".hud.idleNonPassive");
                                break;
                            case COLOR_BYPASS:
                                if (target != null) action = I18n.translateToLocalFormatted(DynamicStealth.MODID + ".hud.targeting", target.getName());
                                else action = I18n.translateToLocalFormatted(DynamicStealth.MODID + ".hud.unknown");
                                break;
                            default: //COLOR_ATTACKING_YOU and COLOR_ATTACKING_OTHER
                                action = I18n.translateToLocalFormatted(DynamicStealth.MODID + ".hud.targeting", target == null ? I18n.translateToLocalFormatted(DynamicStealth.MODID + ".hud.unknown") : target.getName());
                        }
                        elements.add(I18n.translateToLocalFormatted(DynamicStealth.MODID + ".hud.action", action));
                    }

                    if (ClientData.allowTargetingThreat && clientSettings.hudSettings.targetingStyle.components.threat)
                    {
                        if (color == COLOR_BYPASS) elements.add(I18n.translateToLocalFormatted(DynamicStealth.MODID + ".hud.threatNotApplicable"));
                        else if (targetData.percent > 0) elements.add(I18n.translateToLocalFormatted(DynamicStealth.MODID + ".hud.threat", targetData.percent));
                    }

                    if (ClientData.allowTargetingDistance && clientSettings.hudSettings.targetingStyle.components.distance)
                    {
                        elements.add(I18n.translateToLocalFormatted(DynamicStealth.MODID + ".hud.distance", ONE_DECIMAL.format(entity.getDistance(Minecraft.getMinecraft().player))));
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
                    if (!clientSettings.hudSettings.targetingStyle.stateColoredText) color = Integer.parseInt(clientSettings.hudSettings.targetingStyle.defaultTextColor, 16);
                    color |= ((int) (0xFF * alpha) << 24);
                    GlStateManager.disableTexture2D();

                    boolean toRight = originX < portW >> 1;
                    if (!toRight) offX = -offX;

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
        }
        catch (IllegalAccessException e)
        {
            MCTools.crash(e, 155, false);
        }
    }
}
