package com.fantasticsource.dynamicstealth.config.server.items;

import net.minecraftforge.common.config.Config;

public class ItemConfig
{
    @Config.Name("Potions")
    public PotionConfig potionSettings = new PotionConfig();
}
