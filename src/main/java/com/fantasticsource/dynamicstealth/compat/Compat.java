package com.fantasticsource.dynamicstealth.compat;

import java.util.ArrayList;
import java.util.Iterator;

import com.fantasticsource.dynamicstealth.config.DynamicStealthConfig;
import com.fantasticsource.dynamicstealth.server.ai.edited.AIAttackMeleeEdit;
import com.fantasticsource.mctools.NPEAttackTargetTaskHolder;

import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraftforge.fml.common.Loader;

public class Compat
{
    public static boolean
            customnpcs = false,
            neat = false,
            statues = false,
            dissolution = false,
            conarm = false,
            testdummy = false;

    public static void init()
    {
        final String[] addNullChecksToAI = DynamicStealthConfig.serverSettings.ai.addNullChecksToAI;
        ArrayList<String> names = new ArrayList<>(addNullChecksToAI.length);
        for (String entry : addNullChecksToAI)
        {
            entry = entry.trim();
            int commaIndex = entry.indexOf(',');
            if (commaIndex == -1 || commaIndex + 1 > entry.length() || !Loader.isModLoaded(entry.substring(0, commaIndex).trim())) continue;
            
            names.add(entry.substring(commaIndex + 1));
        }
        classNamesToCheck = names.toArray(new String[names.size()]);
        badNullTarget.clear();
    }
    

    public static void cancelTasksRequiringAttackTarget(EntityAITasks tasks)
    {
        for (EntityAITasks.EntityAITaskEntry task : tasks.taskEntries)
        {
            if (hasBadNullTargetHandling(task.action))
            {
                //Hard reset; set using to false, call resetTask(), and remove task from executingTasks
                task.using = false;
                task.action.resetTask();
                tasks.executingTaskEntries.remove(task);
            }
        }
    }

    private static String[] classNamesToCheck;
    // Value of 1 indicates that the corresponding EntityAIBase has bad null targeting.
    private static Object2ByteOpenHashMap<Class<? extends EntityAIBase>> badNullTarget = new Object2ByteOpenHashMap<>();
    {
    	badNullTarget.defaultReturnValue((byte) -1);
    }
    
    private static boolean hasBadNullTargetHandling(EntityAIBase ai)
    {
        if (ai instanceof EntityAIAttackMelee && !(ai instanceof AIAttackMeleeEdit)) return true;
        if (classNamesToCheck.length == 0 || ai instanceof NPEAttackTargetTaskHolder) return false;

        Class<? extends EntityAIBase> aiClass = ai.getClass();
        byte value = badNullTarget.getByte(aiClass);
        if (value != badNullTarget.defaultReturnValue())
            return value == (byte) 1; 
        
        String aiClassName = aiClass.getName();
        for (String str : classNamesToCheck)
            if (aiClassName.contains(str))
            {
                badNullTarget.put(aiClass, (byte) 1);
                return true;
            }

        badNullTarget.put(aiClass, (byte) 0);
        return false;
    }

    public static void replaceNPEAttackTargetTasks(EntityLiving living)
    {
        scanTasksForBadTargetHandling(living, living.targetTasks);
        scanTasksForBadTargetHandling(living, living.tasks);
    }
    
    private static ObjectArrayList<EntityAITasks.EntityAITaskEntry> entityAIStack = new ObjectArrayList<>();
    
    private static void scanTasksForBadTargetHandling(EntityLiving living, EntityAITasks taskList)
    {
        Iterator<EntityAITasks.EntityAITaskEntry> iterator = taskList.taskEntries.iterator();
        while (iterator.hasNext())
        {
            EntityAITaskEntry task = iterator.next();
            if (hasBadNullTargetHandling(task.action))
            {
                if (task.using)
                {
                    task.using = false;
                    task.action.resetTask();
                    taskList.executingTaskEntries.remove(task);
                }
                iterator.remove();
                entityAIStack.push(task);
            }
        }
        
        while (!entityAIStack.isEmpty())
        {
            EntityAITasks.EntityAITaskEntry task = entityAIStack.pop();
            taskList.addTask(task.priority, new NPEAttackTargetTaskHolder(living, task.action));
        }
        // Should never return before entityAIStack is empty.
    }

    public static void clearAttackTargetAndReplaceAITasks(EntityLiving living)
    {
        living.setAttackTarget(null);
        replaceNPEAttackTargetTasks(living);
    }
}
