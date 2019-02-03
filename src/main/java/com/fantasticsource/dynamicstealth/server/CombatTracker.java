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
    private static final int ITERATION_FREQUENCY = 72001;

    private static LinkedHashMap<EntityLivingBase, Long> successfulAttackTimes = new LinkedHashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void update(TickEvent.ServerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START && currentTick() % ITERATION_FREQUENCY == 0) removeAllUnused();
    }

    private static void removeAllUnused()
    {
        successfulAttackTimes.entrySet().removeIf(CombatTracker::checkRemove);
    }

    private static boolean checkRemove(Map.Entry<EntityLivingBase, Long> entry)
    {
        EntityLivingBase livingBase = entry.getKey();
        return !livingBase.world.loadedTileEntityList.contains(livingBase);
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
}
