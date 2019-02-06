package com.fantasticsource.dynamicstealth.config.client.hud;

import net.minecraftforge.common.config.Config;

public class OPHUDFilterConfig
{
    @Config.Name("Passive")
    @Config.Comment({"If true, on-point indicators appear for passive mobs"})
    public boolean showPassive = true;

    @Config.Name("Bypass")
    @Config.Comment({"If true, on-point indicators appear for mobs that bypass the threat system"})
    public boolean showBypass = true;

    @Config.Name("Idle")
    @Config.Comment({"If true, on-point indicators appear for idle mobs"})
    public boolean showIdle = true;

    @Config.Name("Attacking Other")
    @Config.Comment({"If true, on-point indicators appear for mobs which are attacking something besides you"})
    public boolean showAttackingOther = true;

    @Config.Name("Alert")
    @Config.Comment({"If true, on-point indicators appear for alerted mobs who are actively searching for a target"})
    public boolean showAlert = true;

    @Config.Name("Attacking You")
    @Config.Comment({"If true, on-point indicators appear for mobs that are attacking YOU"})
    public boolean showAttackingYou = true;

    @Config.Name("Flee")
    @Config.Comment({"If true, on-point indicators appear for mobs that are fleeing from combat"})
    public boolean showFleeing = true;
}
