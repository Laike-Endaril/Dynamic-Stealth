package com.fantasticsource.dynamicstealth.config.server.hud;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class HUDAllowanceConfig
{
    @Config.Name("Targeting allowances")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetingAllowances")
    public TargetingAllowanceConfig targeting = new TargetingAllowanceConfig();

    @Config.Name("OPHUD allowances")
    @Config.LangKey(DynamicStealth.MODID + ".config.OPHUDAllowances")
    public OPHUDAllowanceConfig ophud = new OPHUDAllowanceConfig();

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
}
