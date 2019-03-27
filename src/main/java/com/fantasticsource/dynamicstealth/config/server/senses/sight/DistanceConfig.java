package com.fantasticsource.dynamicstealth.config.server.senses.sight;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class DistanceConfig
{
    @Config.Name("Distance (Far)")
    @Config.LangKey(DynamicStealth.MODID + ".config.distanceFar")
    @Config.Comment(
            {
                    "The absolute maximum distance that an entity can see another entity from, in blocks",
                    "",
                    "Exclusive, so if set to 0 mobs NEVER see anything, with a few exceptions, eg...",
                    "",
                    "Zombies will still attack villagers as normal; this uses completely different logic than what I'm accessing atm so I'm not trying to change it for now",
                    "",
                    "Some mobs are not affected at all, for the same reason as stated above, including endermen and the ender dragon"
            })
    @Config.RangeInt(min = 0)
    public int distanceFar = 40;

    @Config.Name("Distance (Near)")
    @Config.LangKey(DynamicStealth.MODID + ".config.distanceNear")
    @Config.Comment(
            {
                    "The distance before an entity's sight starts degrading naturally (even in good conditions),  in blocks",
                    "",
                    "Exclusive, so if set to 0 mobs NEVER see anything, with a few exceptions, eg...",
                    "",
                    "...zombies will still attack villagers as normal; this uses completely different logic than what I'm accessing atm so I'm not trying to change it for now",
                    "",
                    "...some mobs are not affected at all, for the same reason as stated above, including endermen and the ender dragon"
            })
    @Config.RangeInt(min = 0)
    public int distanceNear = 5;
}
