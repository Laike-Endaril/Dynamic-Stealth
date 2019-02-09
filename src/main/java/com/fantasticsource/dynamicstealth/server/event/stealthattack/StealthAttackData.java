package com.fantasticsource.dynamicstealth.server.event.stealthattack;

import com.fantasticsource.dynamicstealth.config.server.interactions.StealthAttackConfig;
import com.fantasticsource.dynamicstealth.server.event.WeaponEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class StealthAttackData
{
    public static ArrayList<PotionEffect> attackerEffects;
    public static ArrayList<PotionEffect> victimEffects;
    public static LinkedHashMap<ItemStack, WeaponEntry> weaponSpecific = new LinkedHashMap<>();
    private static StealthAttackConfig config = serverSettings.interactions.stealthAttack;

    static
    {
        attackerEffects = WeaponEntry.getPotions(config.attackerEffects);
        victimEffects = WeaponEntry.getPotions(config.victimEffects);

        for (String string : config.weaponSpecific)
        {
            WeaponEntry entry = new WeaponEntry(string);
            ItemStack itemStack = entry.itemStack;
            if (itemStack != null) weaponSpecific.put(itemStack, entry);
        }
    }


    public static void init()
    {
        //Indirectly initializes the class
    }
}
