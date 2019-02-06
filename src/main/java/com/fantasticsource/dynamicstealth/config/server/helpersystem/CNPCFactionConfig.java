package com.fantasticsource.dynamicstealth.config.server.helpersystem;

import net.minecraftforge.common.config.Config;

public class CNPCFactionConfig
{
    @Config.Name("Help If Good Rep")
    @Config.Comment("Help npcs in the same faction and players with good faction rep")
    public boolean helpGoodRep = true;

    @Config.Name("Don't Help If Bad Rep")
    @Config.Comment("Make sure NOT to help those we're hostile to")
    public boolean dontHelpBadRep = true;
}
