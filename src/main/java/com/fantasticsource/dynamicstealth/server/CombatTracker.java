package com.fantasticsource.dynamicstealth.server;

import com.fantasticsource.dynamicstealth.server.threat.Threat;
import com.fantasticsource.mctools.MCTools;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.LinkedHashMap;

import static com.fantasticsource.mctools.ServerTickTimer.currentTick;

public class CombatTracker
{
    private static final int ITERATION_FREQUENCY = 72000;

    private static LinkedHashMap<EntityLivingBase, Long> successfulAttackTimes = new LinkedHashMap<>();
    private static LinkedHashMap<EntityLivingBase, Long> successfulPathTimes = new LinkedHashMap<>();
    private static LinkedHashMap<EntityLivingBase, Long> idleTimes = new LinkedHashMap<>();
    private static LinkedHashMap<EntityLivingBase, Long> noTargetTimes = new LinkedHashMap<>();


    public static void update(TickEvent.ServerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            long modTick = currentTick() % ITERATION_FREQUENCY;
            if (modTick == 1000) removeAllUnusedTimes(successfulAttackTimes);
            else if (modTick == 2000) removeAllUnusedTimes(successfulPathTimes);
            else if (modTick == 3000) removeAllUnusedTimes(idleTimes);
            else if (modTick == 4000) removeAllUnusedTimes(noTargetTimes);
        }
    }

    private static void removeAllUnusedTimes(LinkedHashMap<EntityLivingBase, Long> map)
    {
        map.entrySet().removeIf(entry -> !MCTools.entityIsValid(entry.getKey()));
    }


    public static void setSuccessfulAttackTime(EntityLivingBase livingBase)
    {
        setSuccessfulAttackTime(livingBase, currentTick());
    }

    public static void setSuccessfulAttackTime(EntityLivingBase livingBase, long time)
    {
        if (GlobalDefaultsAndData.isFullBypass(livingBase)) return;
        successfulAttackTimes.put(livingBase, time);
    }

    public static long lastSuccessfulAttackTime(EntityLivingBase livingBase)
    {
        if (GlobalDefaultsAndData.isFullBypass(livingBase) || !successfulAttackTimes.containsKey(livingBase)) return -1;
        return successfulAttackTimes.get(livingBase);
    }

    public static long timeSinceLastSuccessfulAttack(EntityLivingBase livingBase)
    {
        if (GlobalDefaultsAndData.isFullBypass(livingBase)) return Long.MAX_VALUE;

        long result = lastSuccessfulAttackTime(livingBase);
        return result == -1 ? Long.MAX_VALUE : currentTick() - result;
    }


    public static boolean pathReachesThreatTarget(EntityLiving living)
    {
        return pathReachesThreatTarget(living, null);
    }

    public static boolean pathReachesThreatTarget(EntityLiving living, Path path)
    {
        if (GlobalDefaultsAndData.isFullBypass(living)) return false;

        if (path == null) path = living.getNavigator().getPath();
        if (path == null) return false;

        EntityLivingBase target = Threat.getTarget(living);
        if (target == null) return false;

        PathPoint point = path.getFinalPathPoint();
        if (point == null || target.getPosition().distanceSq(new BlockPos(point.x, point.y, point.z)) > 2) return false;

        CombatTracker.setSuccessfulPathTime(living);
        return true;
    }

    public static void setSuccessfulPathTime(EntityLivingBase livingBase)
    {
        setSuccessfulPathTime(livingBase, currentTick());
    }

    public static void setSuccessfulPathTime(EntityLivingBase livingBase, long time)
    {
        if (GlobalDefaultsAndData.isFullBypass(livingBase)) return;
        successfulPathTimes.put(livingBase, time);
    }

    public static long lastSuccessfulPathTime(EntityLivingBase livingBase)
    {
        if (GlobalDefaultsAndData.isFullBypass(livingBase) || !successfulPathTimes.containsKey(livingBase)) return -1;
        return successfulPathTimes.get(livingBase);
    }

    public static long timeSinceLastSuccessfulPath(EntityLivingBase livingBase)
    {
        if (GlobalDefaultsAndData.isFullBypass(livingBase)) return Long.MAX_VALUE;

        long result = lastSuccessfulPathTime(livingBase);
        return result == -1 ? Long.MAX_VALUE : currentTick() - result;
    }


    public static void setIdleTime(EntityLivingBase livingBase)
    {
        if (GlobalDefaultsAndData.isFullBypass(livingBase)) return;

        idleTimes.remove(livingBase);
    }

    public static void setIdleTime(EntityLivingBase livingBase, long time)
    {
        if (GlobalDefaultsAndData.isFullBypass(livingBase)) return;

        if (time == currentTick()) idleTimes.remove(livingBase);
        else idleTimes.put(livingBase, time);
    }

    public static long lastIdleTime(EntityLivingBase livingBase)
    {
        if (GlobalDefaultsAndData.isFullBypass(livingBase)) return -1;

        if (!idleTimes.containsKey(livingBase)) return -1;
        return idleTimes.get(livingBase);
    }

    public static long timeSinceLastIdle(EntityLivingBase livingBase)
    {
        if (GlobalDefaultsAndData.isFullBypass(livingBase)) return 0;

        long result = lastIdleTime(livingBase);
        return result == -1 ? 0 : currentTick() - result;
    }


    public static void setNoTargetTime(EntityLivingBase livingBase)
    {
        if (GlobalDefaultsAndData.isFullBypass(livingBase)) return;

        noTargetTimes.remove(livingBase);
    }

    public static void setNoTargetTime(EntityLivingBase livingBase, long time)
    {
        if (GlobalDefaultsAndData.isFullBypass(livingBase)) return;

        if (time == currentTick()) noTargetTimes.remove(livingBase);
        else noTargetTimes.put(livingBase, time);
    }

    public static long lastNoTargetTime(EntityLivingBase livingBase)
    {
        if (GlobalDefaultsAndData.isFullBypass(livingBase) || !noTargetTimes.containsKey(livingBase)) return -1;
        return noTargetTimes.get(livingBase);
    }

    public static long timeSinceLastNoTarget(EntityLivingBase livingBase)
    {
        if (GlobalDefaultsAndData.isFullBypass(livingBase)) return 0;

        long result = lastNoTargetTime(livingBase);
        return result == -1 ? 0 : currentTick() - result;
    }
}
