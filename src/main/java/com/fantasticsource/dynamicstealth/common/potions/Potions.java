package com.fantasticsource.dynamicstealth.common.potions;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import com.fantasticsource.dynamicstealth.common.DynamicStealthConfig;
import com.fantasticsource.dynamicstealth.common.potions.effects.PotionEffectSoulSight;
import com.fantasticsource.dynamicstealth.common.potions.recipes.RecipePotionBlindness;
import com.fantasticsource.dynamicstealth.common.potions.recipes.RecipePotionGlowing;
import com.fantasticsource.dynamicstealth.common.potions.recipes.RecipePotionSoulSight;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Potions
{
    public static final Potion POTION_SOULSIGHT = new PotionEffectSoulSight();
    public static final PotionType POTIONTYPE_SOULSIGHT = new PotionType(DynamicStealth.MODID + ".soulsight", new PotionEffect(POTION_SOULSIGHT, 200)).setRegistryName(DynamicStealth.MODID, "soulsight");
    public static final PotionType POTIONTYPE_BLINDNESS = new PotionType(DynamicStealth.MODID + ".blindness", new PotionEffect(MobEffects.BLINDNESS, 200)).setRegistryName(DynamicStealth.MODID, "blindness");
    public static final PotionType POTIONTYPE_GLOWING = new PotionType(DynamicStealth.MODID + ".glowing", new PotionEffect(MobEffects.GLOWING, 200)).setRegistryName(DynamicStealth.MODID, "glowing");

    @SubscribeEvent
    public static void registerPotionEffects(RegistryEvent.Register<Potion> event)
    {
        if (DynamicStealthConfig.serverSettings.itemSettings.potionSettings.soulSightPotion)
        {
            event.getRegistry().register(POTION_SOULSIGHT);
        }
    }

    @SubscribeEvent
    public static void registerPotionTypes(RegistryEvent.Register<PotionType> event)
    {
        if (DynamicStealthConfig.serverSettings.itemSettings.potionSettings.soulSightPotion)
        {
            event.getRegistry().register(POTIONTYPE_SOULSIGHT);
            if (DynamicStealthConfig.serverSettings.itemSettings.potionSettings.soulSightPotionRecipe)
            {
                BrewingRecipeRegistry.addRecipe(new RecipePotionSoulSight(null, null, null));
            }
        }

        if (DynamicStealthConfig.serverSettings.itemSettings.potionSettings.blindnessPotion)
        {
            event.getRegistry().register(POTIONTYPE_BLINDNESS);
            if (DynamicStealthConfig.serverSettings.itemSettings.potionSettings.blindnessPotionRecipe)
            {
                BrewingRecipeRegistry.addRecipe(new RecipePotionBlindness(null, null, null));
            }
        }

        if (DynamicStealthConfig.serverSettings.itemSettings.potionSettings.glowingPotion)
        {
            event.getRegistry().register(POTIONTYPE_GLOWING);
            if (DynamicStealthConfig.serverSettings.itemSettings.potionSettings.glowingPotionRecipe)
            {
                BrewingRecipeRegistry.addRecipe(new RecipePotionGlowing(null, null, null));
            }
        }
    }
}
