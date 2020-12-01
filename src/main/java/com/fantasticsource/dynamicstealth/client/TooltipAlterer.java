package com.fantasticsource.dynamicstealth.client;

import com.fantasticsource.dynamicstealth.common.ClientData;
import com.fantasticsource.dynamicstealth.server.event.attacks.WeaponEntry;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.potions.FantasticPotionEffect;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

import static com.fantasticsource.dynamicstealth.DynamicStealth.MODID;

public class TooltipAlterer
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void tooltips(ItemTooltipEvent event)
    {
        EntityPlayer player = event.getEntityPlayer();
        if (player == null) return;

        String attackDamageString = I18n.translateToLocalFormatted("attribute.name.generic.attackDamage");

        ItemStack stack = event.getItemStack();
        WeaponEntry
                normal = ClientData.getWeaponEntry(stack, WeaponEntry.TYPE_NORMAL, false),
                stealth = ClientData.getWeaponEntry(stack, WeaponEntry.TYPE_STEALTH, false),
                blockedNormal = ClientData.getWeaponEntry(stack, WeaponEntry.TYPE_NORMAL, true),
                blockedStealth = ClientData.getWeaponEntry(stack, WeaponEntry.TYPE_STEALTH, true),
                assassination = ClientData.getWeaponEntry(stack, WeaponEntry.TYPE_ASSASSINATION, false);

        List<String> tooltip = event.getToolTip();
        double vanillaDamage = MCTools.getAttribute(player, SharedMonsterAttributes.ATTACK_DAMAGE);
        for (int i = 0; i < tooltip.size(); i++)
        {
            String line = tooltip.get(i);
            if (line.contains(attackDamageString))
            {
                try
                {
                    vanillaDamage = Double.parseDouble(line.replace(attackDamageString, "").trim());
                    tooltip.remove(i);
                    break;
                }
                catch (NumberFormatException e)
                {
                }
            }
        }


        tooltip.add("");
        tooltip.add(I18n.translateToLocal(MODID + ".tooltip.normalAttacks"));
        if (normal.consumeItem) tooltip.add(I18n.translateToLocal(MODID + ".tooltip.consumeItem"));
        if (normal.armorPenetration) tooltip.add(I18n.translateToLocalFormatted(MODID + ".tooltip.penetratingDamage", String.format("%.2f", vanillaDamage * normal.damageMultiplier)));
        else tooltip.add(I18n.translateToLocalFormatted(MODID + ".tooltip.normalDamage", String.format("%.2f", vanillaDamage * normal.damageMultiplier)));
        for (FantasticPotionEffect potionEffect : normal.attackerEffects)
        {
            tooltip.add(I18n.translateToLocalFormatted(MODID + ".tooltip.giveYouPotion", I18n.translateToLocalFormatted(potionEffect.getPotion().getName()), potionEffect.getAmplifier() + 1, Potion.getPotionDurationString(potionEffect, 1)));
        }
        for (FantasticPotionEffect potionEffect : normal.victimEffects)
        {
            tooltip.add(I18n.translateToLocalFormatted(MODID + ".tooltip.giveVictimPotion", I18n.translateToLocalFormatted(potionEffect.getPotion().getName()), potionEffect.getAmplifier() + 1, Potion.getPotionDurationString(potionEffect, 1)));
        }


        if (!stealth.equals(normal))
        {
            tooltip.add("");
            tooltip.add(I18n.translateToLocal(MODID + ".tooltip.stealthAttacks"));
            if (stealth.consumeItem) tooltip.add(I18n.translateToLocal(MODID + ".tooltip.consumeItem"));
            if (stealth.armorPenetration) tooltip.add(I18n.translateToLocalFormatted(MODID + ".tooltip.penetratingDamage", String.format("%.2f", vanillaDamage * stealth.damageMultiplier)));
            else tooltip.add(I18n.translateToLocalFormatted(MODID + ".tooltip.normalDamage", String.format("%.2f", vanillaDamage * stealth.damageMultiplier)));
            for (FantasticPotionEffect potionEffect : stealth.attackerEffects)
            {
                tooltip.add(I18n.translateToLocalFormatted(MODID + ".tooltip.giveYouPotion", I18n.translateToLocalFormatted(potionEffect.getPotion().getName()), potionEffect.getAmplifier() + 1, Potion.getPotionDurationString(potionEffect, 1)));
            }
            for (FantasticPotionEffect potionEffect : stealth.victimEffects)
            {
                tooltip.add(I18n.translateToLocalFormatted(MODID + ".tooltip.giveVictimPotion", I18n.translateToLocalFormatted(potionEffect.getPotion().getName()), potionEffect.getAmplifier() + 1, Potion.getPotionDurationString(potionEffect, 1)));
            }
        }


        if (!WeaponEntry.resultMatches(vanillaDamage, normal, blockedNormal))
        {
            double blockedDamage = blockedNormal.armorPenetration ? vanillaDamage * blockedNormal.damageMultiplier : 0;
            tooltip.add("");
            tooltip.add(I18n.translateToLocal(MODID + ".tooltip.blockedNormalAttacks"));
            if (blockedNormal.consumeItem) tooltip.add(I18n.translateToLocal(MODID + ".tooltip.consumeItem"));
            if (blockedNormal.armorPenetration) tooltip.add(I18n.translateToLocalFormatted(MODID + ".tooltip.penetratingDamage", String.format("%.2f", blockedDamage)));
            else tooltip.add(I18n.translateToLocalFormatted(MODID + ".tooltip.normalDamage", String.format("%.2f", blockedDamage)));
            for (FantasticPotionEffect potionEffect : blockedNormal.attackerEffects)
            {
                tooltip.add(I18n.translateToLocalFormatted(MODID + ".tooltip.giveYouPotion", I18n.translateToLocalFormatted(potionEffect.getPotion().getName()), potionEffect.getAmplifier() + 1, Potion.getPotionDurationString(potionEffect, 1)));
            }
            for (FantasticPotionEffect potionEffect : blockedNormal.victimEffects)
            {
                tooltip.add(I18n.translateToLocalFormatted(MODID + ".tooltip.giveVictimPotion", I18n.translateToLocalFormatted(potionEffect.getPotion().getName()), potionEffect.getAmplifier() + 1, Potion.getPotionDurationString(potionEffect, 1)));
            }
        }


        if (!WeaponEntry.resultMatches(vanillaDamage, stealth, blockedStealth))
        {
            double blockedDamage = blockedStealth.armorPenetration ? vanillaDamage * blockedStealth.damageMultiplier : 0;
            tooltip.add("");
            tooltip.add(I18n.translateToLocal(MODID + ".tooltip.blockedStealthAttacks"));
            if (blockedStealth.consumeItem) tooltip.add(I18n.translateToLocal(MODID + ".tooltip.consumeItem"));
            if (blockedStealth.armorPenetration) tooltip.add(I18n.translateToLocalFormatted(MODID + ".tooltip.penetratingDamage", String.format("%.2f", blockedDamage)));
            else tooltip.add(I18n.translateToLocalFormatted(MODID + ".tooltip.normalDamage", String.format("%.2f", blockedDamage)));
            for (FantasticPotionEffect potionEffect : blockedStealth.attackerEffects)
            {
                tooltip.add(I18n.translateToLocalFormatted(MODID + ".tooltip.giveYouPotion", I18n.translateToLocalFormatted(potionEffect.getPotion().getName()), potionEffect.getAmplifier() + 1, Potion.getPotionDurationString(potionEffect, 1)));
            }
            for (FantasticPotionEffect potionEffect : blockedStealth.victimEffects)
            {
                tooltip.add(I18n.translateToLocalFormatted(MODID + ".tooltip.giveVictimPotion", I18n.translateToLocalFormatted(potionEffect.getPotion().getName()), potionEffect.getAmplifier() + 1, Potion.getPotionDurationString(potionEffect, 1)));
            }
        }


        if (assassination.attackerEffects.size() > 0)
        {
            tooltip.add("");
            tooltip.add(I18n.translateToLocal(MODID + ".tooltip.assassinations"));
            for (FantasticPotionEffect potionEffect : assassination.attackerEffects)
            {
                tooltip.add(I18n.translateToLocalFormatted(MODID + ".tooltip.giveYouPotion", I18n.translateToLocalFormatted(potionEffect.getPotion().getName()), potionEffect.getAmplifier() + 1, Potion.getPotionDurationString(potionEffect, 1)));
            }
        }
    }
}
