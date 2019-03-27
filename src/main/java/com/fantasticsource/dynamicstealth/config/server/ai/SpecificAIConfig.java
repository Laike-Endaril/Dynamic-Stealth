package com.fantasticsource.dynamicstealth.config.server.ai;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class SpecificAIConfig
{
    @Config.Name("Head Turn Speed")
    @Config.LangKey(DynamicStealth.MODID + ".config.entitySpecificHeadSpeed")
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
    public String[] headTurnSpeed = new String[]{"ghast, 10"};
}
