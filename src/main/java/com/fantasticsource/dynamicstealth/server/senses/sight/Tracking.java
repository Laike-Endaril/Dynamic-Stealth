package com.fantasticsource.dynamicstealth.server.senses.sight;

import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Tracking
{
    private static LinkedHashMap<EntityLivingBase, ArrayList<Pair<EntityLivingBase, Integer>>> trackingEntries = new LinkedHashMap<>();

    /**
     * Negative duration is infinite duration
     */
    public static void track(EntityLivingBase tracker, EntityLivingBase target, int tickDuration)
    {
        ArrayList<Pair<EntityLivingBase, Integer>> list = trackingEntries.computeIfAbsent(tracker, k -> new ArrayList<>());
        list.add(new Pair<>(target, tickDuration));
    }

    public static boolean isTracking(EntityLivingBase tracker, Entity target)
    {
        ArrayList<Pair<EntityLivingBase, Integer>> list = trackingEntries.get(tracker);
        return list != null && list.stream().anyMatch(k -> k.getKey() == target);
    }

    @SubscribeEvent
    public static void update(TickEvent.ServerTickEvent event)
    {
        trackingEntries.entrySet().removeIf(e -> updateList(e.getValue()));
    }

    private static boolean updateList(ArrayList<Pair<EntityLivingBase, Integer>> list)
    {
        list.removeIf(Tracking::updateEntry);
        return list.size() == 0;
    }

    private static boolean updateEntry(Pair<EntityLivingBase, Integer> pair)
    {
        int val = pair.getValue();
        if (val >= 0)
        {
            pair.setValue(val - 1);
            return true;
        }

        return false;
    }
}
