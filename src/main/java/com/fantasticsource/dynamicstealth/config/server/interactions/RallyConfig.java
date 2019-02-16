package com.fantasticsource.dynamicstealth.config.server.interactions;

import net.minecraftforge.common.config.Config;

public class RallyConfig
{
    @Config.Name("Potion Effects")
    @Config.Comment(
            {
                    "Potion effects to apply when something rallies",
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
                    "resistance.200.2"
            };
}
