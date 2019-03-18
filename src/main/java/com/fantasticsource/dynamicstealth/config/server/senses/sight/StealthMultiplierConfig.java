package com.fantasticsource.dynamicstealth.config.server.senses.sight;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class StealthMultiplierConfig
{
    @Config.Name("Crouching Multiplier")
    @Config.LangKey(DynamicStealth.MODID + ".config.crouchMultiplier")
    @Config.Comment(
            {
                    "Multiplies an entity's visibility by this decimal when crouching",
                    "",
                    "If set to 1, there is no effect",
                    "",
                    "If set to 0, crouching entities are invisible (except in special cases)"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double crouchingMultiplier = 0.75;

    @Config.Name("Mob Head Multiplier")
    @Config.LangKey(DynamicStealth.MODID + ".config.mobHeadMultiplier")
    @Config.Comment(
            {
                    "When an entity (including a player) is wearing a mob head, mobs of that type have reduced chance to realize they're a target",
                    "",
                    "If set to 1, there is no effect",
                    "",
                    "If set to 0, mobs of the mob head type cannot notice entities wearing their heads"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double mobHeadMultiplier = 0.5;

    @Config.Name("Invisibility Multiplier")
    @Config.LangKey(DynamicStealth.MODID + ".config.invisibilityMultiplier")
    @Config.Comment(
            {
                    "Invisible entities' visibility is multiplied by this",
                    "",
                    "If set to 1, there is no effect",
                    "",
                    "If set to 0, invisible entities are, uh...invisible"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double invisibilityMultiplier = 0.1;

    @Config.Name("Blindness Multiplier")
    @Config.LangKey(DynamicStealth.MODID + ".config.blindnessMultiplier")
    @Config.Comment(
            {
                    "Blinded entities' detection range is multiplied by this",
                    "",
                    "If set to 1, there is no effect",
                    "",
                    "If set to 0, blind entities can't see"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double blindnessMultiplier = 0.5;
}
