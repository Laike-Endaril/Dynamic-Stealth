package com.fantasticsource.dynamicstealth.common.potions;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import com.fantasticsource.dynamicstealth.server.senses.EntityVisionData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.potion.Potion;

public class PotionSoulSight extends Potion
{
    public PotionSoulSight()
    {
        super(false, 0xFFFF55);
        setPotionName(DynamicStealth.MODID + ".soulSight");
        setRegistryName(DynamicStealth.MODID, "soulSight");
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
}
