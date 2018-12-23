package com.fantasticsource.dynamicstealth;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fantasticsource.dynamicstealth.newai.AISearchLastKnownPosition;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAITasks;

public class Threat
{
    public static int updateFrequency = 20; //Server ticks
    public static double unseenTargetDegredationMutliplier = 0.5;
    public static int unseenMinimumThreat = 10;

    //Searcher EntityLiving, current target EntityLiving, current threat
    private static Map<EntityLiving, Pair<EntityLiving, Integer>> threatMap = new LinkedHashMap<>(200);



    public Pair<EntityLiving, Integer> get(EntityLiving searcher)
    {
        return threatMap.get(searcher);
    }

    public EntityLiving getTarget(EntityLiving searcher)
    {
        Pair<EntityLiving, Integer> entry = threatMap.get(searcher);
        if (entry != null) return entry.getKey();
        return null;
    }

    public Integer getThreat(EntityLiving searcher)
    {
        Pair<EntityLiving, Integer> entry = threatMap.get(searcher);
        if (entry != null) return entry.getValue();
        return 0;
    }



    public void set(EntityLiving searcher, EntityLiving target, int threat)
    {
        Pair<EntityLiving, Integer> entry = threatMap.get(searcher);
        if (entry != null) entry.set(target, threat);
        else threatMap.put(searcher, new Pair<>(target, threat));
    }

    public void setTarget(EntityLiving searcher, EntityLiving target)
    {
        Pair<EntityLiving, Integer> entry = threatMap.get(searcher);
        if (entry != null) entry.setKey(target);
        else threatMap.put(searcher, new Pair<>(target, 0));
    }

    public void setThreat(EntityLiving searcher, int threat)
    {
        Pair<EntityLiving, Integer> entry = threatMap.get(searcher);
        if (entry != null) entry.setValue(threat);
        else threatMap.put(searcher, new Pair<>(null, threat));
    }



    public void update()
    {
        threatMap.entrySet().removeIf(this::updateEntry);
    }

    private boolean updateEntry(Map.Entry<EntityLiving, Pair<EntityLiving, Integer>> entry)
    {
        Pair<EntityLiving, Integer> properties = entry.getValue();
        int threat = properties.getValue();
        if (threat <= 0) return true;

        EntityLiving searcher = entry.getKey();
        for (EntityAITasks.EntityAITaskEntry task : searcher.targetTasks.taskEntries)
        {
            if (task.action instanceof AISearchLastKnownPosition)
            {
                if (task.using)
                {
                    threat = (int) (threat * unseenTargetDegredationMutliplier);
                    if (threat < unseenMinimumThreat) return true;
                }
                break;
            }
        }

        return false;
    }



    //TODO Scale damage-based threat changes based on searcher's max hp

    //TODO Remove when...
    //...searcher dies (go to out-of-combat)
    //...searcher is unloaded (go to out-of-combat)

    //TODO ...DO NOT reset when target dies (go into search mode instead, and degrade threat normally unless it runs out or another target is found.  If a new target is found, set threat to initial value)
}
