package com.fantasticsource.dynamicstealth.config.client.hud;

import net.minecraftforge.common.config.Config;

public class TargetingHUDComponents
{
    @Config.Name("Target's Name")
    @Config.Comment("If true, the targeted entity's name is shown (if available)")
    public boolean name = true;

    @Config.Name("Target's Target")
    @Config.Comment("If true, the targeted entity's target is shown (if available)")
    public boolean target = true;

    @Config.Name("Target's Threat")
    @Config.Comment("If true, the targeted entity's threat level is shown (if available)")
    public boolean threat = true;

    @Config.Name("Target's Distance")
    @Config.Comment("If true, the targeted entity's distance is shown (if available)")
    public boolean distance = true;
}
