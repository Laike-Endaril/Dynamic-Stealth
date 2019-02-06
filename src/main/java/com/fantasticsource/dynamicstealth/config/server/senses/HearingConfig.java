package com.fantasticsource.dynamicstealth.config.server.senses;

import net.minecraftforge.common.config.Config;

public class HearingConfig
{
    @Config.Name("Warning Range")
    @Config.Comment("How far away entities can hear warnings, by default")
    public double warningRange = 30;

    @Config.Name("No LOS Multiplier")
    @Config.Comment("If the observer doesn't have LOS to the source of the sound, its hearing range is multiplied by this")
    public double noLOSMultiplier = 0.5;
}
