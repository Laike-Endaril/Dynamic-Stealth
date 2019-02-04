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
    private static LinkedHashMap<EntityLivingBase, Long> successfulPathTimes = new LinkedHashMap<>();
    private static LinkedHashMap<EntityLivingBase, Long> idleTimes = new LinkedHashMap<>();
    private static LinkedHashMap<EntityLivingBase, Long> noTargetTimes = new LinkedHashMap<>();


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void update(TickEvent.ServerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START && (currentTick() + 1000) % ITERATION_FREQUENCY == 0) removeAllUnusedTimes(successfulAttackTimes);
        if (event.phase == TickEvent.Phase.START && (currentTick() + 2000) % ITERATION_FREQUENCY == 0) removeAllUnusedTimes(successfulPathTimes);
        if (event.phase == TickEvent.Phase.START && (currentTick() + 3000) % ITERATION_FREQUENCY == 0) removeAllUnusedTimes(idleTimes);
        if (event.phase == TickEvent.Phase.START && (currentTick() + 4000) % ITERATION_FREQUENCY == 0) removeAllUnusedTimes(noTargetTimes);
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
        return result == -1 ? Long.MAX_VALUE : currentTick() - result;
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
        return result == -1 ? Long.MAX_VALUE : currentTick() - result;
    }


    public static void setIdleTime(EntityLivingBase livingBase)
    {
        idleTimes.remove(livingBase);
    }

    public static void setIdleTime(EntityLivingBase livingBase, long time)
    {
        if (time == currentTick()) idleTimes.remove(livingBase);
        else idleTimes.put(livingBase, time);
    }

    public static long lastIdleTime(EntityLivingBase livingBase)
    {
        if (!idleTimes.containsKey(livingBase)) return -1;
        return idleTimes.get(livingBase);
    }

    public static long timeSinceLastIdle(EntityLivingBase livingBase)
    {
        long result = lastIdleTime(livingBase);
        return result == -1 ? 0 : currentTick() - result;
    }


    public static void setNoTargetTime(EntityLivingBase livingBase)
    {
        noTargetTimes.remove(livingBase);
    }

    public static void setNoTargetTime(EntityLivingBase livingBase, long time)
    {
        if (time == currentTick()) noTargetTimes.remove(livingBase);
        else noTargetTimes.put(livingBase, time);
    }

    public static long lastNoTargetTime(EntityLivingBase livingBase)
    {
        if (!noTargetTimes.containsKey(livingBase)) return -1;
        return noTargetTimes.get(livingBase);
    }

    public static long timeSinceLastNoTarget(EntityLivingBase livingBase)
    {
        long result = lastNoTargetTime(livingBase);
        return result == -1 ? 0 : currentTick() - result;
    }
}
