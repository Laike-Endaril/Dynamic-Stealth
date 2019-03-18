package com.fantasticsource.dynamicstealth.config.server.interactions;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class DesperationConfig
{
    @Config.Name("Potion Effects")
    @Config.LangKey(DynamicStealth.MODID + ".config.desperationPotions")
    @Config.Comment(
            {
                    "Potion effects to apply when something gets cornered and desperate",
                    "",
                    "This applies strength 2 for 200 ticks (10 seconds):",
                    "strength.200.2",
                    "",
                    "This applies soul sight for 100 ticks (5 seconds):",
                    "dynamicstealth:soulsight.100"
            })
    @Config.RequiresMcRestart
    public String[] potionEffects =
            {
                    "strength.200.3"
            };
}
