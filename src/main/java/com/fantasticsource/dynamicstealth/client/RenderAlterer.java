package com.fantasticsource.dynamicstealth.client;

import com.fantasticsource.dynamicstealth.common.ClientData;
import com.fantasticsource.dynamicstealth.compat.Compat;
import com.fantasticsource.dynamicstealth.config.DynamicStealthConfig;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class RenderAlterer
{
    private static ArrayList<EntityLivingBase> soulSightCache = new ArrayList<>();


    private static float interpolateRotation(float prevYawOffset, float yawOffset, float partialTicks)
    {
        float f;
        for (f = yawOffset - prevYawOffset; f < -180.0F; f += 360.0F) ;
        while (f >= 180.0F) f -= 360.0F;
        return prevYawOffset + partialTicks * f;
    }

    private static void renderGlow(RenderLivingBase renderLivingBase, EntityLivingBase livingBase, double x, double y, double z, float partialTicks, int color) throws Exception
    {
        //RenderManager#renderEntity()
        renderLivingBase.setRenderOutlines(true);

        ModelBase mainModel = (ModelBase) ReflectionTool.getField(RenderLivingBase.class, "field_77045_g", "mainModel").get(renderLivingBase);

        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        mainModel.swingProgress = livingBase.getSwingProgress(partialTicks);
        boolean shouldSit = livingBase.isRiding() && (livingBase.getRidingEntity() != null && livingBase.getRidingEntity().shouldRiderSit());
        mainModel.isRiding = shouldSit;
        mainModel.isChild = livingBase.isChild();

        try
        {
            float f = interpolateRotation(livingBase.prevRenderYawOffset, livingBase.renderYawOffset, partialTicks);
            float f1 = interpolateRotation(livingBase.prevRotationYawHead, livingBase.rotationYawHead, partialTicks);
            float f2 = f1 - f;

            if (shouldSit && livingBase.getRidingEntity() instanceof EntityLivingBase)
            {
                EntityLivingBase entitylivingbase = (EntityLivingBase) livingBase.getRidingEntity();
                f = interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, partialTicks);
                f2 = f1 - f;
                float f3 = MathHelper.wrapDegrees(f2);
                if (f3 < -85.0F) f3 = -85.0F;
                if (f3 >= 85.0F) f3 = 85.0F;
                f = f1 - f3;
                if (f3 * f3 > 2500.0F) f += f3 * 0.2F;
                f2 = f1 - f;
            }

            float f7 = livingBase.prevRotationPitch + (livingBase.rotationPitch - livingBase.prevRotationPitch) * partialTicks;
            ReflectionTool.getMethod(RenderLivingBase.class, "func_77039_a", "renderLivingAt").invoke(renderLivingBase, livingBase, x, y, z);
            float f8 = (float) ReflectionTool.getMethod(RenderLivingBase.class, "func_77044_a", "handleRotationFloat").invoke(renderLivingBase, livingBase, partialTicks);
            ReflectionTool.getMethod(RenderLivingBase.class, "func_77043_a", "applyRotations").invoke(renderLivingBase, livingBase, f8, f, partialTicks);
            float f4 = (float) ReflectionTool.getMethod(RenderLivingBase.class, "func_188322_c", "prepareScale").invoke(renderLivingBase, livingBase, partialTicks);
            float f5 = 0.0F;
            float f6 = 0.0F;

            if (!livingBase.isRiding())
            {
                f5 = livingBase.prevLimbSwingAmount + (livingBase.limbSwingAmount - livingBase.prevLimbSwingAmount) * partialTicks;
                f6 = livingBase.limbSwing - livingBase.limbSwingAmount * (1.0F - partialTicks);

                if (livingBase.isChild())
                {
                    f6 *= 3.0F;
                }

                if (f5 > 1.0F)
                {
                    f5 = 1.0F;
                }
                f2 = f1 - f; // Forge: Fix MC-1207
            }

            GlStateManager.enableAlpha();
            mainModel.setLivingAnimations(livingBase, f6, f5, partialTicks);
            mainModel.setRotationAngles(f6, f5, f8, f2, f7, f4, livingBase);

            GlStateManager.disableLighting();
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.disableTexture2D();
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(color);

            ReflectionTool.getMethod(RenderLivingBase.class, "func_177093_a", "renderLayers").invoke(renderLivingBase, livingBase, f6, f5, partialTicks, f8, f2, f7, f4);

            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();

            GlStateManager.enableLighting();
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.enableTexture2D();
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

            GlStateManager.disableRescaleNormal();
        }
        catch (Exception e)
        {
            MCTools.crash(e, 156, false);
        }

        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    private static void renderGlow2(RenderLivingBase renderLivingBase, EntityLivingBase livingBase, double x, double y, double z, float partialTicks, int color) throws Exception
    {
        //Part of RenderGlobal#renderEntities()
        RenderManager manager = renderLivingBase.getRenderManager();
        RenderGlobal global = Minecraft.getMinecraft().renderGlobal;

        GlStateManager.depthFunc(519);
        GlStateManager.disableFog();
        ((Framebuffer) ReflectionTool.getField(RenderGlobal.class, "field_175015_z", "entityOutlineFramebuffer").get(global)).bindFramebuffer(false);
        RenderHelper.disableStandardItemLighting();
        manager.setRenderOutlines(true);


        {
            //RenderManager#renderEntityStatic()
            if (livingBase.ticksExisted == 0)
            {
                livingBase.lastTickPosX = livingBase.posX;
                livingBase.lastTickPosY = livingBase.posY;
                livingBase.lastTickPosZ = livingBase.posZ;
            }

            int i = livingBase.getBrightnessForRender();

            if (livingBase.isBurning())
            {
                i = 15728880;
            }

            int j = i % 65536;
            int k = i / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            {
                //Part of RenderManager#renderEntity()
                Render<Entity> render = manager.getEntityRenderObject(livingBase);

                if (render != null && manager.renderEngine != null)
                {
                    render.setRenderOutlines(true);

                    {
                        //RenderLivingBase#doRender()
                        renderGlow(renderLivingBase, livingBase, x, y, z, partialTicks, color);
                    }
                }
            }
        }


        manager.setRenderOutlines(false);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.depthMask(false);
        ((ShaderGroup) ReflectionTool.getField(RenderGlobal.class, "field_174991_A", "entityOutlineShader").get(global)).render(partialTicks);
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.enableFog();
        GlStateManager.enableBlend();
        GlStateManager.enableColorMaterial();
        GlStateManager.depthFunc(515);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
    }

    @SubscribeEvent
    public static void preRender(RenderLivingEvent.Pre event) throws Exception
    {
        //Don't draw seen entities as invisible, because they've been SEEN
        EntityLivingBase livingBase = event.getEntity();
        if (Compat.statues && livingBase.getClass().getName().contains("party.lemons.statue")) return;
        livingBase.setInvisible(false);


        //Remove glowing from soul sight
        if (soulSightCache.contains(livingBase))
        {
            soulSightCache.remove(livingBase);
            livingBase.setGlowing(false);
        }

        //Glowing effect for the focused target and for when the player has soul sight
        if (ClientData.detailData != null && livingBase.getEntityId() == ClientData.detailData.searcherID)
        {
            renderGlow(event.getRenderer(), event.getEntity(), event.getX(), event.getY(), event.getZ(), event.getPartialRenderTick(), 0xFFFFFFFF);
//            renderGlow2(event.getRenderer(), event.getEntity(), event.getX(), event.getY(), event.getZ(), event.getPartialRenderTick(), 0xFFFFFFFF);
        }
        else if (ClientData.soulSight && !livingBase.isGlowing())
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

            GlStateManager.color(1, 1, 1, 1);
            GL11.glColor4f(1, 1, 1, (float) (min + (1d - min) * visibility));
        }
    }

    @SubscribeEvent
    public static void postRender(RenderLivingEvent.Post event)
    {
        EntityLivingBase livingBase = event.getEntity();
        if (Compat.statues && livingBase.getClass().getName().contains("party.lemons.statue")) return;
        GlStateManager.color(1, 1, 1, 1);
        GL11.glColor4f(1, 1, 1, 1);
        GlStateManager.disableBlend();
    }
}
