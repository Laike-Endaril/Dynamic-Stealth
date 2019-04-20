package com.fantasticsource.dynamicstealth.config.server.hud;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class TargetingAllowanceConfig
{
    @Config.Name("030 Allow 'Name' Element")
    @Config.LangKey(DynamicStealth.MODID + ".config.allowTargetName")
    @Config.Comment(
            {
                    "If enabled, clients can see the names of entities",
                    "",
                    "0 means disabled for all players",
                    "1 means enabled for OP players ONLY",
                    "2 means enabled for all players"
            })
    @Config.RangeInt(min = 0, max = 2)
    public int allowNameElement = 2;

    @Config.Name("040 Allow 'HP' Element")
    @Config.LangKey(DynamicStealth.MODID + ".config.allowTargetHP")
    @Config.Comment(
            {
                    "If enabled, clients can see the HP of entities",
                    "",
                    "0 means disabled for all players",
                    "1 means enabled for OP players ONLY",
                    "2 means enabled for all players"
            })
    @Config.RangeInt(min = 0, max = 2)
    public int allowHPElement = 2;

    @Config.Name("050 Allow 'Action' Element")
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

    @Config.Name("060 Allow 'Threat' Element")
    @Config.LangKey(DynamicStealth.MODID + ".config.allowTargetThreat")
    @Config.Comment(
            {
                    "If enabled, clients can see the numerical threat % of entities",
                    "",
                    "0 means disabled for all players",
                    "1 means enabled for OP players ONLY",
                    "2 means enabled for all players"
            })
    @Config.RangeInt(min = 0, max = 2)
    public int allowThreatElement = 2;

    @Config.Name("070 Allow 'Distance' Element")
    @Config.LangKey(DynamicStealth.MODID + ".config.allowTargetDistance")
    @Config.Comment(
            {
                    "If enabled, clients can see the distance to entities",
                    "",
                    "0 means disabled for all players",
                    "1 means enabled for OP players ONLY",
                    "2 means enabled for all players"
            })
    @Config.RangeInt(min = 0, max = 2)
    public int allowDistanceElement = 2;
}
