package com.fantasticsource.dynamicstealth.server.event;

import com.fantasticsource.mctools.potions.Potions;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class EventData
{
    public static ArrayList<PotionEffect> rallyPotions, calmDownPotions, desperationPotions, cantReachPotions;

    public static void update()
    {
        rallyPotions = Potions.parsePotions(serverSettings.interactions.rally.potionEffects);
        calmDownPotions = Potions.parsePotions(serverSettings.interactions.calmDown.potionEffects);
        desperationPotions = Potions.parsePotions(serverSettings.interactions.desperation.potionEffects);
        cantReachPotions = Potions.parsePotions(serverSettings.interactions.cantReach.potionEffects);
    }
}
