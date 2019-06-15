package com.fantasticsource.dynamicstealth.config.server.ai;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class CantReachTriggerConfig
{
    @Config.Name("Combat Time")
    @Config.LangKey(DynamicStealth.MODID + ".config.cantReachCombatTime")
    @Config.Comment("Deciding we can't reach the target requires at least this many ticks in combat")
    public int lastIdleThreshold = 1;

    @Config.Name("No Attack Time")
    @Config.LangKey(DynamicStealth.MODID + ".config.cantReachNoAttackTime")
    @Config.Comment("Deciding we can't reach the target requires at least this many ticks since the last successful attack")
    public int lastAttackThreshold = 200;

    @Config.Name("No Path Time")
    @Config.LangKey(DynamicStealth.MODID + ".config.cantReachNoPathTime")
    @Config.Comment("Deciding we can't reach the target requires at least this many ticks without a path to the target")
    public int lastPathThreshold = 100;

    @Config.Name("Target Time")
    @Config.LangKey(DynamicStealth.MODID + ".config.cantReachTargetTime")
    @Config.Comment("Deciding we can't reach the target requires at least this many ticks with a valid target")
    public int lastNoTargetThreshold = 100;

    @Config.Name("Flee If Unreachable")
    @Config.LangKey(DynamicStealth.MODID + ".config.cantReachFlee")
    @Config.Comment("If we can't reach the target, do what brave Sir Robin does")
    public boolean flee = false;

    @Config.Name("Potion Filter")
    @Config.LangKey(DynamicStealth.MODID + ".config.cantReachPotionFilter")
    @Config.Comment(
            {
                    "An entity will not go into 'can't reach' mode if it has one of these potion effects",
                    "",
                    "This prevents 'can't reach' mode if the entity has slowness level 99:",
                    "slowness.99",
                    "",
                    "This prevents 'can't reach' mode if the entity has any level of levitation:",
                    "levitation.*"
            })
    public String[] potionFilter = new String[0];
}
