package com.fantasticsource.dynamicstealth.config.client.hud;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class TargetingHUDComponents
{
    @Config.Name("Target's Name")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetShowName")
    @Config.Comment("If true, the targeted entity's name is shown (if available)")
    public boolean name = true;

    @Config.Name("Target's Target") //TODO on config version change change to "Target's Action"
    @Config.LangKey(DynamicStealth.MODID + ".config.targetShowAction")
    @Config.Comment("If true, the targeted entity's current action is shown (if available)")
    public boolean target = true; //TODO on config version change change to "action"

    @Config.Name("Target's Threat")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetShowThreat")
    @Config.Comment("If true, the targeted entity's threat level is shown (if available)")
    public boolean threat = true;

    @Config.Name("Target's Distance")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetShowDistance")
    @Config.Comment("If true, the targeted entity's distance is shown (if available)")
    public boolean distance = true;
}
