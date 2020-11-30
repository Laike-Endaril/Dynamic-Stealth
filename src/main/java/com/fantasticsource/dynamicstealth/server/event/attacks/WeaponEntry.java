package com.fantasticsource.dynamicstealth.server.event.attacks;

import com.fantasticsource.mctools.items.ItemFilter;
import com.fantasticsource.mctools.potions.FantasticPotionEffect;
import com.fantasticsource.mctools.potions.Potions;

import java.util.ArrayList;
import java.util.regex.Pattern;

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


    /**
     * This method should only ever be called for melee attacks; ranged attacks are unreliable when it comes to detecting what item they came from (eg. if the attacker switches items before the attack hits)
     */
    public static WeaponEntry generateWeaponSpecific(String configEntry, int type, boolean isBlocked)
    {
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

        WeaponEntry result = new WeaponEntry(ItemFilter.getInstance(nameAndNBT, suppressMissingItemError));
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


        return result;
    }
}
