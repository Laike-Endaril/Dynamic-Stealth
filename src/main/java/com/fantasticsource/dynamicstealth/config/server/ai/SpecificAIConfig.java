package com.fantasticsource.dynamicstealth.config.server.ai;

import net.minecraftforge.common.config.Config;

public class SpecificAIConfig
{
    @Config.Name("Head Turn Speed")
    @Config.Comment(
            {
                    "How quickly entities' heads spin during eg. a search sequence",
                    "",
                    "entityID, headTurnSpeed",
                    "",
                    "eg...",
                    "",
                    "minecraft:skeleton, 5"
            })
    @Config.RequiresMcRestart
    public String[] headTurnSpeed = new String[]{"ghast, 10"};
}
