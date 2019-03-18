package com.fantasticsource.dynamicstealth.config.server.threat;

import net.minecraftforge.common.config.Config;

public class CNPCThreatConfig
{
    @Config.Name("Faction Threat Bypass")
    @Config.Comment("All CNPCs in these factions will bypass the threat system")
    public String[] threatBypassFactions = new String[]{};
}
