package com.fantasticsource.dynamicstealth.server.event;

import com.fantasticsource.dynamicstealth.server.event.attacks.WeaponEntry;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class EventData
{
    public static ArrayList<PotionEffect> rallyPotions = new ArrayList<>(WeaponEntry.getPotions(serverSettings.interactions.rally.potionEffects));
    public static ArrayList<PotionEffect> calmDownPotions = new ArrayList<>(WeaponEntry.getPotions(serverSettings.interactions.calmDown.potionEffects));
    public static ArrayList<PotionEffect> desperationPotions = new ArrayList<>(WeaponEntry.getPotions(serverSettings.interactions.desperation.potionEffects));
    public static ArrayList<PotionEffect> cantReachPotions = new ArrayList<>(WeaponEntry.getPotions(serverSettings.interactions.cantReach.potionEffects));
}
