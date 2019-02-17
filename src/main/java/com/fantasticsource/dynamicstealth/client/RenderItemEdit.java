package com.fantasticsource.dynamicstealth.client;

import com.fantasticsource.dynamicstealth.common.ClientData;
import com.fantasticsource.dynamicstealth.config.DynamicStealthConfig;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.List;

public class RenderItemEdit extends RenderItem
{
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private static Minecraft mc = Minecraft.getMinecraft();
    private static TextureManager textureManager = mc.getTextureManager();
    private static ItemColors itemColors = mc.getItemColors();

    private RenderItemEdit(TextureManager textureManager, ModelManager modelManager, ItemColors itemColors)
    {
        super(textureManager, modelManager, itemColors);
    }

    public static void init()
    {
        try
        {
            Field itemRendererRenderItemField = ReflectionTool.getField(true, ItemRenderer.class, "field_178112_h", "field_73841_b", "field_78516_c", "field_147709_v", "field_175620_Y", "field_177074_h", "field_177080_a", "field_177083_e", "itemRenderer");
            Field minecraftModelManagerField = ReflectionTool.getField(true, Minecraft.class, "field_175617_aL", "field_178090_d", "field_178128_c", "modelManager");
            //noinspection ConstantConditions
            itemRendererRenderItemField.set(mc.getItemRenderer(), new RenderItemEdit(mc.getTextureManager(), (ModelManager) minecraftModelManagerField.get(mc), mc.getItemColors()));
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().exitJava(152, true);
        }
    }

    @Override
    public void renderItem(ItemStack stack, EntityLivingBase livingBase, ItemCameraTransforms.TransformType transform, boolean leftHanded)
    {
        if (!stack.isEmpty() && livingBase != null)
        {
            IBakedModel ibakedmodel = getItemModelWithOverrides(stack, livingBase.world, livingBase);
            renderItemModel(stack, ibakedmodel, transform, leftHanded, livingBase);
        }
    }

    protected void renderItemModel(ItemStack stack, IBakedModel bakedmodel, ItemCameraTransforms.TransformType transform, boolean leftHanded, EntityLivingBase livingBase)
    {
        if (!stack.isEmpty())
        {
            textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.enableRescaleNormal();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.pushMatrix();
            bakedmodel = ForgeHooksClient.handleCameraTransforms(bakedmodel, transform, leftHanded);
            renderItem(stack, bakedmodel, livingBase);
            GlStateManager.cullFace(GlStateManager.CullFace.BACK);
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        }
    }

    public void renderItem(ItemStack stack, @Nonnull IBakedModel model, EntityLivingBase livingBase)
    {
        if (!stack.isEmpty())
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

            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);

            if (model.isBuiltInRenderer())
            {
                GlStateManager.color(1, 1, 1, (float) (min + (1d - min) * visibility));
                GlStateManager.enableRescaleNormal();
                stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack);
            }
            else
            {
                int alpha = Tools.min(0xFF, (int) ((min + (1d - min) * visibility) * 0xFF));
                renderModel(model, 0xFFFFFF | (alpha << 24), stack);

                if (stack.hasEffect())
                {
                    renderEffect(model);
                }
            }

            GlStateManager.popMatrix();
        }
    }

    private void renderModel(IBakedModel model, int color, ItemStack stack)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.ITEM);

        for (EnumFacing enumfacing : EnumFacing.values())
        {
            renderQuads(bufferbuilder, model.getQuads(null, enumfacing, 0), color, stack);
        }

        renderQuads(bufferbuilder, model.getQuads(null, null, 0), color, stack);
        tessellator.draw();
    }

    private void renderEffect(IBakedModel model)
    {
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        textureManager.bindTexture(RES_ITEM_GLINT);
        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        GlStateManager.scale(8, 8, 8);
        float f = (float) (Minecraft.getSystemTime() % 3000L) / 3000f / 8f;
        GlStateManager.translate(f, 0, 0);
        GlStateManager.rotate(-50, 0, 0, 1);
        renderModel(model, 0xFF8040CC, ItemStack.EMPTY);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(8, 8, 8);
        float f1 = (float) (Minecraft.getSystemTime() % 4873L) / 4873f / 8f;
        GlStateManager.translate(-f1, 0, 0);
        GlStateManager.rotate(10, 0, 0, 1);
        renderModel(model, 0xFF8040CC, ItemStack.EMPTY);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    }

    private void renderQuads(BufferBuilder renderer, List<BakedQuad> quads, int color, ItemStack stack)
    {
        for (BakedQuad bakedquad : quads)
        {
            if ((color == -1 && !stack.isEmpty()) && bakedquad.hasTintIndex())
            {
                color = itemColors.colorMultiplier(stack, bakedquad.getTintIndex());
                if (EntityRenderer.anaglyphEnable) color = TextureUtil.anaglyphColor(color);
                color |= 0xFF000000;
            }

            LightUtil.renderQuadColor(renderer, bakedquad, color);
        }
    }
}
