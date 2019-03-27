package com.fantasticsource.dynamicstealth.config.server.senses.sight;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class SightConfig
{
    @Config.Name("Stealth Multipliers")
    @Config.LangKey(DynamicStealth.MODID + ".config.stealthMultipliers")
    @Config.Comment(
            {
                    "Contains multipliers that increase stealth / decrease awareness",
                    "",
                    "Whichever of these multipliers is currently giving the best (lowest) multiplier is used"
            })
    public StealthMultiplierConfig a_stealthMultipliers = new StealthMultiplierConfig();

    @Config.Name("Visibility Multipliers")
    @Config.LangKey(DynamicStealth.MODID + ".config.visibilityMultipliers")
    @Config.Comment(
            {
                    "Contains multipliers that decrease stealth / increase awareness",
                    "",
                    "Whichever of these multipliers is currently giving the worst (highest) multiplier is used"
            })
    public VisibilityMultiplierConfig b_visibilityMultipliers = new VisibilityMultiplierConfig();

    @Config.Name("Lighting")
    @Config.LangKey(DynamicStealth.MODID + ".config.lighting")
    @Config.Comment({"How much of an effect lighting has on stealth.  Nightvision is in here as well"})
    public LightingConfig c_lighting = new LightingConfig();

    @Config.Name("Angle")
    @Config.LangKey(DynamicStealth.MODID + ".config.angle")
    @Config.Comment({"FOV angles"})
    public AngleConfig e_angles = new AngleConfig();

    @Config.Name("Distance")
    @Config.LangKey(DynamicStealth.MODID + ".config.distance")
    @Config.Comment({"FOV distances"})
    public DistanceConfig f_distances = new DistanceConfig();

    @Config.Name("Absolute Cases")
    @Config.LangKey(DynamicStealth.MODID + ".config.absolutes")
    @Config.Comment({"Special cases, eg. glowing"})
    public AbsoluteCasesConfig g_absolutes = new AbsoluteCasesConfig();

    @Config.Name("Entity-Specific Settings (Advanced)")
    @Config.LangKey(DynamicStealth.MODID + ".config.entitySpecificSight")
    public SpecificSightConfig y_entityOverrides = new SpecificSightConfig();
}
