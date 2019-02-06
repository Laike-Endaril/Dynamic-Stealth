package com.fantasticsource.dynamicstealth.config.server.ai;

import net.minecraftforge.common.config.Config;

public class CantReachConfig
{
    @Config.Name("Combat Time")
    @Config.Comment("Deciding we can't reach the target requires at least this many ticks in combat")
    public int lastIdleThreshold = 1;

    @Config.Name("No Attack Time")
    @Config.Comment("Deciding we can't reach the target requires at least this many ticks since the last successful attack")
    public int lastAttackThreshold = 200;

    @Config.Name("No Path Time")
    @Config.Comment("Deciding we can't reach the target requires at least this many ticks without a path to the target")
    public int lastPathThreshold = 100;

    @Config.Name("Target Time")
    @Config.Comment("Deciding we can't reach the target requires at least this many ticks with a valid target")
    public int lastNoTargetThreshold = 100;

    @Config.Name("Flee If Unreachable")
    @Config.Comment("If we can't reach the target, do what brave Sir Robin does")
    public boolean flee = false;
}
