package com.fantasticsource.dynamicstealth.server.event.attacks;

import com.fantasticsource.dynamicstealth.config.server.interactions.AssassinationConfig;
import com.fantasticsource.dynamicstealth.config.server.interactions.NormalAttackConfig;
import com.fantasticsource.dynamicstealth.config.server.interactions.StealthAttackConfig;
import com.fantasticsource.mctools.potions.Potions;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class AttackData
{
    public static ArrayList<PotionEffect> normalAttackerEffects;
    public static ArrayList<PotionEffect> normalVictimEffects;
    public static ArrayList<WeaponEntry> normalWeaponSpecific = new ArrayList<>();

    public static ArrayList<PotionEffect> stealthAttackerEffects;
    public static ArrayList<PotionEffect> stealthVictimEffects;
    public static ArrayList<WeaponEntry> stealthWeaponSpecific = new ArrayList<>();

    public static ArrayList<PotionEffect> assassinationAttackerEffects;
    public static ArrayList<WeaponEntry> assassinationWeaponSpecific = new ArrayList<>();

    private static NormalAttackConfig normalConfig = serverSettings.interactions.attack;
    private static StealthAttackConfig stealthConfig = serverSettings.interactions.stealthAttack;
    private static AssassinationConfig assassinationConfig = serverSettings.interactions.assassination;


    static
    {
        normalAttackerEffects = Potions.parsePotions(normalConfig.attackerEffects);
        normalVictimEffects = Potions.parsePotions(normalConfig.victimEffects);

        for (String string : normalConfig.weaponSpecific)
        {
            WeaponEntry entry = WeaponEntry.getInstance(string, WeaponEntry.TYPE_NORMAL);
            if (entry != null) normalWeaponSpecific.add(entry);
        }


        stealthAttackerEffects = Potions.parsePotions(stealthConfig.attackerEffects);
        stealthVictimEffects = Potions.parsePotions(stealthConfig.victimEffects);

        for (String string : stealthConfig.weaponSpecific)
        {
            WeaponEntry entry = WeaponEntry.getInstance(string, WeaponEntry.TYPE_STEALTH);
            if (entry != null) stealthWeaponSpecific.add(entry);
        }


        assassinationAttackerEffects = Potions.parsePotions(assassinationConfig.attackerEffects);

        for (String string : assassinationConfig.weaponSpecific)
        {
            WeaponEntry entry = WeaponEntry.getInstance(string, WeaponEntry.TYPE_ASSASSINATION);
            if (entry != null) assassinationWeaponSpecific.add(entry);
        }
    }


    public static void init()
    {
        //Indirectly initializes the class
    }
}
