package com.fantasticsource.dynamicstealth.config.client.hud;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class TargetingFilterConfig
{
    @Config.Name("Passive")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetingPassive")
    @Config.Comment({"If true, the targeting HUD shows when focused on a passive entity"})
    public boolean showPassive = false; //TODO change to "passive"

    @Config.Name("Bypass")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetingBypass")
    @Config.Comment({"If true, the targeting HUD shows when focused on an entity that bypasses the threat system"})
    public boolean showBypass = true; //TODO change to "bypass"

    @Config.Name("Idle")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetingIdle")
    @Config.Comment({"If true, the targeting HUD shows when focused on an idle entity"})
    public boolean showIdle = true; //TODO change to "idle"

    @Config.Name("Attacking Other")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetingAtkOther")
    @Config.Comment({"If true, the targeting HUD shows when focused on an entity that is attacking something besides you"})
    public boolean showAttackingOther = true; //TODO change to "attackingOther"

    @Config.Name("Alert")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetingAlert")
    @Config.Comment({"If true, the targeting HUD shows when focused on an entity that is actively searching for a target"})
    public boolean showAlert = true; //TODO change to "alert"

    @Config.Name("Attacking You")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetingAtkYou")
    @Config.Comment({"If true, the targeting HUD shows when focused on an entity that is attacking YOU"})
    public boolean showAttackingYou = true; //TODO change to "attackingYou"

    @Config.Name("Flee")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetingFlee")
    @Config.Comment({"If true, the targeting HUD shows when focused on a non-passive entity that is fleeing"})
    public boolean showFleeing = true; //TODO change to "fleeing"

    @Config.Name("Flee2")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetingFleePassive")
    @Config.Comment({"If true, the targeting HUD shows when focused on a passive entity that is fleeing"})
    public boolean fleeingPassive = false;

    @Config.Name("Max Distance")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetingMaxDist")
    @Config.Comment({"The maximum distance at which the targeting system will acquire a target"})
    public int maxDist = Integer.MAX_VALUE;

    @Config.Name("Max Angle")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetingMaxAngle")
    @Config.Comment(
            {
                    "The maximum angle at which the targeting system will acquire a target",
                    "If set to -1, targeting is disabled"
            })
    @Config.RangeInt(min = -1, max = 180)
    public int maxAngle = 180;
}
