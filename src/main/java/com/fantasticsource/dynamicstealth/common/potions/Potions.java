package com.fantasticsource.dynamicstealth.common.potions;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Potions
{
    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event)
    {
        event.getRegistry().register(new PotionSoulSight());
    }

    @SubscribeEvent
    public static void registerPotionTypes(RegistryEvent.Register<PotionType> event)
    {
        event.getRegistry().register(new PotionType(DynamicStealth.MODID + ".soulsight", new PotionEffect(new PotionSoulSight(), 200)).setRegistryName(DynamicStealth.MODID, "soulSight"));
    }
}
