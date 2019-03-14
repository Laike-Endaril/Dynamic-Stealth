package com.fantasticsource.dynamicstealth.config.client.hud;

import net.minecraftforge.common.config.Config;

public class TargetingFilterConfig
{
    @Config.Name("Passive")
    @Config.Comment({"If true, the targeting HUD shows when focused on a passive entity"})
    public boolean showPassive = false;

    @Config.Name("Bypass")
    @Config.Comment({"If true, the targeting HUD shows when focused on an entity that bypasses the threat system"})
    public boolean showBypass = true;

    @Config.Name("Idle")
    @Config.Comment({"If true, the targeting HUD shows when focused on an idle entity"})
    public boolean showIdle = true;

    @Config.Name("Attacking Other")
    @Config.Comment({"If true, the targeting HUD shows when focused on an entity that is attacking something besides you"})
    public boolean showAttackingOther = true;

    @Config.Name("Alert")
    @Config.Comment({"If true, the targeting HUD shows when focused on an entity that is actively searching for a target"})
    public boolean showAlert = true;

    @Config.Name("Attacking You")
    @Config.Comment({"If true, the targeting HUD shows when focused on an entity that is attacking YOU"})
    public boolean showAttackingYou = true;

    @Config.Name("Flee")
    @Config.Comment({"If true, the targeting HUD shows when focused on an entity that is fleeing from combat"})
    public boolean showFleeing = true;

    @Config.Name("Max Distance")
    @Config.Comment({"The maximum distance at which the targeting system will acquire a target"})
    public int maxDist = Integer.MAX_VALUE;

    @Config.Name("Max Angle")
    @Config.Comment(
            {
                    "The maximum angle at which the targeting system will acquire a target",
                    "If set to -1, targeting is disabled"
            })
    @Config.RangeInt(min = -1, max = 180)
    public int maxAngle = 180;
}
