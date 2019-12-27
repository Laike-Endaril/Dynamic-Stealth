package com.fantasticsource.dynamicstealth.config.client.hud;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class OPHUDFilterConfig
{
    @Config.Name("070 Bypass")
    @Config.LangKey(DynamicStealth.MODID + ".config.opHUDShowBypass")
    @Config.Comment({"If true, on-point indicators appear for entities that bypass the threat system"})
    public boolean bypass = true;

    @Config.Name("071 Idle (Passive)")
    @Config.LangKey(DynamicStealth.MODID + ".config.opHUDShowIdlePassive")
    @Config.Comment({"If true, on-point indicators appear for idle passive entities"})
    public boolean idlePassive = true;

    @Config.Name("072 Idle (Non-Passive)")
    @Config.LangKey(DynamicStealth.MODID + ".config.opHUDShowIdleNonPassive")
    @Config.Comment({"If true, on-point indicators appear for idle non-passive entities.  This also applies to idle passive entities if the server has passive recognition disabled"})
    public boolean idleNonPassive = true;

    @Config.Name("073 Attacking You")
    @Config.LangKey(DynamicStealth.MODID + ".config.opHUDShowAtkYou")
    @Config.Comment({"If true, on-point indicators appear for entities that are attacking YOU"})
    public boolean attackingYou = true;

    @Config.Name("074 Attacking Other")
    @Config.LangKey(DynamicStealth.MODID + ".config.opHUDShowAtkOther")
    @Config.Comment({"If true, on-point indicators appear for entities which are attacking something besides you"})
    public boolean attackingOther = true;

    @Config.Name("075 Searching")
    @Config.LangKey(DynamicStealth.MODID + ".config.opHUDShowSearching")
    @Config.Comment({"If true, on-point indicators appear for alerted entities who are actively searching for a target"})
    public boolean alert = true;

    @Config.Name("076 Fleeing (Passive)")
    @Config.LangKey(DynamicStealth.MODID + ".config.opHUDShowFleePassive")
    @Config.Comment({"If true, on-point indicators appear for passive entities that are fleeing"})
    public boolean fleeingPassive = true;

    @Config.Name("077 Fleeing (Non-Passive)")
    @Config.LangKey(DynamicStealth.MODID + ".config.opHUDShowFlee")
    @Config.Comment({"If true, on-point indicators appear for non-passive entities that are fleeing"})
    public boolean fleeingNonPassive = true;

    @Config.Name("090 Dazed")
    @Config.LangKey(DynamicStealth.MODID + ".config.opHUDShowDazed")
    @Config.Comment({"If true, on-point indicators appear for dazed entities"})
    public boolean dazed = true;
}
