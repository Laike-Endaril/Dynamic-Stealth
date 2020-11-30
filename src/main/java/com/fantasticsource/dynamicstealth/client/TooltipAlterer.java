package com.fantasticsource.dynamicstealth.client;

import com.fantasticsource.dynamicstealth.common.ClientData;
import com.fantasticsource.dynamicstealth.server.event.attacks.WeaponEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

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
                blocked = ClientData.getWeaponEntry(stack, WeaponEntry.TYPE_NORMAL, true),
                stealth = ClientData.getWeaponEntry(stack, WeaponEntry.TYPE_STEALTH, false),
                stealthBlocked = ClientData.getWeaponEntry(stack, WeaponEntry.TYPE_STEALTH, true),
                assassination = ClientData.getWeaponEntry(stack, WeaponEntry.TYPE_ASSASSINATION, false);

        List<String> tooltip = event.getToolTip();
        for (int i = 0; i < tooltip.size(); i++)
        {
            String line = tooltip.get(i);
            if (line.contains(attackDamageString))
            {
                try
                {
                    double vanillaDamage = Double.parseDouble(line.replace(attackDamageString, "").trim());
                    System.out.println(vanillaDamage);

                    //TODO [Consume item for ]mulDamage[ (penetrating)]
//                    tooltip.set(i, line.replaceFirst("[0-9.]+", "" + damage));
                }
                catch (NumberFormatException e)
                {
                    continue;
                }
            }
        }
    }
}
