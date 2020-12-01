package com.fantasticsource.dynamicstealth.config.client;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class TooltipConfig
{
    @Config.Name("Alter Tooltips")
    @Config.LangKey(DynamicStealth.MODID + ".config.alterTooltips")
    @Config.Comment(
            {
                    "If false, Dynamic Stealth will not touch item tooltips"
            })
    public boolean alterTooltips = true;

    @Config.Name("Always Show Assassination Info")
    @Config.LangKey(DynamicStealth.MODID + ".config.alwaysShowAssassinationInfo")
    @Config.Comment(
            {
                    "If true, tooltips always show assassination info, even if it's just the defaults",
                    "If false, tooltips only show assassination info if it's different from the defaults"
            })
    public boolean alwaysShowAssassinationInfo = true;
}
