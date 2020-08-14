package com.fantasticsource.dynamicstealth.compat;

import com.fantasticsource.dynamicstealth.config.DynamicStealthConfig;
import com.fantasticsource.dynamicstealth.server.ai.edited.AIAttackMeleeEdit;
import com.fantasticsource.tools.Tools;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraftforge.fml.common.Loader;

import java.util.HashSet;

public class Compat
{
    public static HashSet<Class> NAUGHTY = new HashSet<>(), NICE = new HashSet<>();

    public static Class bibliocraftArmorStandEntity = null;

    public static boolean
            customnpcs = false,
            neat = false,
            statues = false,
            iceandfire = false,
            dissolution = false,
            conarm = false,
            testdummy = false;


    protected static void cancelTasksRequiringAttackTarget(EntityAITasks tasks)
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


    protected static boolean badNullTargetHandling(EntityAIBase ai)
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


    public static void clearAttackTargetAndCancelBadTasks(EntityLiving living)
    {
        living.setAttackTarget(null);
        cancelTasksRequiringAttackTarget(living.targetTasks);
        cancelTasksRequiringAttackTarget(living.tasks);
    }
}
