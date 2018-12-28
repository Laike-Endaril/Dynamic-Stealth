package com.fantasticsource.mctools;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class MCTools
{
    public static boolean isOP(EntityPlayerMP player)
    {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().canSendCommands(player.getGameProfile());
    }

    public static boolean isPassive(EntityLiving living)
    {
        //TODO This will probably need improvement at some point, mostly for mod compat, but possibly also for vanilla
        for (EntityAITasks.EntityAITaskEntry task : living.targetTasks.taskEntries)
        {
            if (task.action instanceof EntityAIHurtByTarget) return false;
        }
        return true;
    }
}
