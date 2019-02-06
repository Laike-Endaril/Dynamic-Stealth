package com.fantasticsource.dynamicstealth.config.server.senses;

import net.minecraftforge.common.config.Config;

public class TouchConfig
{
    @Config.Name("Enable Touch")
    @Config.Comment({"If true, entities can feel each other if they bump into one another"})
    @Config.RequiresMcRestart
    public boolean touchEnabled = true;

    @Config.Name("Unfeeling")
    @Config.Comment({"Entities in this list don't notice when something bumps them"})
    @Config.RequiresMcRestart
    public String[] unfeelingEntities = new String[]
            {
                    "zombie",
                    "husk"
            };
}
