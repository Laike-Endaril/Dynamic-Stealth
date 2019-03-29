package com.fantasticsource.dynamicstealth.config.server.hud;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class HUDAllowanceConfig
{
    @Config.Name("Allow On-Point HUD For Clients")
    @Config.LangKey(DynamicStealth.MODID + ".config.allowOPHUD")
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
    @Config.LangKey(DynamicStealth.MODID + ".config.allowTargeting")
    @Config.Comment(
            {
                    "If enabled, clients can see what other entities are targeting",
                    "",
                    "0 means disabled for all players",
                    "1 means enabled for OP players ONLY",
                    "2 means enabled for all players"
            })
    @Config.RangeInt(min = 0, max = 2)
    public int allowTargetElement = 2;

    @Config.Name("Recognize Passives Automatically")
    @Config.LangKey(DynamicStealth.MODID + ".config.allowPassiveRecognition")
    @Config.Comment("If enabled, clients' threat HUDs will display green for passive mobs.  If disabled, passives appear as idle (blue).")
    public boolean recognizePassive = true;

    @Config.Name("Allow Stealth Gauge For Clients")
    @Config.LangKey(DynamicStealth.MODID + ".config.allowStealthGauge")
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

    @Config.Name("OPHUD Range")
    @Config.LangKey(DynamicStealth.MODID + ".config.OPHUDRange")
    @Config.Comment("The maximum distance at which on-point HUDs will appear on clients; impacts server performance if too high")
    @Config.RangeInt(min = 0, max = Integer.MAX_VALUE >> 1)
    public int opHUDRange = 100;

    @Config.Name("OPHUD Update Delay")
    @Config.LangKey(DynamicStealth.MODID + ".config.OPHUDDelay")
    @Config.Comment("How many ticks there are between on-point HUD updates; increasing this will reduce server load")
    @Config.RangeInt(min = 1)
    public int opHUDDelay = 5;
}
