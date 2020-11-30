package com.fantasticsource.dynamicstealth.server.event.attacks;

import com.fantasticsource.mctools.items.ItemFilter;
import com.fantasticsource.mctools.potions.FantasticPotionEffect;
import com.fantasticsource.mctools.potions.Potions;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class WeaponEntry
{
    public static final int TYPE_NORMAL = 0, TYPE_STEALTH = 1, TYPE_ASSASSINATION = 2;

    public ItemFilter filter;

    public boolean consumeItem = false;
    public boolean armorPenetration = false;
    public double damageMultiplier = 1;
    public ArrayList<FantasticPotionEffect> attackerEffects = new ArrayList<>();
    public ArrayList<FantasticPotionEffect> victimEffects = new ArrayList<>();

    public WeaponEntry(ItemFilter filter)
    {
        this.filter = filter;
    }

    private WeaponEntry(int type, boolean isMelee, boolean isBlocked)
    {
        //Defaults
        if (type == TYPE_NORMAL)
        {
            if (isMelee)
            {
                if (isBlocked)
                {
                    armorPenetration = serverSettings.interactions.attackBlocked.armorPenetration;
                    damageMultiplier = serverSettings.interactions.attackBlocked.damageMultiplier;
                    attackerEffects = AttackData.normalBlockedAttackerEffects;
                    victimEffects = AttackData.normalBlockedVictimEffects;
                }
                else
                {
                    armorPenetration = serverSettings.interactions.attack.armorPenetration;
                    damageMultiplier = serverSettings.interactions.attack.damageMultiplier;
                    attackerEffects = AttackData.normalAttackerEffects;
                    victimEffects = AttackData.normalVictimEffects;
                }
            }
            else
            {
                if (isBlocked)
                {
                    armorPenetration = serverSettings.interactions.rangedAttackBlocked.armorPenetration;
                    damageMultiplier = serverSettings.interactions.rangedAttackBlocked.damageMultiplier;
                    attackerEffects = AttackData.rangedBlockedAttackerEffects;
                    victimEffects = AttackData.rangedBlockedVictimEffects;
                }
                else
                {
                    armorPenetration = serverSettings.interactions.rangedAttack.armorPenetration;
                    damageMultiplier = serverSettings.interactions.rangedAttack.damageMultiplier;
                    attackerEffects = AttackData.rangedAttackerEffects;
                    victimEffects = AttackData.rangedVictimEffects;
                }
            }
        }
        else if (type == TYPE_STEALTH)
        {
            if (isMelee)
            {
                if (isBlocked)
                {
                    armorPenetration = serverSettings.interactions.stealthAttackBlocked.armorPenetration;
                    damageMultiplier = serverSettings.interactions.stealthAttackBlocked.damageMultiplier;
                    attackerEffects = AttackData.stealthBlockedAttackerEffects;
                    victimEffects = AttackData.stealthBlockedVictimEffects;
                }
                else
                {
                    armorPenetration = serverSettings.interactions.stealthAttack.armorPenetration;
                    damageMultiplier = serverSettings.interactions.stealthAttack.damageMultiplier;
                    attackerEffects = AttackData.stealthAttackerEffects;
                    victimEffects = AttackData.stealthVictimEffects;
                }
            }
            else
            {
                if (isBlocked)
                {
                    armorPenetration = serverSettings.interactions.rangedStealthAttackBlocked.armorPenetration;
                    damageMultiplier = serverSettings.interactions.rangedStealthAttackBlocked.damageMultiplier;
                    attackerEffects = AttackData.rangedStealthBlockedAttackerEffects;
                    victimEffects = AttackData.rangedStealthBlockedVictimEffects;
                }
                else
                {
                    armorPenetration = serverSettings.interactions.rangedStealthAttack.armorPenetration;
                    damageMultiplier = serverSettings.interactions.rangedStealthAttack.damageMultiplier;
                    attackerEffects = AttackData.rangedStealthAttackerEffects;
                    victimEffects = AttackData.rangedStealthVictimEffects;
                }
            }
        }
        else if (type == TYPE_ASSASSINATION)
        {
            if (isMelee) attackerEffects = AttackData.assassinationAttackerEffects;
            else attackerEffects = AttackData.rangedAssassinationAttackerEffects;
        }
    }

    /**
     * This method should only ever be called for melee attacks; ranged attacks are unreliable when it comes to detecting what item they came from (eg. if the attacker switches items before the attack hits)
     */
    public static WeaponEntry getInstance(String configEntry, int type, boolean isBlocked)
    {
        //Defaults
        WeaponEntry result = new WeaponEntry(type, true, isBlocked);


        String[] tokens = configEntry.split(Pattern.quote(","));

        if (tokens.length < 2)
        {
            System.err.println("Not enough arguments for weapon entry: " + configEntry);
            return null;
        }
        if (((type == TYPE_NORMAL || type == TYPE_STEALTH) && tokens.length > 6) || (type == TYPE_ASSASSINATION && tokens.length > 2))
        {
            System.err.println("Too many arguments for weapon entry: " + configEntry);
            return null;
        }


        //Item, meta, and NBT
        String nameAndNBT = tokens[0].trim();
        boolean suppressMissingItemError = false;
        ArrayList<String> list = null;
        if (type == TYPE_NORMAL) list = isBlocked ? AttackDefaults.normalAttackBlockedDefaults : AttackDefaults.normalAttackDefaults;
        else if (type == TYPE_STEALTH) list = isBlocked ? AttackDefaults.stealthAttackBlockedDefaults : AttackDefaults.stealthAttackDefaults;
        else if (type == TYPE_ASSASSINATION) list = AttackDefaults.assassinationDefaults;
        for (String entry : list)
        {
            if (entry.split(",")[0].trim().equals(nameAndNBT))
            {
                suppressMissingItemError = true;
                break;
            }
        }

        result.filter = ItemFilter.getInstance(nameAndNBT, suppressMissingItemError);
        if (result.filter == null) return null;


        //Easy stuff...
        if (type == TYPE_NORMAL || type == TYPE_STEALTH)
        {
            result.armorPenetration = Boolean.parseBoolean(tokens[1]);
            if (tokens.length > 2) result.damageMultiplier = Double.parseDouble(tokens[2]);
        }


        //Potion effects
        if (type == TYPE_ASSASSINATION) result.attackerEffects = Potions.parsePotions(tokens[1]);
        else if (tokens.length > 3)
        {
            result.attackerEffects = Potions.parsePotions(tokens[3]);
            if (tokens.length > 4)
            {
                result.victimEffects = Potions.parsePotions(tokens[4]);
            }
        }


        //More easy stuff...
        if (tokens.length > 5) result.consumeItem = Boolean.parseBoolean(tokens[5].trim());


        if (result.attackerEffects == null) result.attackerEffects = new ArrayList<>();
        if (result.victimEffects == null) result.victimEffects = new ArrayList<>();

        return result;
    }

    public static WeaponEntry get(ItemStack itemStack, int type, boolean isBlocked)
    {
        if (itemStack == null) return new WeaponEntry(type, false, isBlocked);

        ArrayList<WeaponEntry> list = null;
        if (type == TYPE_NORMAL) list = isBlocked ? AttackData.normalBlockedWeaponSpecific : AttackData.normalWeaponSpecific;
        else if (type == TYPE_STEALTH) list = isBlocked ? AttackData.stealthBlockedWeaponSpecific : AttackData.stealthWeaponSpecific;
        else if (type == TYPE_ASSASSINATION) list = AttackData.assassinationWeaponSpecific;

        for (WeaponEntry weaponEntry : list)
        {
            if (weaponEntry.filter.matches(itemStack)) return weaponEntry;
        }

        return new WeaponEntry(type, true, isBlocked);
    }
}
