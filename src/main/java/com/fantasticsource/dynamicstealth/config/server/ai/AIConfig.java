package com.fantasticsource.dynamicstealth.config.server.ai;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class AIConfig
{
    @Config.Name("Head Turn Speed")
    @Config.LangKey(DynamicStealth.MODID + ".config.aiHeadSpeed")
    @Config.Comment({"How quickly entities' heads spin during eg. a search sequence"})
    @Config.RangeInt(min = 1, max = 180)
    public int headTurnSpeed = 3;

    @Config.Name("Entity-Specific Settings (Advanced)")
    @Config.LangKey(DynamicStealth.MODID + ".config.aiEntitySpecific")
    public SpecificAIConfig y_entityOverrides = new SpecificAIConfig();

    @Config.Name("Flee")
    @Config.LangKey(DynamicStealth.MODID + ".config.aiFlee")
    public FleeConfig flee = new FleeConfig();

    @Config.Name("Can't Reach")
    @Config.LangKey(DynamicStealth.MODID + ".config.aiCantReach")
    public CantReachTriggerConfig cantReach = new CantReachTriggerConfig();

    @Config.Name("Prevent Pet Teleport")
    @Config.LangKey(DynamicStealth.MODID + ".config.aiNoPetTeleport")
    @Config.Comment("If set to true, wolves, cats, and parrots do not teleport while following their owners")
    public boolean preventPetTeleport = true;
}
