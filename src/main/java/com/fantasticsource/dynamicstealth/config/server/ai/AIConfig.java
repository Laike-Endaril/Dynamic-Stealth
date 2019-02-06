package com.fantasticsource.dynamicstealth.config.server.ai;

import net.minecraftforge.common.config.Config;

public class AIConfig
{
    @Config.Name("Head Turn Speed")
    @Config.Comment({"How quickly entities' heads spin during eg. a search sequence"})
    @Config.RangeInt(min = 1, max = 180)
    public int headTurnSpeed = 3;

    @Config.Name("Entity-Specific Settings (Advanced)")
    public SpecificAIConfig y_entityOverrides = new SpecificAIConfig();

    @Config.Name("Flee")
    public FleeConfig flee = new FleeConfig();

    @Config.Name("Can't Reach")
    public CantReachConfig cantReach = new CantReachConfig();
}
