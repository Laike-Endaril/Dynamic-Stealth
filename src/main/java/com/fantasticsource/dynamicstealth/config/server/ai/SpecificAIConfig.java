package com.fantasticsource.dynamicstealth.config.server.ai;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import com.fantasticsource.dynamicstealth.server.ai.EntityAIDefaults;
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

    @Config.Name("Entity-Specific Flee Threshold")
    @Config.LangKey(DynamicStealth.MODID + ".config.entitySpecificFleeThreshold")
    @Config.Comment(
            {
                    "The % of health at which entities start to flee",
                    "",
                    "entityID, fleeThreshold",
                    "",
                    "eg...",
                    "",
                    "minecraft:skeleton, 70",
                    "",
                    "You can also specify entities with a certain name, like so:",
                    "modid:entity:name"
            })
    public String[] specificFleeThresholds = EntityAIDefaults.fleeThresholdDefaults.toArray(new String[0]);
}
