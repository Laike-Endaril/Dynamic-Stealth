package com.fantasticsource.dynamicstealth.server.event;

import com.fantasticsource.mctools.potions.FantasticPotionEffect;
import com.fantasticsource.mctools.potions.Potions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;

import static com.fantasticsource.dynamicstealth.config.DynamicStealthConfig.serverSettings;

public class EventData
{
    public static ArrayList<FantasticPotionEffect> cantReachPotionFilter, rallyPotions, calmDownPotions, desperationPotions, cantReachPotions;

    public static void update()
    {
        cantReachPotionFilter = Potions.parsePotions(serverSettings.ai.cantReach.potionFilter, true);
        if (cantReachPotionFilter == null) cantReachPotionFilter = new ArrayList<>();

        rallyPotions = Potions.parsePotions(serverSettings.interactions.rally.potionEffects);
        calmDownPotions = Potions.parsePotions(serverSettings.interactions.calmDown.potionEffects);
        desperationPotions = Potions.parsePotions(serverSettings.interactions.desperation.potionEffects);
        cantReachPotions = Potions.parsePotions(serverSettings.interactions.cantReach.potionEffects);
    }

    public static boolean checkCantReachPotionFilter(EntityLivingBase livingBase)
    {
        for (PotionEffect potionEffect : cantReachPotionFilter)
        {
            PotionEffect check = livingBase.getActivePotionEffect(potionEffect.getPotion());
            if (check != null)
            {
                int potAmp = potionEffect.getAmplifier();
                if (potAmp == Integer.MAX_VALUE || potAmp == check.getAmplifier()) return true;
            }
        }
        return false;
    }
}
