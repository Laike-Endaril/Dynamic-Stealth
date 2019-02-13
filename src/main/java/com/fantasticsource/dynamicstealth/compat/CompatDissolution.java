package com.fantasticsource.dynamicstealth.compat;

import com.fantasticsource.dynamicstealth.server.ai.AIDynamicStealth;
import com.fantasticsource.dynamicstealth.server.threat.Threat;
import ladysnake.dissolution.api.corporeality.IIncorporealHandler;
import ladysnake.dissolution.api.possession.PossessionEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Set;

public class CompatDissolution
{
    @CapabilityInject(IIncorporealHandler.class)
    private static Capability INCORPOREAL_HANDLER_CAP;

    public static boolean isPossessing(EntityPlayer player, Entity entity)
    {
        return Compat.dissolution && ((IIncorporealHandler) player.getCapability(INCORPOREAL_HANDLER_CAP, null)).getPossessed() == entity;
    }

    @SubscribeEvent
    public static void onPossessionStart(PossessionEvent.Start event)
    {
        for (EntityLivingBase livingBase : new EntityLivingBase[]{event.getEntityLiving(), event.getPossessed(), event.getEntityPlayer()})
        {
            //Clear threat
            Threat.setThreat(livingBase, 0);

            //Completely wipe ai by recreating it
            if (livingBase instanceof EntityLiving)
            {
                EntityLiving living = (EntityLiving) livingBase;
                Set taskSet = living.tasks.taskEntries;
                for (EntityAITasks.EntityAITaskEntry entry : (EntityAITasks.EntityAITaskEntry[]) taskSet.toArray(new EntityAITasks.EntityAITaskEntry[taskSet.size()]))
                {
                    if (entry.action instanceof AIDynamicStealth)
                    {
                        AIDynamicStealth ai = new AIDynamicStealth(living, 1);
                        int priority = entry.priority;
                        living.tasks.removeTask(entry.action);
                        living.tasks.addTask(priority, ai);
                    }
                }
            }
        }
    }
}
