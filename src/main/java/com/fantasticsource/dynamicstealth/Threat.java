package com.fantasticsource.dynamicstealth;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

public class Threat
{
    private static final int ITERATION_FREQUENCY = 72000; //Server ticks

    private static int timer = ITERATION_FREQUENCY;

    //Searcher EntityLiving, current target EntityLiving, current threat
    private static Map<EntityLiving, Pair<EntityLivingBase, Integer>> threatMap = new LinkedHashMap<>(200);


    public static void update()
    {
        if (DynamicStealthConfig.a8_threatSystem.debug)
        {
            System.out.println("=====================================================================================");
            System.out.println(threatMap.size() == 0 ? "No threat levels to report" : "Searcher\t\t\tTarget\t\t\tThreat Level");
            for (Map.Entry<EntityLiving, Pair<EntityLivingBase, Integer>> entry : threatMap.entrySet())
            {
                Pair<EntityLivingBase, Integer> properties = entry.getValue();
                System.out.println((entry.getKey() == null ? "null" : entry.getKey().getName()) + "\t\t\t" + (properties.getKey() == null ? "null" : properties.getKey().getName()) + "\t\t\t" + properties.getValue());
            }
            System.out.println("=====================================================================================");
        }

        if (--timer == 0)
        {
            timer = ITERATION_FREQUENCY;
            removeUnusedEntities();
        }
    }

    private static void removeUnusedEntities()
    {
        threatMap.entrySet().removeIf(Threat::checkRemoveSearcher);
    }

    private static boolean checkRemoveSearcher(Map.Entry<EntityLiving, Pair<EntityLivingBase, Integer>> entry)
    {
        EntityLiving searcher = entry.getKey();
        return !searcher.world.loadedEntityList.contains(searcher);
    }


    public static void removeTargetFromAll(EntityLivingBase target)
    {
        for (Map.Entry<EntityLiving, Pair<EntityLivingBase, Integer>> entry : threatMap.entrySet())
        {
            Pair<EntityLivingBase, Integer> properties = entry.getValue();
            if (properties.getKey() == target) properties.setKey(null);
        }
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
