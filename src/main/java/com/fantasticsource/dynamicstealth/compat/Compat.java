package com.fantasticsource.dynamicstealth.compat;

import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.lang.reflect.Field;
import java.util.Set;

public class Compat
{
    public static boolean lycanites = false, ancientwarfare = false, customnpcs = false, neat = false, statues = false, vampirism = false;

    private static Field executingTasksField;

    static
    {
        initReflections();
    }


    public static void cancelTasksRequiringAttackTarget(EntityAITasks tasks)
    {
        for (EntityAITasks.EntityAITaskEntry task : tasks.taskEntries)
        {
            if (badNullTargetHandling(task.action))
            {
                //Hard reset; set using to false, call resetTask(), and remove task from executingTasks
                task.using = false;
                task.action.resetTask();
                try
                {
                    Set<EntityAITasks.EntityAITaskEntry> executingTasks = (Set<EntityAITasks.EntityAITaskEntry>) executingTasksField.get(tasks);
                    executingTasks.remove(task);
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                    FMLCommonHandler.instance().exitJava(145, false);
                }
            }
        }
    }


    private static boolean badNullTargetHandling(EntityAIBase ai)
    {
        if (ai.getClass() == EntityAIAttackMelee.class) return true;

        String aiClassname = ai.getClass().getName();
        return (lycanites && aiClassname.equals("com.lycanitesmobs.core.entity.ai.EntityAIAttackMelee"))
                || (ancientwarfare && aiClassname.equals("net.shadowmage.ancientwarfare.npc.ai.vehicle.NpcAIAimVehicle"))
                || (vampirism && (aiClassname.equals("de.teamlapen.vampirism.entity.ai.EntityAIAttackMeleeNoSun") || aiClassname.equals("de.teamlapen.vampirism.entity.vampire.EntityVampireBaron$BaronAIAttackMelee")));
    }


    private static void initReflections()
    {
        try
        {
            executingTasksField = ReflectionTool.getField(EntityAITasks.class, "field_75780_b", "executingTaskEntries");
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().exitJava(144, false);
        }
    }
}
