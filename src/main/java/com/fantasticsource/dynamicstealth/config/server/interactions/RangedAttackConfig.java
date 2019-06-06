package com.fantasticsource.dynamicstealth.config.server.interactions;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import net.minecraftforge.common.config.Config;

public class RangedAttackConfig
{
    @Config.Name("Remove Invisibility On Hit")
    @Config.LangKey(DynamicStealth.MODID + ".config.removeInvis")
    @Config.Comment(
            {
                    "If set to true, when one living entity hits another living entity, they both lose invisibility",
                    "",
                    "This happens before any new effects are applied"
            })
    public boolean removeInvisibilityOnHit = true;

    @Config.Name("Remove Blindness On Hit")
    @Config.LangKey(DynamicStealth.MODID + ".config.removeBlind")
    @Config.Comment(
            {
                    "If set to true, when one living entity hits another living entity, they both lose blindness",
                    "",
                    "This happens before any new effects are applied"
            })
    public boolean removeBlindnessOnHit = true;

    @Config.Name("Armor Penetration")
    @Config.LangKey(DynamicStealth.MODID + ".config.attackArmorPenetration")
    @Config.Comment({"Whether stealth attacks bypass armor or not"})
    public boolean armorPenetration = false;

    @Config.Name("Damage Multiplier")
    @Config.LangKey(DynamicStealth.MODID + ".config.attackDamageMult")
    @Config.Comment("Damage is multiplied by this when attacking from stealth")
    @Config.RangeDouble(min = 1)
    public double damageMultiplier = 1;

    @Config.Name("Attacker Effects")
    @Config.LangKey(DynamicStealth.MODID + ".config.attackAEffects")
    @Config.Comment(
            {
                    "Potion effects that are applied to the attacker when an attack happens",
                    "",
                    "This applies strength 2 for 200 ticks (10 seconds):",
                    "strength.200.2",
                    "",
                    "This applies soul sight for 100 ticks (5 seconds):",
                    "dynamicstealth:soulsight.100"
            })
    public String[] attackerEffects = {};

    @Config.Name("Victim Effects")
    @Config.LangKey(DynamicStealth.MODID + ".config.attackVEffects")
    @Config.Comment(
            {
                    "Potion effects that are applied to the victim when an attack happens",
                    "",
                    "This applies blindness for 100 ticks (5 seconds):",
                    "blindness.100"
            })
    public String[] victimEffects = {};
}
