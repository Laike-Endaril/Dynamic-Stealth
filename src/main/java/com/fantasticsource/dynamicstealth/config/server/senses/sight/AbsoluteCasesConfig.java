package com.fantasticsource.dynamicstealth.config.server.senses.sight;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class AbsoluteCasesConfig
{
    @Config.Name("See Glowing")
    @Config.LangKey(DynamicStealth.MODID + ".config.seeGlowing")
    @Config.Comment(
            {
                    "If set to true, glowing entities will be seen when inside another entity's FOV, ignoring all other factors",
                    "",
                    "Allows entities to see invisible players who are glowing, but does not remove invisibility; if glowing runs out before invisibility, you're hard to see again"
            })
    public boolean seeGlowing = true;

    @Config.Name("See Burning")
    @Config.LangKey(DynamicStealth.MODID + ".config.seeBurning")
    @Config.Comment("If set to true, burning entities are always considered to be standing in full light")
    public boolean seeBurning = true;
}
