package com.fantasticsource.mctools;

import com.fantasticsource.dynamicstealth.server.ai.AIHurtByTargetEdit;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityGuardian;
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
        if (living instanceof EntityGuardian) return false;
        
        for (EntityAITasks.EntityAITaskEntry task : living.targetTasks.taskEntries)
        {
            if (task.action instanceof EntityAIHurtByTarget
            || task.action instanceof AIHurtByTargetEdit) return false;
        }
        return true;
    }

    public static void printTasks(EntityLiving living)
    {
        ExplicitPriorityQueue<EntityAIBase> queue = new ExplicitPriorityQueue<>();
        EntityAIBase ai;
        String str;
        double priority;

        System.out.println("===================================");
        System.out.println(living.getName());
        System.out.println("===================================");
        for (EntityAITasks.EntityAITaskEntry task : living.targetTasks.taskEntries)
        {
            queue.add(task.action, task.priority);
        }
        while(queue.size() > 0)
        {
            priority = queue.peekPriority();
            ai = queue.poll();
            str = ai.getClass().getSimpleName();
            if (str.equals("")) str = ai.getClass().getName();
            if (str.equals("")) str = ai.getClass().getPackage().getName() + ".???????";
            System.out.println(priority + "\t" + str);
        }
        System.out.println("===================================");
        for (EntityAITasks.EntityAITaskEntry task : living.tasks.taskEntries)
        {
            queue.add(task.action, task.priority);
        }
        while(queue.size() > 0)
        {
            priority = queue.peekPriority();
            ai = queue.poll();
            str = ai.getClass().getSimpleName();
            if (str.equals("")) str = ai.getClass().getName();
            if (str.equals("")) str = ai.getClass().getPackage().getName() + ".???????";
            System.out.println(priority + "\t" + str);
        }
        System.out.println("===================================");
        System.out.println();
    }
}
