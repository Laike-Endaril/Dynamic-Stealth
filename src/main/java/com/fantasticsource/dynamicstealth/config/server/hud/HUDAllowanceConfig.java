package com.fantasticsource.dynamicstealth.config.server.hud;

import net.minecraftforge.common.config.Config;

public class HUDAllowanceConfig
{
    @Config.Name("Allow detailed HUD on clients")
    @Config.Comment(
            {
                    "If enabled, clients are allowed to turn on a HUD for displaying detailed threat information for a single target",
                    "",
                    "0 means disabled for all players",
                    "1 means enabled for OP players ONLY",
                    "2 means enabled for all players"
            })
    @Config.RangeInt(min = 0, max = 2)
    public int allowClientDetailHUD = 2;

    @Config.Name("On-Point HUD for normal players")
    @Config.Comment(
            {
                    "Controls how the on-point, per-entity threat HUD can be used on clients (for normal/non-OP players)",
                    "",
                    "0 means disabled",
                    "1 means enabled for targeted entity ONLY",
                    "2 means enabled for all seen entities"
            })
    @Config.RangeInt(min = 0, max = 2)
    public int normalOnPointHUD = 2;

    @Config.Name("On-Point HUD for OP players")
    @Config.Comment(
            {
                    "Controls how the on-point, per-entity threat HUD can be used on clients (for OP players)",
                    "",
                    "0 means disabled",
                    "1 means enabled for targeted entity ONLY",
                    "2 means enabled for all seen entities"
            })
    @Config.RangeInt(min = 0, max = 2)
    public int opOnPointHUD = 2;

}
