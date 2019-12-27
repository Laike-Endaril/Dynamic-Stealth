package com.fantasticsource.dynamicstealth.config.client.hud;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class TargetingFilterConfig
{
    @Config.Name("020 Max Distance")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetingMaxDist")
    @Config.Comment({"The maximum distance at which the targeting system will acquire a target"})
    public int maxDist = Integer.MAX_VALUE;

    @Config.Name("030 Max Angle")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetingMaxAngle")
    @Config.Comment(
            {
                    "The maximum angle at which the targeting system will acquire a target",
                    "If set to -1, targeting is disabled"
            })
    @Config.RangeInt(min = -1, max = 180)
    public int maxAngle = 180;

    @Config.Name("070 Bypass")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetingBypass")
    @Config.Comment({"If true, the targeting HUD shows when focused on an entity that bypasses the threat system"})
    public boolean bypass = true;

    @Config.Name("071 Idle (Passive)")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetingIdlePassive")
    @Config.Comment({"If true, the targeting HUD shows when focused on an idle passive entity"})
    public boolean idlePassive = false;

    @Config.Name("072 Idle (Non-Passive)")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetingIdleNonPassive")
    @Config.Comment({"If true, the targeting HUD shows when focused on an idle non-passive entity.  This also applies to idle passive entities if the server has passive recognition disabled"})
    public boolean idleNonPassive = true;

    @Config.Name("073 Attacking You")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetingAtkYou")
    @Config.Comment({"If true, the targeting HUD shows when focused on an entity that is attacking YOU"})
    public boolean attackingYou = true;

    @Config.Name("074 Attacking Other")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetingAtkOther")
    @Config.Comment({"If true, the targeting HUD shows when focused on an entity that is attacking something besides you"})
    public boolean attackingOther = true;

    @Config.Name("075 Searching")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetingSearching")
    @Config.Comment({"If true, the targeting HUD shows when focused on an entity that is actively searching for a target"})
    public boolean alert = true;

    @Config.Name("076 Fleeing (Passive)")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetingFleePassive")
    @Config.Comment({"If true, the targeting HUD shows when focused on a passive entity that is fleeing"})
    public boolean fleeingPassive = false;

    @Config.Name("077 Fleeing (Non-Passive)")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetingFlee")
    @Config.Comment({"If true, the targeting HUD shows when focused on a non-passive entity that is fleeing"})
    public boolean fleeingNonPassive = true;

    @Config.Name("090 Dazed")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetingDazed")
    @Config.Comment({"If true, the targeting HUD shows when focused on a dazed entity"})
    public boolean dazed = true;
}
