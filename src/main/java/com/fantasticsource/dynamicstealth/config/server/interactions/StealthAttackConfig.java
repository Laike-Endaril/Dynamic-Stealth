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
    @Config.Comment("Potion effects that are applied to the attacker when a stealth attack happens")
    public String[] attackerEffects = {};

    @Config.Name("Victim Effects")
    @Config.Comment("Potion effects that are applied to the victim when a stealth attack happens")
    public String[] victimEffects = {};

    @Config.Name("Weapon-Specific Settings")
    @Config.Comment("Weapon-specific overrides for all the settings above")
    public String[] weaponSpecific = {};
}
