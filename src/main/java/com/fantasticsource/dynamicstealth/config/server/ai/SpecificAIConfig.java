package com.fantasticsource.dynamicstealth.config.server.ai;

import com.fantasticsource.dynamicstealth.DynamicStealth;
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
                    "minecraft:skeleton, 5",
                    "",
                    "You can also specify entities with a certain name, like so:",
                    "modid:entity:name"
            })
    public String[] headTurnSpeed = new String[]{"ghast, 10"};
}
