package com.fantasticsource.dynamicstealth.config.server.items;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class ItemConfig
{
    @Config.Name("Potions")
    @Config.LangKey(DynamicStealth.MODID + ".config.potions")
    public PotionConfig potionSettings = new PotionConfig();
}
