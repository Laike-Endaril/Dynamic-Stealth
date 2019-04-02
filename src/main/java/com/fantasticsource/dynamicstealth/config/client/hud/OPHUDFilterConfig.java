package com.fantasticsource.dynamicstealth.config.client.hud;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class OPHUDFilterConfig
{
    @Config.Name("Passive")
    @Config.LangKey(DynamicStealth.MODID + ".config.opHUDShowIdlePassive")
    @Config.Comment({"If true, on-point indicators appear for idle passive entities"})
    public boolean showPassive = true; //TODO change to showIdlePassive

    @Config.Name("Bypass")
    @Config.LangKey(DynamicStealth.MODID + ".config.opHUDShowBypass")
    @Config.Comment({"If true, on-point indicators appear for entities that bypass the threat system"})
    public boolean showBypass = true;

    @Config.Name("Idle")
    @Config.LangKey(DynamicStealth.MODID + ".config.opHUDShowIdleNonPassive")
    @Config.Comment({"If true, on-point indicators appear for idle non-passive entities.  This also applies to idle passive entities if the server has passive recognition disabled"})
    public boolean showIdle = true; //TODO change to showIdleNonPassive

    @Config.Name("Attacking Other")
    @Config.LangKey(DynamicStealth.MODID + ".config.opHUDShowAtkOther")
    @Config.Comment({"If true, on-point indicators appear for entities which are attacking something besides you"})
    public boolean showAttackingOther = true;

    @Config.Name("Alert") //TODO on config version change change to, "Searching"
    @Config.LangKey(DynamicStealth.MODID + ".config.opHUDShowSearching")
    @Config.Comment({"If true, on-point indicators appear for alerted entities who are actively searching for a target"})
    public boolean showAlert = true; //TODO on config version change change to, "searching"

    @Config.Name("Attacking You")
    @Config.LangKey(DynamicStealth.MODID + ".config.opHUDShowAtkYou")
    @Config.Comment({"If true, on-point indicators appear for entities that are attacking YOU"})
    public boolean showAttackingYou = true;

    @Config.Name("Flee")
    @Config.LangKey(DynamicStealth.MODID + ".config.opHUDShowFlee")
    @Config.Comment({"If true, on-point indicators appear for non-passive entities that are fleeing"})
    public boolean showFleeing = true; //TODO change to "fleeing"

    @Config.Name("Flee2")
    @Config.LangKey(DynamicStealth.MODID + ".config.opHUDShowFleePassive")
    @Config.Comment({"If true, on-point indicators appear for passive entities that are fleeing"})
    public boolean showFleeingPassive = true;
}
