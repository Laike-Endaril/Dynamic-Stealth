package com.fantasticsource.dynamicstealth.config.server.senses;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class TouchConfig
{
    @Config.Name("Enable Touch")
    @Config.LangKey(DynamicStealth.MODID + ".config.enabletouch")
    @Config.Comment({"If true, entities can feel each other if they bump into one another"})
    public boolean touchEnabled = true;

    @Config.Name("Unfeeling")
    @Config.LangKey(DynamicStealth.MODID + ".config.unfeeling")
    @Config.Comment({"Entities in this list don't notice when something bumps them"})
    public String[] unfeelingEntities = new String[]
            {
                    "zombie",
                    "husk"
            };
}
