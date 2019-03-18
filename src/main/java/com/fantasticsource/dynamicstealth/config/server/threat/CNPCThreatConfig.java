package com.fantasticsource.dynamicstealth.config.server.threat;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class CNPCThreatConfig
{
    @Config.Name("Faction Threat Bypass")
    @Config.LangKey(DynamicStealth.MODID + ".config.CNPCFactionBypass")
    @Config.Comment("All CNPCs in these factions will bypass the threat system")
    public String[] threatBypassFactions = new String[]{};
}
