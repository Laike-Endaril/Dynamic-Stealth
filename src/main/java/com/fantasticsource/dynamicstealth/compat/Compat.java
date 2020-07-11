package com.fantasticsource.dynamicstealth.compat;

import com.fantasticsource.dynamicstealth.config.DynamicStealthConfig;
import com.fantasticsource.dynamicstealth.server.ai.edited.AIAttackMeleeEdit;
import com.fantasticsource.mctools.NPEAttackTargetTaskHolder;
import com.fantasticsource.tools.Tools;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraftforge.fml.common.Loader;

import java.util.HashSet;
import java.util.Set;

public class Compat
{
    public static HashSet<Class> NAUGHTY = new HashSet<>(), NICE = new HashSet<>();

    public static Class bibliocraftArmorStandEntity = null;

    public static boolean
            customnpcs = false,
            neat = false,
            statues = false,
            dissolution = false,
            conarm = false,
            testdummy = false;


    public static void cancelTasksRequiringAttackTarget(EntityAITasks tasks)
    {
        for (EntityAITasks.EntityAITaskEntry task : tasks.taskEntries)
        {
            if (badNullTargetHandling(task.action))
            {
                //Hard reset; set using to false, call resetTask(), and remove task from executingTasks
                task.using = false;
                task.action.resetTask();
                tasks.executingTaskEntries.remove(task);
            }
        }
    }


    private static boolean badNullTargetHandling(EntityAIBase ai)
    {
        if (ai instanceof EntityAIAttackMelee && !(ai instanceof AIAttackMeleeEdit)) return true;

        Class aiClass = ai.getClass();
        if (NICE.contains(aiClass)) return false;
        if (NAUGHTY.contains(aiClass)) return true;


        String aiClassname = ai.getClass().getName();
        for (String entry : DynamicStealthConfig.serverSettings.ai.addNullChecksToAI)
        {
            String[] tokens = Tools.fixedSplit(entry, ",");
            if (tokens.length != 2) continue;
            if (!Loader.isModLoaded(tokens[0].trim())) continue;

            if (aiClassname.contains(tokens[1].trim()))
            {
                NAUGHTY.add(aiClass);
                return true;
            }
        }

        NICE.add(aiClass);
        return false;
    }


    public static void replaceNPEAttackTargetTasks(EntityLiving living)
    {
        EntityAITasks taskList = living.targetTasks;
        Set<EntityAITasks.EntityAITaskEntry> entrySet = taskList.taskEntries;
        for (EntityAITasks.EntityAITaskEntry task : entrySet.toArray(new EntityAITasks.EntityAITaskEntry[0]))
        {
            if (badNullTargetHandling(task.action))
            {
                taskList.addTask(task.priority, new NPEAttackTargetTaskHolder(living, task.action));
                taskList.removeTask(task.action);
            }
        }

        taskList = living.tasks;
        entrySet = taskList.taskEntries;
        for (EntityAITasks.EntityAITaskEntry task : entrySet.toArray(new EntityAITasks.EntityAITaskEntry[0]))
        {
            if (badNullTargetHandling(task.action))
            {
                taskList.addTask(task.priority, new NPEAttackTargetTaskHolder(living, task.action));
                taskList.removeTask(task.action);
            }
        }
    }

    public static void clearAttackTargetAndReplaceAITasks(EntityLiving living)
    {
        living.setAttackTarget(null);
        replaceNPEAttackTargetTasks(living);
    }
}
