package com.fantasticsource.dynamicstealth.config.server.items;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class ItemConfig
{
    @Config.Name("Potions")
    @Config.LangKey(DynamicStealth.MODID + ".config.potions")
    public PotionConfig potionSettings = new PotionConfig();

    @Config.Name("Hand Mirror Recipe")
    @Config.LangKey(DynamicStealth.MODID + ".config.handMirrorRecipe")
    @Config.Comment("Whether to load the hand mirror recipe (glass pane above iron ingot)")
    public boolean handMirrorRecipe = true;
}
