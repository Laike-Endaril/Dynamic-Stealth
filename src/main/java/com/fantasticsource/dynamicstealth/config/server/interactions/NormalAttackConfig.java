package com.fantasticsource.dynamicstealth.config.server.interactions;

import com.fantasticsource.dynamicstealth.server.event.attacks.AttackDefaults;
import net.minecraftforge.common.config.Config;

public class NormalAttackConfig
{
    @Config.Name("Remove Invisibility On Hit")
    @Config.Comment(
            {
                    "If set to true, when one living entity hits another living entity, they both lose invisibility",
                    "",
                    "This happens before any new effects are applied"
            })
    public boolean removeInvisibilityOnHit = true;

    @Config.Name("Remove Blindness On Hit")
    @Config.Comment(
            {
                    "If set to true, when one living entity hits another living entity, they both lose blindness",
                    "",
                    "This happens before any new effects are applied"
            })
    public boolean removeBlindnessOnHit = true;

    @Config.Name("Armor Penetration")
    @Config.Comment({"Whether stealth attacks bypass armor or not"})
    public boolean armorPenetration = false;

    @Config.Name("Damage Multiplier")
    @Config.Comment("Damage is multiplied by this when attacking from stealth")
    @Config.RangeDouble(min = 1)
    public double damageMultiplier = 1;

    @Config.Name("Attacker Effects")
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
    @Config.RequiresMcRestart
    public String[] attackerEffects = {};

    @Config.Name("Victim Effects")
    @Config.Comment(
            {
                    "Potion effects that are applied to the victim when an attack happens",
                    "",
                    "This applies blindness for 100 ticks (5 seconds):",
                    "blindness.100"
            })
    @Config.RequiresMcRestart
    public String[] victimEffects = {};

    @Config.Name("Weapon-Specific Settings")
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
    @Config.RequiresMcRestart
    public String[] weaponSpecific = AttackDefaults.normalAttackDefaults.toArray(new String[AttackDefaults.normalAttackDefaults.size()]);
}
