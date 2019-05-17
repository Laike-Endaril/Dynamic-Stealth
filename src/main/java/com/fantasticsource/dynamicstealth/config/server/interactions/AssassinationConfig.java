package com.fantasticsource.dynamicstealth.config.server.interactions;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import com.fantasticsource.dynamicstealth.server.event.attacks.AttackDefaults;
import net.minecraftforge.common.config.Config;

public class AssassinationConfig
{
    @Config.Name("Attacker Effects")
    @Config.LangKey(DynamicStealth.MODID + ".config.assassinationAEffects")
    @Config.Comment(
            {
                    "Potion effects that are applied to the attacker when an assassination happens",
                    "",
                    "This applies strength 2 for 200 ticks (10 seconds):",
                    "strength.200.2",
                    "",
                    "This applies soul sight for 100 ticks (5 seconds):",
                    "dynamicstealth:soulsight.100"
            })
    public String[] attackerEffects =
            {
                    "dynamicstealth:soulsight.140",
                    "invisibility.140",
                    "night_vision.140"
            };

    @Config.Name("Weapon-Specific Settings")
    @Config.LangKey(DynamicStealth.MODID + ".config.assassinationWeaponSpecific")
    @Config.Comment(
            {
                    "Weapon-specific overrides",
                    "Syntax is...",
                    "",
                    "domain:item:meta > nbtkey1 = nbtvalue1 & nbtkey2 = nbtvalue2, attackerpotion1 & attackerpotion2",
                    "",
                    "eg...",
                    "",
                    "backstab:diamond_dagger, invisibility.200",
                    "",
                    "tetra:duplex_tool_modular > duplex/sickle_left_material & duplex/butt_right_material, regeneration.60.3"
            })
    public String[] weaponSpecific = AttackDefaults.assassinationDefaults.toArray(new String[AttackDefaults.assassinationDefaults.size()]);
}
