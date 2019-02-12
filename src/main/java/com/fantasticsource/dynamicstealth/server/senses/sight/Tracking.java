package com.fantasticsource.dynamicstealth.server.senses.sight;

import com.fantasticsource.tools.Tools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class Tracking
{
    private static LinkedHashMap<EntityLivingBase, LinkedHashMap<Entity, Integer>> trackingEntries = new LinkedHashMap<>();

    public static void track(EntityLivingBase tracker, EntityLivingBase target, int ticks)
    {
        LinkedHashMap<Entity, Integer> map = trackingEntries.get(tracker);
        if (map == null) map = new LinkedHashMap<>();
        map.put(target, Tools.max(map.getOrDefault(target, ticks), ticks));
    }

    public static boolean isTracking(EntityLivingBase tracker, Entity target)
    {
        LinkedHashMap<Entity, Integer> map = trackingEntries.get(tracker);
        return map != null && map.containsKey(target);
    }

    @SubscribeEvent
    public static void update(TickEvent.ServerTickEvent event)
    {
        trackingEntries.entrySet().removeIf(e -> updateMap(e.getValue()));
    }

    private static boolean updateMap(LinkedHashMap<Entity, Integer> map)
    {
        map.entrySet().removeIf(Tracking::updateEntry);
        return map.size() <= 0;
    }

    private static boolean updateEntry(Map.Entry<Entity, Integer> entry)
    {
        int val = entry.getValue();
        if (--val <= 0) return true;

        entry.setValue(val);
        return false;
    }
}
