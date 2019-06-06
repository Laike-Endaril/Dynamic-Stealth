package com.fantasticsource.dynamicstealth.config.server.interactions;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import com.fantasticsource.dynamicstealth.server.event.attacks.AttackDefaults;
import net.minecraftforge.common.config.Config;

public class RangedAssassinationConfig
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
    public String[] attackerEffects = {};
}
