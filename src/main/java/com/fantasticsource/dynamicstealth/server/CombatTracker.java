package com.fantasticsource.dynamicstealth.server;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.fantasticsource.mctools.ServerTickTimer.currentTick;

public class CombatTracker
{
    private static final int ITERATION_FREQUENCY = 72000;

    private static LinkedHashMap<EntityLivingBase, Long> successfulAttackTimes = new LinkedHashMap<>();
    private static LinkedHashMap<EntityLivingBase, Long> successfulPathTimes = new LinkedHashMap<>(); //TODO update contents
    private static LinkedHashMap<EntityLivingBase, Long> idleTimes = new LinkedHashMap<>(); //TODO update contents


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void update(TickEvent.ServerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START && (currentTick() + 1000) % ITERATION_FREQUENCY == 0) removeAllUnusedTimes(successfulAttackTimes);
        if (event.phase == TickEvent.Phase.START && (currentTick() + 2000) % ITERATION_FREQUENCY == 0) removeAllUnusedTimes(successfulPathTimes);
        if (event.phase == TickEvent.Phase.START && (currentTick() + 3000) % ITERATION_FREQUENCY == 0) removeAllUnusedTimes(idleTimes);
    }

    private static void removeAllUnusedTimes(LinkedHashMap<EntityLivingBase, Long> map)
    {
        map.entrySet().removeIf(CombatTracker::checkRemove);
    }

    private static boolean checkRemove(Map.Entry<EntityLivingBase, Long> entry)
    {
        EntityLivingBase livingBase = entry.getKey();
        return !livingBase.world.loadedEntityList.contains(livingBase);
    }


    public static void setSuccessfulAttackTime(EntityLivingBase livingBase)
    {
        setSuccessfulAttackTime(livingBase, currentTick());
    }

    public static void setSuccessfulAttackTime(EntityLivingBase livingBase, long time)
    {
        successfulAttackTimes.put(livingBase, time);
    }

    public static long lastSuccessfulAttackTime(EntityLivingBase livingBase)
    {
        if (!successfulAttackTimes.containsKey(livingBase)) return -1;
        return successfulAttackTimes.get(livingBase);
    }

    public static long timeSinceLastSuccessfulAttack(EntityLivingBase livingBase)
    {
        long result = lastSuccessfulAttackTime(livingBase);
        return result == -1 ? -1 : currentTick() - result;
    }


    public static void setSuccessfulPathTime(EntityLivingBase livingBase)
    {
        setSuccessfulPathTime(livingBase, currentTick());
    }

    public static void setSuccessfulPathTime(EntityLivingBase livingBase, long time)
    {
        successfulPathTimes.put(livingBase, time);
    }

    public static long lastSuccessfulPathTime(EntityLivingBase livingBase)
    {
        if (!successfulPathTimes.containsKey(livingBase)) return -1;
        return successfulPathTimes.get(livingBase);
    }

    public static long timeSinceLastSuccessfulPath(EntityLivingBase livingBase)
    {
        long result = lastSuccessfulPathTime(livingBase);
        return result == -1 ? -1 : currentTick() - result;
    }


    public static void setIdleTime(EntityLivingBase livingBase)
    {
        setIdleTime(livingBase, currentTick());
    }

    public static void setIdleTime(EntityLivingBase livingBase, long time)
    {
        idleTimes.put(livingBase, time);
    }

    public static long lastIdleTime(EntityLivingBase livingBase)
    {
        if (!idleTimes.containsKey(livingBase)) return -1;
        return idleTimes.get(livingBase);
    }

    public static long timeSinceLastIdle(EntityLivingBase livingBase)
    {
        long result = lastIdleTime(livingBase);
        return result == -1 ? -1 : currentTick() - result;
    }
}
