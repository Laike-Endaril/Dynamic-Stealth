package com.fantasticsource.dynamicstealth;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

public class Threat
{
    //Searcher EntityLiving, current target EntityLiving, current threat
    private static Map<EntityLiving, Pair<EntityLivingBase, Integer>> threatMap = new LinkedHashMap<>(200);


    public static void removeUnusedEntities()
    {
        threatMap.entrySet().removeIf(Threat::checkRemove);
    }

    private static boolean checkRemove(Map.Entry<EntityLiving, Pair<EntityLivingBase, Integer>> entry)
    {
        return !entry.getKey().world.loadedEntityList.contains(entry);
    }


    public static void remove(EntityLiving searcher)
    {
        threatMap.remove(searcher);
    }


    public static Pair<EntityLivingBase, Integer> get(EntityLiving searcher)
    {
        Pair<EntityLivingBase, Integer> entry = threatMap.get(searcher);
        if (entry != null) return entry;
        return new Pair<>(null, 0);
    }

    public static EntityLivingBase getTarget(EntityLiving searcher)
    {
        Pair<EntityLivingBase, Integer> entry = threatMap.get(searcher);
        if (entry != null) return entry.getKey();
        return null;
    }

    public static Integer getThreat(EntityLiving searcher)
    {
        Pair<EntityLivingBase, Integer> entry = threatMap.get(searcher);
        if (entry != null) return entry.getValue();
        return 0;
    }


    public static void set(EntityLiving searcher, EntityLivingBase target, int threat)
    {
        Pair<EntityLivingBase, Integer> entry = threatMap.get(searcher);
        if (entry != null) entry.set(target, threat);
        else threatMap.put(searcher, new Pair<>(target, threat));
    }

    public static void setTarget(EntityLiving searcher, EntityLivingBase target)
    {
        Pair<EntityLivingBase, Integer> entry = threatMap.get(searcher);
        if (entry != null) entry.setKey(target);
        else threatMap.put(searcher, new Pair<>(target, 0));
    }

    public static void setThreat(EntityLiving searcher, int threat)
    {
        Pair<EntityLivingBase, Integer> entry = threatMap.get(searcher);
        if (entry != null) entry.setValue(threat);
        else threatMap.put(searcher, new Pair<>(null, threat));
    }
}
