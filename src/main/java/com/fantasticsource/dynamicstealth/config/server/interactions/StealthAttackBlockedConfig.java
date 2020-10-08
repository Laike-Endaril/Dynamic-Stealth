package com.fantasticsource.dynamicstealth.config.server.interactions;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import com.fantasticsource.dynamicstealth.server.event.attacks.AttackDefaults;
import net.minecraftforge.common.config.Config;

public class StealthAttackBlockedConfig
{
    @Config.Name("Armor Penetration")
    @Config.LangKey(DynamicStealth.MODID + ".config.stealthAtkArmorPenetration")
    @Config.Comment({"Whether stealth attacks bypass armor and shields or not"})
    public boolean armorPenetration = false;

    @Config.Name("Damage Multiplier")
    @Config.LangKey(DynamicStealth.MODID + ".config.stealthAtkDamageMult")
    @Config.Comment("Damage is multiplied by this when attacking from stealth")
    @Config.RangeDouble(min = 1)
    public double damageMultiplier = 1.25;

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

    @Config.Name("Weapon-Specific Settings")
    @Config.LangKey(DynamicStealth.MODID + ".config.stealthAtkWeaponSpecific")
    @Config.Comment(
            {
                    "Weapon-specific overrides",
                    "Syntax is...",
                    "",
                    "domain:item:meta > nbtkey1 = nbtvalue1 & nbtkey2 = nbtvalue2, armorpenetration, multiplier, attackerpotion1 & attackerpotion2, vidtimpotion1 & victimpotion2, consumeitem",
                    "",
                    "eg...",
                    "",
                    "backstab:diamond_dagger, true, 3",
                    "",
                    "tetra:duplex_tool_modular > duplex/sickle_left_material & duplex/butt_right_material, true, 3, regeneration.60.3, wither.60.3",
                    "",
                    "dye, false, 0, , blindness.20, true"
            })
    public String[] weaponSpecific = AttackDefaults.stealthAttackBlockedDefaults.toArray(new String[0]);
}
