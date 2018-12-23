package com.fantasticsource.dynamicstealth;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.entity.EntityLiving;

public class Threat
{
    //Searcher EntityLiving, current target EntityLiving, current threat
    private static Map<EntityLiving, Pair<EntityLiving, Integer>> threatMap = new LinkedHashMap<>(200);



    public static void remove(EntityLiving searcher)
    {
        threatMap.remove(searcher);
    }



    public static Pair<EntityLiving, Integer> get(EntityLiving searcher)
    {
        return threatMap.get(searcher);
    }

    public static EntityLiving getTarget(EntityLiving searcher)
    {
        Pair<EntityLiving, Integer> entry = threatMap.get(searcher);
        if (entry != null) return entry.getKey();
        return null;
    }

    public static Integer getThreat(EntityLiving searcher)
    {
        Pair<EntityLiving, Integer> entry = threatMap.get(searcher);
        if (entry != null) return entry.getValue();
        return 0;
    }



    public static void set(EntityLiving searcher, EntityLiving target, int threat)
    {
        Pair<EntityLiving, Integer> entry = threatMap.get(searcher);
        if (entry != null) entry.set(target, threat);
        else threatMap.put(searcher, new Pair<>(target, threat));
    }

    public static void setTarget(EntityLiving searcher, EntityLiving target)
    {
        Pair<EntityLiving, Integer> entry = threatMap.get(searcher);
        if (entry != null) entry.setKey(target);
        else threatMap.put(searcher, new Pair<>(target, 0));
    }

    public static void setThreat(EntityLiving searcher, int threat)
    {
        Pair<EntityLiving, Integer> entry = threatMap.get(searcher);
        if (entry != null) entry.setValue(threat);
        else threatMap.put(searcher, new Pair<>(null, threat));
    }



    //TODO When target dies, DO NOT REMOVE!  Go into search mode instead, and degrade threat normally unless it runs out or another target is found.  If a new target is found, set target, and set threat to initial "target found" value
}
