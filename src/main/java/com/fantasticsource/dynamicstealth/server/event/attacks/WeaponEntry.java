package com.fantasticsource.dynamicstealth.server.event.attacks;

import com.fantasticsource.mctools.items.ItemFilter;
import com.fantasticsource.mctools.potions.Potions;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class WeaponEntry
{
    public static final int TYPE_NORMAL = 0, TYPE_STEALTH = 1, TYPE_ASSASSINATION = 2;

    public boolean armorPenetration = false;
    public double damageMultiplier = 1;
    public ArrayList<PotionEffect> attackerEffects;
    public ArrayList<PotionEffect> victimEffects;
    public boolean consumeItem = false;

    public ItemFilter filter;

    private WeaponEntry(int type)
    {
        //Defaults
        if (type == TYPE_NORMAL)
        {
            armorPenetration = serverSettings.interactions.attack.armorPenetration;
            damageMultiplier = serverSettings.interactions.attack.damageMultiplier;
            attackerEffects = AttackData.normalAttackerEffects;
            victimEffects = AttackData.normalVictimEffects;
        }
        else if (type == TYPE_STEALTH)
        {
            armorPenetration = serverSettings.interactions.stealthAttack.armorPenetration;
            damageMultiplier = serverSettings.interactions.stealthAttack.damageMultiplier;
            attackerEffects = AttackData.stealthAttackerEffects;
            victimEffects = AttackData.stealthVictimEffects;
        }
        else if (type == TYPE_ASSASSINATION)
        {
            attackerEffects = AttackData.assassinationAttackerEffects;
        }
    }

    public static WeaponEntry getInstance(String configEntry, int type)
    {
        //Defaults
        WeaponEntry result = new WeaponEntry(type);


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
        if (type == TYPE_NORMAL) list = AttackDefaults.normalAttackDefaults;
        else if (type == TYPE_STEALTH) list = AttackDefaults.stealthAttackDefaults;
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

    public static WeaponEntry get(ItemStack itemStack, int type)
    {
        ArrayList<WeaponEntry> list = null;
        if (type == TYPE_NORMAL) list = AttackData.normalWeaponSpecific;
        else if (type == TYPE_STEALTH) list = AttackData.stealthWeaponSpecific;
        else if (type == TYPE_ASSASSINATION) list = AttackData.assassinationWeaponSpecific;

        for (WeaponEntry weaponEntry : list)
        {
            if (weaponEntry.filter.matches(itemStack)) return weaponEntry;
        }

        return new WeaponEntry(type);
    }
}
