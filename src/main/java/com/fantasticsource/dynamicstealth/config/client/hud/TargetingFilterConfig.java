package com.fantasticsource.dynamicstealth.config.client.hud;

import net.minecraftforge.common.config.Config;

public class TargetingFilterConfig
{
    @Config.Name("Passive")
    @Config.Comment({"If true, the detail HUD shows when focused on a passive entity"})
    public boolean showPassive = false;

    @Config.Name("Bypass")
    @Config.Comment({"If true, the detail HUD shows when focused on an entity that bypasses the threat system"})
    public boolean showBypass = true;

    @Config.Name("Idle")
    @Config.Comment({"If true, the detail HUD shows when focused on an idle entity"})
    public boolean showIdle = true;

    @Config.Name("Attacking Other")
    @Config.Comment({"If true, the detail HUD shows when focused on an entity that is attacking something besides you"})
    public boolean showAttackingOther = true;

    @Config.Name("Alert")
    @Config.Comment({"If true, the detail HUD shows when focused on an entity that is actively searching for a target"})
    public boolean showAlert = true;

    @Config.Name("Attacking You")
    @Config.Comment({"If true, the detail HUD shows when focused on an entity that is attacking YOU"})
    public boolean showAttackingYou = true;

    @Config.Name("Flee")
    @Config.Comment({"If true, the detail HUD shows when focused on an entity that is fleeing from combat"})
    public boolean showFleeing = true;
}
