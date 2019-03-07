package com.fantasticsource.dynamicstealth.config.server.hud;

import net.minecraftforge.common.config.Config;

public class HUDAllowanceConfig
{
    @Config.Name("Allow On-Point HUD For Clients")
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

    @Config.Name("Allow Targeting HUD For Clients")
    @Config.Comment(
            {
                    "If enabled, clients can see detailed information for a single, targeted entity",
                    "",
                    "0 means disabled for all players",
                    "1 means enabled for OP players ONLY",
                    "2 means enabled for all players"
            })
    @Config.RangeInt(min = 0, max = 2)
    public int allowTargetingHUD = 2;

    @Config.Name("Recognize Passives Automatically")
    @Config.Comment("If enabled, clients' threat HUDs will display green for passive mobs.  If disabled, passives appear as idle (blue).")
    public boolean recognizePassive = true;

    @Config.Name("Allow Stealth Gauge For Clients")
    @Config.Comment(
            {
                    "If enabled, clients can see their current stealth level in their HUD",
                    "",
                    "0 means disabled for all players",
                    "1 means enabled for OP players ONLY",
                    "2 means enabled for all players"
            })
    @Config.RangeInt(min = 0, max = 2)
    public int allowStealthGauge = 2;
}
