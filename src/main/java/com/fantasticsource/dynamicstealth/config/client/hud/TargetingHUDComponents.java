package com.fantasticsource.dynamicstealth.config.client.hud;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class TargetingHUDComponents
{
    @Config.Name("010 Name")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetShowName")
    @Config.Comment("If true, the targeted entity's name is shown (if available)")
    public boolean name = true;

    @Config.Name("020 Health")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetShowHP")
    @Config.Comment("If true, the targeted entity's health is shown (if available)")
    public boolean hp = true;

    @Config.Name("030 Action")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetShowAction")
    @Config.Comment("If true, the targeted entity's current action is shown (if available)")
    public boolean action = true;

    @Config.Name("040 Threat")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetShowThreat")
    @Config.Comment("If true, the targeted entity's threat level is shown (if available)")
    public boolean threat = true;

    @Config.Name("050 Distance")
    @Config.LangKey(DynamicStealth.MODID + ".config.targetShowDistance")
    @Config.Comment("If true, the targeted entity's distance is shown (if available)")
    public boolean distance = true;
}
