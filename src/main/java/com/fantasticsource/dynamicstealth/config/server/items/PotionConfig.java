package com.fantasticsource.dynamicstealth.config.server.items;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class PotionConfig
{
    @Config.Name("Soul Sight Potions")
    @Config.LangKey(DynamicStealth.MODID + ".config.potionSoulSight")
    @Config.Comment("Whether to load soul sight potions (accessible from the brewing tab of the creative menu)")
    public boolean soulSightPotion = true;

    @Config.Name("Soul Sight Potion Recipes")
    @Config.LangKey(DynamicStealth.MODID + ".config.potionSoulSightRecipe")
    @Config.Comment("Whether to load the soul sight potion recipe (thick potion + ender eye)")
    public boolean soulSightPotionRecipe = true;

    @Config.Name("Blindness Potions")
    @Config.LangKey(DynamicStealth.MODID + ".config.potionBlindness")
    @Config.Comment("Whether to load blindness potions (accessible from the brewing tab of the creative menu)")
    public boolean blindnessPotion = true;

    @Config.Name("Blindness Potion Recipes")
    @Config.LangKey(DynamicStealth.MODID + ".config.potionBlindnessRecipe")
    @Config.Comment("Whether to load the blindness potion recipe (thick potion + ink sac)")
    public boolean blindnessPotionRecipe = true;

    @Config.Name("Glowing Potions")
    @Config.LangKey(DynamicStealth.MODID + ".config.potionGlowing")
    @Config.Comment("Whether to load glowing potions (accessible from the brewing tab of the creative menu)")
    public boolean glowingPotion = true;

    @Config.Name("Glowing Potion Recipes")
    @Config.LangKey(DynamicStealth.MODID + ".config.potionGlowingRecipe")
    @Config.Comment("Whether to load the glowing potion recipe (thick potion + glowstone dust)")
    public boolean glowingPotionRecipe = true;
}
