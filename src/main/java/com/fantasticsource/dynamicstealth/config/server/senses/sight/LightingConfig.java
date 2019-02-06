package com.fantasticsource.dynamicstealth.config.server.senses.sight;

import net.minecraftforge.common.config.Config;

public class LightingConfig
{
    @Config.Name("Light (High/Bright)")
    @Config.Comment(
            {
                    "The lowest light level at which entities take no sight penalty",
                    "",
                    "Entities are harder to see in light levels lower than this"
            })
    @Config.RangeInt(min = 0, max = 15)
    public int lightHigh = 8;

    @Config.Name("Light (Low/Dark)")
    @Config.Comment(
            {
                    "At or below this light level, entities cannot be seen at all",
                    "",
                    "Inclusive, so if set to 0, then in 0 lighting, entities cannot be seen by other entities"
            })
    @Config.RangeInt(min = -1, max = 15)
    public int lightLow = -1;

    @Config.Name("Nightvision Bonus")
    @Config.Comment("When an entity has the nightvision effect, this value is added to their perceived light levels (and then set to 15 if larger than 15)")
    @Config.RangeInt(min = 0, max = 15)
    public int nightvisionBonus = 15;
}
