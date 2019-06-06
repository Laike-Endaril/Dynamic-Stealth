package com.fantasticsource.dynamicstealth.config.server.interactions;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import com.fantasticsource.dynamicstealth.server.event.attacks.AttackDefaults;
import net.minecraftforge.common.config.Config;

public class RangedStealthAttackConfig
{
    @Config.Name("Armor Penetration")
    @Config.LangKey(DynamicStealth.MODID + ".config.stealthAtkArmorPenetration")
    @Config.Comment({"Whether stealth attacks bypass armor or not"})
    public boolean armorPenetration = false;

    @Config.Name("Damage Multiplier")
    @Config.LangKey(DynamicStealth.MODID + ".config.stealthAtkDamageMult")
    @Config.Comment("Damage is multiplied by this when attacking from stealth")
    @Config.RangeDouble(min = 1)
    public double damageMultiplier = 1;

    @Config.Name("Attacker Effects")
    @Config.LangKey(DynamicStealth.MODID + ".config.stealthAtkAEffects")
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
    public String[] attackerEffects = {};

    @Config.Name("Victim Effects")
    @Config.LangKey(DynamicStealth.MODID + ".config.stealthAtkVEffects")
    @Config.Comment(
            {
                    "Potion effects that are applied to the victim when a stealth attack happens",
                    "",
                    "This applies blindness for 100 ticks (5 seconds):",
                    "blindness.100"
            })
    public String[] victimEffects = {};
}
