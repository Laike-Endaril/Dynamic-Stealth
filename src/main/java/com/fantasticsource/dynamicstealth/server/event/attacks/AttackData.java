package com.fantasticsource.dynamicstealth.server.event.attacks;

import com.fantasticsource.dynamicstealth.config.server.interactions.*;
import com.fantasticsource.mctools.potions.Potions;
import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;
import static com.fantasticsource.dynamicstealth.server.event.attacks.WeaponEntry.*;

public class AttackData
{
    protected static final Method ENTITY_LIVING_BASE_CAN_BLOCK_DAMAGE_SOURCE_METHOD = ReflectionTool.getMethod(EntityLivingBase.class, "func_184583_d", "canBlockDamageSource");


    public static WeaponEntry normalDefault = new WeaponEntry(null), rangedDefault = new WeaponEntry(null), stealthDefault = new WeaponEntry(null), rangedStealthDefault = new WeaponEntry(null), normalBlockedDefault = new WeaponEntry(null), rangedBlockedDefault = new WeaponEntry(null), stealthBlockedDefault = new WeaponEntry(null), rangedStealthBlockedDefault = new WeaponEntry(null), assassinationDefault = new WeaponEntry(null), rangedAssassinationDefault = new WeaponEntry(null);
    public static ArrayList<WeaponEntry> normalWeaponSpecific, stealthWeaponSpecific, normalBlockedWeaponSpecific, stealthBlockedWeaponSpecific, assassinationWeaponSpecific;


    public static boolean isBlocked(EntityLivingBase victim, DamageSource damageSource)
    {
        return (boolean) ReflectionTool.invoke(ENTITY_LIVING_BASE_CAN_BLOCK_DAMAGE_SOURCE_METHOD, victim, damageSource);
    }


    public static void update()
    {
        NormalAttackConfig normalConfig = serverSettings.interactions.attack;
        normalDefault = new WeaponEntry(null);
        normalDefault.armorPenetration = normalConfig.armorPenetration;
        normalDefault.damageMultiplier = normalConfig.damageMultiplier;
        normalDefault.attackerEffects = Potions.parsePotions(normalConfig.attackerEffects);
        normalDefault.victimEffects = Potions.parsePotions(normalConfig.victimEffects);

        normalWeaponSpecific = new ArrayList<>();
        for (String string : normalConfig.weaponSpecific)
        {
            WeaponEntry entry = WeaponEntry.generateWeaponSpecific(string, TYPE_NORMAL, false);
            if (entry != null) normalWeaponSpecific.add(entry);
        }

        NormalAttackBlockedConfig normalBlockedConfig = serverSettings.interactions.attackBlocked;
        normalBlockedDefault = new WeaponEntry(null);
        normalBlockedDefault.armorPenetration = normalBlockedConfig.armorPenetration;
        normalBlockedDefault.damageMultiplier = normalBlockedConfig.damageMultiplier;
        normalBlockedDefault.attackerEffects = Potions.parsePotions(normalBlockedConfig.attackerEffects);
        normalBlockedDefault.victimEffects = Potions.parsePotions(normalBlockedConfig.victimEffects);

        normalBlockedWeaponSpecific = new ArrayList<>();
        for (String string : normalBlockedConfig.weaponSpecific)
        {
            WeaponEntry entry = WeaponEntry.generateWeaponSpecific(string, TYPE_NORMAL, true);
            if (entry != null) normalBlockedWeaponSpecific.add(entry);
        }


        RangedAttackConfig rangedConfig = serverSettings.interactions.rangedAttack;
        rangedDefault.armorPenetration = rangedConfig.armorPenetration;
        rangedDefault.damageMultiplier = rangedConfig.damageMultiplier;
        rangedDefault.attackerEffects = Potions.parsePotions(rangedConfig.attackerEffects);
        rangedDefault.victimEffects = Potions.parsePotions(rangedConfig.victimEffects);

        rangedConfig = serverSettings.interactions.rangedAttackBlocked;
        rangedBlockedDefault.armorPenetration = rangedConfig.armorPenetration;
        rangedBlockedDefault.damageMultiplier = rangedConfig.damageMultiplier;
        rangedBlockedDefault.attackerEffects = Potions.parsePotions(rangedConfig.attackerEffects);
        rangedBlockedDefault.victimEffects = Potions.parsePotions(rangedConfig.victimEffects);


        StealthAttackConfig stealthConfig = serverSettings.interactions.stealthAttack;
        stealthDefault.armorPenetration = stealthConfig.armorPenetration;
        stealthDefault.damageMultiplier = stealthConfig.damageMultiplier;
        stealthDefault.attackerEffects = Potions.parsePotions(stealthConfig.attackerEffects);
        stealthDefault.victimEffects = Potions.parsePotions(stealthConfig.victimEffects);

        stealthWeaponSpecific = new ArrayList<>();
        for (String string : stealthConfig.weaponSpecific)
        {
            WeaponEntry entry = WeaponEntry.generateWeaponSpecific(string, TYPE_STEALTH, false);
            if (entry != null) stealthWeaponSpecific.add(entry);
        }

        StealthAttackBlockedConfig stealthBlockedConfig = serverSettings.interactions.stealthAttackBlocked;
        stealthBlockedDefault.armorPenetration = stealthBlockedConfig.armorPenetration;
        stealthBlockedDefault.damageMultiplier = stealthBlockedConfig.damageMultiplier;
        stealthBlockedDefault.attackerEffects = Potions.parsePotions(stealthBlockedConfig.attackerEffects);
        stealthBlockedDefault.victimEffects = Potions.parsePotions(stealthBlockedConfig.victimEffects);

        stealthBlockedWeaponSpecific = new ArrayList<>();
        for (String string : stealthBlockedConfig.weaponSpecific)
        {
            WeaponEntry entry = WeaponEntry.generateWeaponSpecific(string, TYPE_STEALTH, true);
            if (entry != null) stealthBlockedWeaponSpecific.add(entry);
        }


        RangedStealthAttackConfig rangedStealthConfig = serverSettings.interactions.rangedStealthAttack;
        rangedStealthDefault.armorPenetration = rangedStealthConfig.armorPenetration;
        rangedStealthDefault.damageMultiplier = rangedStealthConfig.damageMultiplier;
        rangedStealthDefault.attackerEffects = Potions.parsePotions(rangedStealthConfig.attackerEffects);
        rangedStealthDefault.victimEffects = Potions.parsePotions(rangedStealthConfig.victimEffects);

        rangedStealthConfig = serverSettings.interactions.rangedStealthAttackBlocked;
        rangedStealthBlockedDefault.armorPenetration = rangedStealthConfig.armorPenetration;
        rangedStealthBlockedDefault.damageMultiplier = rangedStealthConfig.damageMultiplier;
        rangedStealthBlockedDefault.attackerEffects = Potions.parsePotions(rangedStealthConfig.attackerEffects);
        rangedStealthBlockedDefault.victimEffects = Potions.parsePotions(rangedStealthConfig.victimEffects);


        AssassinationConfig assassinationConfig = serverSettings.interactions.assassination;
        assassinationDefault.attackerEffects = Potions.parsePotions(assassinationConfig.attackerEffects);

        assassinationWeaponSpecific = new ArrayList<>();
        for (String string : assassinationConfig.weaponSpecific)
        {
            WeaponEntry entry = WeaponEntry.generateWeaponSpecific(string, TYPE_ASSASSINATION, false);
            if (entry != null) assassinationWeaponSpecific.add(entry);
        }

        RangedAssassinationConfig rangedAssassinationConfig = serverSettings.interactions.rangedAssassination;
        rangedAssassinationDefault.attackerEffects = Potions.parsePotions(rangedAssassinationConfig.attackerEffects);
    }


    protected static WeaponEntry getDefault(int type, boolean isMelee, boolean isBlocked)
    {
        //Defaults
        if (type == TYPE_NORMAL)
        {
            if (isMelee)
            {
                if (isBlocked) return normalBlockedDefault;
                return normalDefault;
            }

            if (isBlocked) return rangedBlockedDefault;
            return rangedDefault;
        }

        if (type == TYPE_STEALTH)
        {
            if (isMelee)
            {
                if (isBlocked) return stealthBlockedDefault;
                return stealthDefault;
            }

            if (isBlocked) return rangedStealthBlockedDefault;
            return rangedStealthDefault;
        }

        if (type == TYPE_ASSASSINATION)
        {
            if (isMelee) return assassinationDefault;
            return rangedAssassinationDefault;
        }

        return null;
    }


    //Needs to match the one in ClientData
    public static WeaponEntry getWeaponEntry(ItemStack itemStack, int type, boolean isBlocked)
    {
        if (itemStack == null) return getDefault(type, false, isBlocked);

        ArrayList<ArrayList<WeaponEntry>> priorityOrderedLists = new ArrayList<>();
        if (type == TYPE_ASSASSINATION) priorityOrderedLists.add(assassinationWeaponSpecific);
        else
        {
            if (type == TYPE_STEALTH)
            {
                if (isBlocked) priorityOrderedLists.add(stealthBlockedWeaponSpecific);
                priorityOrderedLists.add(stealthWeaponSpecific);
            }

            if (isBlocked) priorityOrderedLists.add(normalBlockedWeaponSpecific);
            priorityOrderedLists.add(normalWeaponSpecific);
        }

        for (ArrayList<WeaponEntry> list : priorityOrderedLists)
        {
            for (WeaponEntry weaponEntry : list)
            {
                if (weaponEntry.filter.matches(itemStack)) return weaponEntry;
            }
        }

        return getDefault(type, true, isBlocked);
    }
}
