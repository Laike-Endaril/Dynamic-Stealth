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
}
