package com.fantasticsource.dynamicstealth.config.server.hud;

import net.minecraftforge.common.config.Config;

public class HUDAllowanceConfig
{
    @Config.Name("Allow on-point HUD for Clients")
    @Config.Comment(
            {
                    "If enabled, clients can turn on/off a HUD which appears above each entity",
                    "",
                    "0 means disabled for all players",
                    "1 means enabled for OP players ONLY",
                    "2 means enabled for all players"
            })
    @Config.RangeInt(min = 0, max = 2)
    public int allowOPHUD = 2;

    @Config.Name("Allow Detail HUD for Clients")
    @Config.Comment(
            {
                    "If enabled, clients can see more detailed information in their on-point HUD",
                    "If their on-point HUD is disallowed, this does nothing",
                    "",
                    "0 means disabled for all players",
                    "1 means enabled for OP players ONLY",
                    "2 means enabled for all players"
            })
    @Config.RangeInt(min = 0, max = 2)
    public int allowDetailedOPHUD = 2;

    @Config.Name("Recognize Passives Automatically")
    @Config.Comment("If enabled, clients' threat HUDs will display green for passive mobs.  If disabled, passives appear as idle (blue).")
    public boolean recognizePassive = true;
}
