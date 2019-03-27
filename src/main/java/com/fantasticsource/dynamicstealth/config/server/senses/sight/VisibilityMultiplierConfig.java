package com.fantasticsource.dynamicstealth.config.server.senses.sight;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class VisibilityMultiplierConfig
{
    @Config.Name("Armor Multiplier (Cumulative)")
    @Config.LangKey(DynamicStealth.MODID + ".config.armorMultiplier")
    @Config.Comment(
            {
                    "An entity's visibility is multiplied by 1 + (this setting * armor)",
                    "",
                    "If set to 0, there is no effect",
                    "",
                    "If set to 0.25, an entity with 20 armor (full diamond) is 5x as likely to be seen"
            })
    @Config.RangeDouble(min = 0)
    public double armorMultiplierCumulative = 0.25;

    @Config.Name("'Alert' Multiplier")
    @Config.LangKey(DynamicStealth.MODID + ".config.alertMultiplier")
    @Config.Comment(
            {
                    "If an entity is alert, their visual perception is multiplied by this",
                    "",
                    "If set to 1, there is no effect",
                    "",
                    "If set to 2, an alerted entity can generally see targets twice as easily (but still not beyond Distance (Far))"
            })
    @Config.RangeDouble(min = 1)
    public double alertMultiplier = 1.25;

    @Config.Name("'Seen' Multiplier")
    @Config.LangKey(DynamicStealth.MODID + ".config.seenMultiplier")
    @Config.Comment(
            {
                    "If an entity has recently seen its target, their visual perception is multiplied by this",
                    "",
                    "If set to 1, there is no effect",
                    "",
                    "If set to 2, the searcher can generally see targets twice as easily (but still not beyond Distance (Far))"
            })
    @Config.RangeDouble(min = 1)
    public double seenMultiplier = 2;
}
