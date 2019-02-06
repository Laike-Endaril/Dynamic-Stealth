package com.fantasticsource.dynamicstealth.config.server.senses.sight;

import net.minecraftforge.common.config.Config;

public class SightConfig
{
    @Config.Name("Stealth Multipliers")
    @Config.Comment(
            {
                    "Contains multipliers that increase stealth / decrease awareness",
                    "",
                    "Whichever of these multipliers is currently giving the best (lowest) multiplier is used"
            })
    public StealthMultiplierConfig a_stealthMultipliers = new StealthMultiplierConfig();
    @Config.Name("Visibility Multipliers")
    @Config.Comment(
            {
                    "Contains multipliers that decrease stealth / increase awareness",
                    "",
                    "Whichever of these multipliers is currently giving the worst (highest) multiplier is used"
            })
    public VisibilityMultiplierConfig b_visibilityMultipliers = new VisibilityMultiplierConfig();
    @Config.Name("Lighting")
    @Config.Comment({"How much of an effect lighting has on stealth.  Nightvision is in here as well"})
    public LightingConfig c_lighting = new LightingConfig();
    @Config.Name("Speed")
    @Config.Comment({"How much of an effect an entity's speed has on stealth"})
    public SpeedConfig d_speeds = new SpeedConfig();
    @Config.Name("Angle")
    @Config.Comment({"FOV angles"})
    public AngleConfig e_angles = new AngleConfig();
    @Config.Name("Distance")
    @Config.Comment({"FOV distances"})
    public DistanceConfig f_distances = new DistanceConfig();
    @Config.Name("Absolute Cases")
    @Config.Comment({"Special cases, eg. glowing"})
    public AbsoluteCasesConfig g_absolutes = new AbsoluteCasesConfig();
    @Config.Name("Entity-Specific Settings (Advanced)")
    public SpecificSightConfig y_entityOverrides = new SpecificSightConfig();
}
