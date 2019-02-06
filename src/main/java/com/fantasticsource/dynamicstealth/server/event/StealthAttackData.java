package com.fantasticsource.dynamicstealth.server.event;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class StealthAttackData
{
    public static ArrayList<PotionEffect> attackerEffects = new ArrayList<>();
    public static ArrayList<PotionEffect> victimEffects = new ArrayList<>();

    static
    {
        String[] tokens;

        Potion potion;
        int duration, amplifier;

        for (String string : serverSettings.interactions.stealthAttack.attackerEffects)
        {
            tokens = string.split(",");
            if (tokens.length > 3)
            {
                System.err.println("Too many arguments for potion effect; should be max of 3 (see config tooltip for example)");
                continue;
            }

            duration = 0;
            amplifier = 0;

            if (tokens.length > 0)
            {
                potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(tokens[0].trim()));

                if (potion == null)
                {
                    System.err.println("ResourceLocation for entity \"" + string + "\" not found!");
                    continue;
                }

                if (tokens.length > 1) duration = Integer.parseInt(tokens[1].trim());
                if (tokens.length > 2) amplifier = Integer.parseInt(tokens[2].trim());
                if (amplifier > 0) amplifier--; //Makes it so ppl can just type 2 for stength 2 instead of typing 1

                attackerEffects.add(new PotionEffect(potion, duration, amplifier, false, true));
            }
        }

        for (String string : serverSettings.interactions.stealthAttack.victimEffects)
        {
            tokens = string.split(",");
            if (tokens.length > 3)
            {
                System.err.println("Too many arguments for potion effect; should be max of 3 (see config tooltip for example)");
                continue;
            }

            duration = 0;
            amplifier = 0;

            if (tokens.length > 0)
            {
                potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(tokens[0].trim()));

                if (potion == null)
                {
                    System.err.println("ResourceLocation for entity \"" + string + "\" not found!");
                    continue;
                }

                if (tokens.length > 1) duration = Integer.parseInt(tokens[1].trim());
                if (tokens.length > 2) amplifier = Integer.parseInt(tokens[2].trim());
                if (amplifier > 0) amplifier--; //Makes it so ppl can just type 2 for stength 2 instead of typing 1

                victimEffects.add(new PotionEffect(potion, duration, amplifier, false, true));
            }
        }
    }
}
