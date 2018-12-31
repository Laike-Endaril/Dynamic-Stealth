package com.fantasticsource.dynamicstealth.compat;

import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.lang.reflect.Field;
import java.util.Set;

public class Compat
{
    private static Field executingTasksField;
    static
    {
        initReflections();
    }


    public static void cancelTasksRequiringAttackTarget(EntityAITasks tasks)
    {
        for (EntityAITasks.EntityAITaskEntry task : tasks.taskEntries)
        {
            EntityAIBase ai = task.action;
            String aiClassname = ai.getClass().getName();
            if (aiClassname.equals("com.lycanitesmobs.core.entity.ai.EntityAIAttackMelee")
                || aiClassname.equals("net.shadowmage.ancientwarfare.npc.ai.vehicle.NpcAIAimVehicle"))
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
