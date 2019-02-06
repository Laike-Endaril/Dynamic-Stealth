package com.fantasticsource.dynamicstealth.config.server.items;

import net.minecraftforge.common.config.Config;

public class PotionConfig
{
    @Config.Name("Soul Sight Potions")
    @Config.Comment("Whether to load soul sight potions (accessible from the brewing tab of the creative menu)")
    @Config.RequiresMcRestart
    public boolean soulSightPotion = true;

    @Config.Name("Soul Sight Potion Recipes")
    @Config.Comment("Whether to load the soul sight potion recipe (thick potion + ender eye)")
    @Config.RequiresMcRestart
    public boolean soulSightPotionRecipe = true;

    @Config.Name("Blindness Potions")
    @Config.Comment("Whether to load blindness potions (accessible from the brewing tab of the creative menu)")
    @Config.RequiresMcRestart
    public boolean blindnessPotion = true;

    @Config.Name("Blindness Potion Recipes")
    @Config.Comment("Whether to load the blindness potion recipe (thick potion + ink sac)")
    @Config.RequiresMcRestart
    public boolean blindnessPotionRecipe = true;

    @Config.Name("Glowing Potions")
    @Config.Comment("Whether to load glowing potions (accessible from the brewing tab of the creative menu)")
    @Config.RequiresMcRestart
    public boolean glowingPotion = true;

    @Config.Name("Glowing Potion Recipes")
    @Config.Comment("Whether to load the glowing potion recipe (thick potion + glowstone dust)")
    @Config.RequiresMcRestart
    public boolean glowingPotionRecipe = true;
}
