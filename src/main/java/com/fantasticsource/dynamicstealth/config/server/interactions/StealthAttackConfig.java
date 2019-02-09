package com.fantasticsource.dynamicstealth.config.server.interactions;

import net.minecraftforge.common.config.Config;

public class StealthAttackConfig
{
    @Config.Name("Armor Penetration")
    @Config.Comment({"Whether stealth attacks bypass armor or not"})
    public boolean armorPenetration = true;

    @Config.Name("Damage Multiplier")
    @Config.Comment("Damage is multiplied by this when attacking from stealth")
    @Config.RangeDouble(min = 1)
    public double damageMultiplier = 1.25;

    @Config.Name("Attacker Effects")
    @Config.Comment(
            {
                    "Potion effects that are applied to the attacker when a stealth attack happens",
                    "",
                    "This applies strength 2 for 200 ticks (10 seconds):",
                    "strength.200.2",
                    "",
                    "This applies soul sight for 100 ticks (5 seconds):",
                    "dynamicstealth:soulsight.100"
            })
    @Config.RequiresMcRestart
    public String[] attackerEffects = {};

    @Config.Name("Victim Effects")
    @Config.Comment(
            {
                    "Potion effects that are applied to the victim when a stealth attack happens",
                    "",
                    "This applies blindness for 100 ticks (5 seconds):",
                    "blindness.100"
            })
    @Config.RequiresMcRestart
    public String[] victimEffects = {};

    @Config.Name("Weapon-Specific Settings")
    @Config.Comment("Weapon-specific overrides for all the settings above")
    @Config.RequiresMcRestart
    public String[] weaponSpecific =
            {
                    "backstab:wood_dagger:0, true, 3",
                    "backstab:stone_dagger:0, true, 3",
                    "backstab:iron_dagger:0, true, 3",
                    "backstab:diamond_dagger:0, true, 3",
                    "backstab:gold_dagger:0, true, 3",
                    "natura:ghostwood_kama:0, true, 2",
                    "natura:bloodwood_kama:0, true, 2",
                    "natura:darkwood_kama:0, true, 2",
                    "natura:fusewood_kama:0, true, 2",
                    "natura:netherquartz_kama:0, true, 2",
                    "tetra:duplex_tool_modular:0 > duplex/sickle_left_material & duplex/butt_right_material, true, 2",
                    "tconstruct:kama:0, true, 2",
                    ", false, 0, , blindness.100",
                    "minecraft:dye:0, false, 0, , blindness.100, true",
            };
}
