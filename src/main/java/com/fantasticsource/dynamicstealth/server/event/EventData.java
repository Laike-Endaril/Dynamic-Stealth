package com.fantasticsource.dynamicstealth.server.event;

import com.fantasticsource.mctools.potions.Potions;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class EventData
{
    public static ArrayList<PotionEffect> rallyPotions = Potions.parsePotions(serverSettings.interactions.rally.potionEffects);
    public static ArrayList<PotionEffect> calmDownPotions = Potions.parsePotions(serverSettings.interactions.calmDown.potionEffects);
    public static ArrayList<PotionEffect> desperationPotions = Potions.parsePotions(serverSettings.interactions.desperation.potionEffects);
    public static ArrayList<PotionEffect> cantReachPotions = Potions.parsePotions(serverSettings.interactions.cantReach.potionEffects);
}
