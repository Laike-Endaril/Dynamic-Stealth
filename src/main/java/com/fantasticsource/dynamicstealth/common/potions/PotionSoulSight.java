package com.fantasticsource.dynamicstealth.common.potions;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import com.fantasticsource.dynamicstealth.server.senses.EntityVisionData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionSoulSight extends Potion
{
    private final ResourceLocation iconTexture;

    public PotionSoulSight()
    {
        super(false, 0xFFFF55);
        setPotionName(DynamicStealth.MODID + ".soulSight");
        setRegistryName(DynamicStealth.MODID, "soulSight");
        setBeneficial();
        iconTexture = new ResourceLocation(DynamicStealth.MODID, "potions/soulSight.png");
    }

    @Override
    public boolean isInstant()
    {
        return false;
    }

    @Override
    public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier)
    {
        //Should be called "onPotionStart"
        EntityVisionData.potionSoulSightEntities.add(entityLivingBaseIn);
    }

    @Override
    public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier)
    {
        //Should be called "onPotionEnd"
        EntityVisionData.potionSoulSightEntities.remove(entityLivingBaseIn);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc)
    {
        if (mc.currentScreen != null)
        {
            mc.getTextureManager().bindTexture(iconTexture);
            Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha)
    {
        mc.getTextureManager().bindTexture(iconTexture);
        Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
    }
}
