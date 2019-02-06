package com.fantasticsource.dynamicstealth.config.server.senses.sight;

import net.minecraftforge.common.config.Config;

public class SpeedConfig
{
    @Config.Name("Speed (High/Fast)")
    @Config.Comment({"If moving at this speed or above, an entity has the maximum speed penalty to their stealth rating"})
    public double speedHigh = 5.6;

    @Config.Name("Speed (Low/Slow)")
    @Config.Comment({"At or below this speed, an entity has no speed penalty to their stealth rating"})
    public double speedLow = 0;
}
