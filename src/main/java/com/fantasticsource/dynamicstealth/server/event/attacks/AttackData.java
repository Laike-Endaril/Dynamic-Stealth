package com.fantasticsource.dynamicstealth.server.event.attacks;

import com.fantasticsource.dynamicstealth.config.server.interactions.AssassinationConfig;
import com.fantasticsource.dynamicstealth.config.server.interactions.NormalAttackConfig;
import com.fantasticsource.dynamicstealth.config.server.interactions.StealthAttackConfig;
import com.fantasticsource.mctools.potions.Potions;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class AttackData
{
    public static ArrayList<PotionEffect> normalAttackerEffects;
    public static ArrayList<PotionEffect> normalVictimEffects;
    public static LinkedHashMap<ItemStack, WeaponEntry> normalWeaponSpecific = new LinkedHashMap<>();

    public static ArrayList<PotionEffect> stealthAttackerEffects;
    public static ArrayList<PotionEffect> stealthVictimEffects;
    public static LinkedHashMap<ItemStack, WeaponEntry> stealthWeaponSpecific = new LinkedHashMap<>();

    public static ArrayList<PotionEffect> assassinationAttackerEffects;
    public static LinkedHashMap<ItemStack, WeaponEntry> assassinationWeaponSpecific = new LinkedHashMap<>();

    private static NormalAttackConfig normalConfig = serverSettings.interactions.attack;
    private static StealthAttackConfig stealthConfig = serverSettings.interactions.stealthAttack;
    private static AssassinationConfig assassinationConfig = serverSettings.interactions.assassination;


    static
    {
        normalAttackerEffects = Potions.parsePotions(normalConfig.attackerEffects);
        normalVictimEffects = Potions.parsePotions(normalConfig.victimEffects);

        for (String string : normalConfig.weaponSpecific)
        {
            WeaponEntry entry = new WeaponEntry(string, WeaponEntry.TYPE_NORMAL);
            ItemStack itemStack = entry.itemStack;
            if (itemStack != null) normalWeaponSpecific.put(itemStack, entry);
        }


        stealthAttackerEffects = Potions.parsePotions(stealthConfig.attackerEffects);
        stealthVictimEffects = Potions.parsePotions(stealthConfig.victimEffects);

        for (String string : stealthConfig.weaponSpecific)
        {
            WeaponEntry entry = new WeaponEntry(string, WeaponEntry.TYPE_STEALTH);
            ItemStack itemStack = entry.itemStack;
            if (itemStack != null) stealthWeaponSpecific.put(itemStack, entry);
        }


        assassinationAttackerEffects = Potions.parsePotions(assassinationConfig.attackerEffects);

        for (String string : assassinationConfig.weaponSpecific)
        {
            WeaponEntry entry = new WeaponEntry(string, WeaponEntry.TYPE_ASSASSINATION);
            ItemStack itemStack = entry.itemStack;
            if (itemStack != null) assassinationWeaponSpecific.put(itemStack, entry);
        }
    }


    public static void init()
    {
        //Indirectly initializes the class
    }
}
