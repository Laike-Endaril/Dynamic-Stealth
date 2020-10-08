package com.fantasticsource.dynamicstealth.server.event.attacks;

import com.fantasticsource.dynamicstealth.config.server.interactions.*;
import com.fantasticsource.mctools.potions.FantasticPotionEffect;
import com.fantasticsource.mctools.potions.Potions;
import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class AttackData
{
    protected static final Method ENTITY_LIVING_BASE_CAN_BLOCK_DAMAGE_SOURCE_METHOD = ReflectionTool.getMethod(EntityLivingBase.class, "func_184583_d", "canBlockDamageSource");


    public static ArrayList<FantasticPotionEffect> normalAttackerEffects;
    public static ArrayList<FantasticPotionEffect> normalVictimEffects;
    public static ArrayList<WeaponEntry> normalWeaponSpecific;

    public static ArrayList<FantasticPotionEffect> rangedAttackerEffects;
    public static ArrayList<FantasticPotionEffect> rangedVictimEffects;

    public static ArrayList<FantasticPotionEffect> stealthAttackerEffects;
    public static ArrayList<FantasticPotionEffect> stealthVictimEffects;
    public static ArrayList<WeaponEntry> stealthWeaponSpecific;

    public static ArrayList<FantasticPotionEffect> rangedStealthAttackerEffects;
    public static ArrayList<FantasticPotionEffect> rangedStealthVictimEffects;

    public static ArrayList<FantasticPotionEffect> normalBlockedAttackerEffects;
    public static ArrayList<FantasticPotionEffect> normalBlockedVictimEffects;
    public static ArrayList<WeaponEntry> normalBlockedWeaponSpecific;

    public static ArrayList<FantasticPotionEffect> rangedBlockedAttackerEffects;
    public static ArrayList<FantasticPotionEffect> rangedBlockedVictimEffects;

    public static ArrayList<FantasticPotionEffect> stealthBlockedAttackerEffects;
    public static ArrayList<FantasticPotionEffect> stealthBlockedVictimEffects;
    public static ArrayList<WeaponEntry> stealthBlockedWeaponSpecific;

    public static ArrayList<FantasticPotionEffect> rangedStealthBlockedAttackerEffects;
    public static ArrayList<FantasticPotionEffect> rangedStealthBlockedVictimEffects;

    public static ArrayList<FantasticPotionEffect> assassinationAttackerEffects;
    public static ArrayList<WeaponEntry> assassinationWeaponSpecific;

    public static ArrayList<FantasticPotionEffect> rangedAssassinationAttackerEffects;


    public static void update()
    {
        NormalAttackConfig normalConfig = serverSettings.interactions.attack;
        normalAttackerEffects = Potions.parsePotions(normalConfig.attackerEffects);
        normalVictimEffects = Potions.parsePotions(normalConfig.victimEffects);

        normalWeaponSpecific = new ArrayList<>();
        for (String string : normalConfig.weaponSpecific)
        {
            WeaponEntry entry = WeaponEntry.getInstance(string, WeaponEntry.TYPE_NORMAL, false);
            if (entry != null) normalWeaponSpecific.add(entry);
        }

        NormalAttackBlockedConfig normalBlockedConfig = serverSettings.interactions.attackBlocked;
        normalBlockedAttackerEffects = Potions.parsePotions(normalBlockedConfig.attackerEffects);
        normalBlockedVictimEffects = Potions.parsePotions(normalBlockedConfig.victimEffects);

        normalBlockedWeaponSpecific = new ArrayList<>();
        for (String string : normalBlockedConfig.weaponSpecific)
        {
            WeaponEntry entry = WeaponEntry.getInstance(string, WeaponEntry.TYPE_NORMAL, true);
            if (entry != null) normalBlockedWeaponSpecific.add(entry);
        }


        RangedAttackConfig rangedConfig = serverSettings.interactions.rangedAttack;
        rangedAttackerEffects = Potions.parsePotions(rangedConfig.attackerEffects);
        rangedVictimEffects = Potions.parsePotions(rangedConfig.victimEffects);

        rangedConfig = serverSettings.interactions.rangedAttackBlocked;
        rangedBlockedAttackerEffects = Potions.parsePotions(rangedConfig.attackerEffects);
        rangedBlockedVictimEffects = Potions.parsePotions(rangedConfig.victimEffects);


        StealthAttackConfig stealthConfig = serverSettings.interactions.stealthAttack;
        stealthAttackerEffects = Potions.parsePotions(stealthConfig.attackerEffects);
        stealthVictimEffects = Potions.parsePotions(stealthConfig.victimEffects);

        stealthWeaponSpecific = new ArrayList<>();
        for (String string : stealthConfig.weaponSpecific)
        {
            WeaponEntry entry = WeaponEntry.getInstance(string, WeaponEntry.TYPE_STEALTH, false);
            if (entry != null) stealthWeaponSpecific.add(entry);
        }

        StealthAttackBlockedConfig stealthBlockedConfig = serverSettings.interactions.stealthAttackBlocked;
        stealthBlockedAttackerEffects = Potions.parsePotions(stealthBlockedConfig.attackerEffects);
        stealthBlockedVictimEffects = Potions.parsePotions(stealthBlockedConfig.victimEffects);

        stealthBlockedWeaponSpecific = new ArrayList<>();
        for (String string : stealthBlockedConfig.weaponSpecific)
        {
            WeaponEntry entry = WeaponEntry.getInstance(string, WeaponEntry.TYPE_STEALTH, true);
            if (entry != null) stealthBlockedWeaponSpecific.add(entry);
        }


        RangedStealthAttackConfig rangedStealthConfig = serverSettings.interactions.rangedStealthAttack;
        rangedStealthAttackerEffects = Potions.parsePotions(rangedStealthConfig.attackerEffects);
        rangedStealthVictimEffects = Potions.parsePotions(rangedStealthConfig.victimEffects);

        rangedStealthConfig = serverSettings.interactions.rangedStealthAttackBlocked;
        rangedStealthBlockedAttackerEffects = Potions.parsePotions(rangedStealthConfig.attackerEffects);
        rangedStealthBlockedVictimEffects = Potions.parsePotions(rangedStealthConfig.victimEffects);


        AssassinationConfig assassinationConfig = serverSettings.interactions.assassination;
        assassinationAttackerEffects = Potions.parsePotions(assassinationConfig.attackerEffects);

        assassinationWeaponSpecific = new ArrayList<>();
        for (String string : assassinationConfig.weaponSpecific)
        {
            WeaponEntry entry = WeaponEntry.getInstance(string, WeaponEntry.TYPE_ASSASSINATION, false);
            if (entry != null) assassinationWeaponSpecific.add(entry);
        }

        RangedAssassinationConfig rangedAssassinationConfig = serverSettings.interactions.rangedAssassination;
        rangedAssassinationAttackerEffects = Potions.parsePotions(rangedAssassinationConfig.attackerEffects);
    }

    public static boolean isBlocked(EntityLivingBase victim, DamageSource damageSource)
    {
        return (boolean) ReflectionTool.invoke(ENTITY_LIVING_BASE_CAN_BLOCK_DAMAGE_SOURCE_METHOD, victim, damageSource);
    }
}
