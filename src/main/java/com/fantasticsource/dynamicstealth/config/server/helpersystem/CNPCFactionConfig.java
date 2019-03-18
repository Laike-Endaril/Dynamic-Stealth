package com.fantasticsource.dynamicstealth.config.server.helpersystem;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class CNPCFactionConfig
{
    @Config.Name("Help If Good Rep")
    @Config.LangKey(DynamicStealth.MODID + ".config.helpGoodRep")
    @Config.Comment("Help npcs in the same faction and players with good faction rep")
    public boolean helpGoodRep = true;

    @Config.Name("Don't Help If Bad Rep")
    @Config.LangKey(DynamicStealth.MODID + ".config.dontHelpBadRep")
    @Config.Comment("Make sure NOT to help those we're hostile to")
    public boolean dontHelpBadRep = true;
}
