package com.fantasticsource.dynamicstealth.config.server.senses.sight;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class LightingConfig
{
    @Config.Name("000 Light Level (High/Bright)")
    @Config.LangKey(DynamicStealth.MODID + ".config.lightLevelHigh")
    @Config.Comment("At or above this light level, entities receive the maximum light level multiplier")
    @Config.RangeInt(min = 0, max = 15)
    public int lightLevelHigh = 11;

    @Config.Name("020 Multiplier (High/Bright)")
    @Config.LangKey(DynamicStealth.MODID + ".config.lightMultHigh")
    @Config.Comment(
            {
                    "The light level visibility multiplier when standing in bright areas",
                    "0 means invisible, 1 means fully visible (other factors not accounted for)"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double lightMultHigh = 1;

    @Config.Name("040 Light Level (Low/Dark)")
    @Config.LangKey(DynamicStealth.MODID + ".config.lightLevelLow")
    @Config.Comment("At or below this light level, entities receive the minimum light level multiplier")
    @Config.RangeInt(min = 0, max = 15)
    public int lightLevelLow = 0;

    @Config.Name("060 Multiplier (Low/Dark)")
    @Config.LangKey(DynamicStealth.MODID + ".config.lightMultLow")
    @Config.Comment(
            {
                    "The light level visibility multiplier when standing in dark areas",
                    "0 means invisible, 1 means fully visible (other factors not accounted for)"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double lightMultLow = 0.1;

    @Config.Name("080 Nightvision Bonus")
    @Config.LangKey(DynamicStealth.MODID + ".config.nightvisionBonus")
    @Config.Comment("When an entity has the nightvision effect, this value is added to their perceived light levels (and then set to 15 if larger than 15)")
    @Config.RangeInt(min = 0, max = 15)
    public int nightvisionBonus = 15;
}
