package com.fantasticsource.dynamicstealth.common.potions;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Potions
{
    public static final Potion POTION_SOULSIGHT = new PotionSoulSight();
    public static final PotionType POTIONTYPE_SOULSIGHT_NORMAL = new PotionType(DynamicStealth.MODID + ".soulSight", new PotionEffect(POTION_SOULSIGHT, 200)).setRegistryName(DynamicStealth.MODID, "soulSight");

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event)
    {
        event.getRegistry().register(POTION_SOULSIGHT);
    }

    @SubscribeEvent
    public static void registerPotionTypes(RegistryEvent.Register<PotionType> event)
    {
        event.getRegistry().register(POTIONTYPE_SOULSIGHT_NORMAL);
    }
}