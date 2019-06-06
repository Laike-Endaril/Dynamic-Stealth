package com.fantasticsource.dynamicstealth.server.event.attacks;

import com.fantasticsource.dynamicstealth.config.server.interactions.*;
import com.fantasticsource.mctools.potions.Potions;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class AttackData
{
    public static ArrayList<PotionEffect> normalAttackerEffects;
    public static ArrayList<PotionEffect> normalVictimEffects;
    public static ArrayList<WeaponEntry> normalWeaponSpecific;

    public static ArrayList<PotionEffect> rangedAttackerEffects;
    public static ArrayList<PotionEffect> rangedVictimEffects;

    public static ArrayList<PotionEffect> stealthAttackerEffects;
    public static ArrayList<PotionEffect> stealthVictimEffects;
    public static ArrayList<WeaponEntry> stealthWeaponSpecific;

    public static ArrayList<PotionEffect> rangedStealthAttackerEffects;
    public static ArrayList<PotionEffect> rangedStealthVictimEffects;

    public static ArrayList<PotionEffect> assassinationAttackerEffects;
    public static ArrayList<WeaponEntry> assassinationWeaponSpecific;

    public static ArrayList<PotionEffect> rangedAssassinationAttackerEffects;


    public static void update()
    {
        NormalAttackConfig normalConfig = serverSettings.interactions.attack;
        normalAttackerEffects = Potions.parsePotions(normalConfig.attackerEffects);
        normalVictimEffects = Potions.parsePotions(normalConfig.victimEffects);

        normalWeaponSpecific = new ArrayList<>();
        for (String string : normalConfig.weaponSpecific)
        {
            WeaponEntry entry = WeaponEntry.getInstance(string, WeaponEntry.TYPE_NORMAL);
            if (entry != null) normalWeaponSpecific.add(entry);
        }

        RangedAttackConfig rangedConfig = serverSettings.interactions.rangedAttack;
        rangedAttackerEffects = Potions.parsePotions(rangedConfig.attackerEffects);
        rangedVictimEffects = Potions.parsePotions(rangedConfig.victimEffects);


        StealthAttackConfig stealthConfig = serverSettings.interactions.stealthAttack;
        stealthAttackerEffects = Potions.parsePotions(stealthConfig.attackerEffects);
        stealthVictimEffects = Potions.parsePotions(stealthConfig.victimEffects);

        stealthWeaponSpecific = new ArrayList<>();
        for (String string : stealthConfig.weaponSpecific)
        {
            WeaponEntry entry = WeaponEntry.getInstance(string, WeaponEntry.TYPE_STEALTH);
            if (entry != null) stealthWeaponSpecific.add(entry);
        }

        RangedStealthAttackConfig rangedStealthConfig = serverSettings.interactions.rangedStealthAttack;
        rangedStealthAttackerEffects = Potions.parsePotions(rangedStealthConfig.attackerEffects);
        rangedStealthVictimEffects = Potions.parsePotions(rangedStealthConfig.victimEffects);


        AssassinationConfig assassinationConfig = serverSettings.interactions.assassination;
        assassinationAttackerEffects = Potions.parsePotions(assassinationConfig.attackerEffects);

        assassinationWeaponSpecific = new ArrayList<>();
        for (String string : assassinationConfig.weaponSpecific)
        {
            WeaponEntry entry = WeaponEntry.getInstance(string, WeaponEntry.TYPE_ASSASSINATION);
            if (entry != null) assassinationWeaponSpecific.add(entry);
        }

        RangedAssassinationConfig rangedAssassinationConfig = serverSettings.interactions.rangedAssassination;
        rangedAssassinationAttackerEffects = Potions.parsePotions(rangedAssassinationConfig.attackerEffects);
    }
}
