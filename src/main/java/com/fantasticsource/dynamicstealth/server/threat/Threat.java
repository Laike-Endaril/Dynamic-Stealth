package com.fantasticsource.dynamicstealth.server.threat;

import com.fantasticsource.dynamicstealth.server.CombatTracker;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.fantasticsource.dynamicstealth.common.DynamicStealthConfig.serverSettings;
import static com.fantasticsource.mctools.ServerTickTimer.currentTick;

public class Threat
{
    private static final int ITERATION_FREQUENCY = 72000;

    //Searcher, target, threat level
    private static Map<EntityLivingBase, ThreatData> threatMap = new LinkedHashMap<>(200);

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void update(TickEvent.ServerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START && currentTick() % ITERATION_FREQUENCY == 0) removeAllUnused();
    }


    private static void removeAllUnused()
    {
        threatMap.entrySet().removeIf(Threat::checkRemoveSearcher);
    }

    private static boolean checkRemoveSearcher(Map.Entry<EntityLivingBase, ThreatData> entry)
    {
        EntityLivingBase searcher = entry.getKey();
        return !searcher.world.loadedEntityList.contains(searcher);
    }

    public static void removeTargetFromAll(EntityLivingBase target)
    {
        for (Map.Entry<EntityLivingBase, ThreatData> entry : threatMap.entrySet())
        {
            ThreatData threatData = entry.getValue();
            if (threatData.target == target) threatData.target = null;
        }
    }


    public static void remove(EntityLivingBase searcher)
    {
        threatMap.remove(searcher);
    }


    @Nonnull
    public static ThreatData get(EntityLivingBase searcher)
    {
        ThreatData threatData = threatMap.get(searcher);
        if (threatData != null) return threatData.copy();
        return new ThreatData(searcher, null, 0);
    }

    public static EntityLivingBase getTarget(EntityLivingBase searcher)
    {
        ThreatData threatData = threatMap.get(searcher);
        if (threatData != null) return threatData.searcher;
        return null;
    }

    public static Integer getThreat(EntityLivingBase searcher)
    {
        ThreatData threatData = threatMap.get(searcher);
        if (threatData != null) return threatData.threatLevel;
        return 0;
    }


    public static void set(EntityLivingBase searcher, EntityLivingBase target, int threat)
    {
        if (EntityThreatData.bypassesThreat(searcher)) return;

        if (threat <= 0)
        {
            CombatTracker.setIdleTime(searcher);
            remove(searcher);
        }
        else
        {
            if (threat > serverSettings.threat.maxThreat) threat = serverSettings.threat.maxThreat;

            ThreatData threatData = threatMap.get(searcher);
            if (threatData != null)
            {
                if (threatData.threatLevel <= 0) CombatTracker.setIdleTime(searcher, currentTick() - 1);
                threatData.target = target;
                threatData.threatLevel = threat;
            }
            else
            {
                CombatTracker.setIdleTime(searcher, currentTick() - 1);
                threatMap.put(searcher, new ThreatData(searcher, target, threat));
            }
        }
    }

    public static void setTarget(EntityLivingBase searcher, EntityLivingBase target)
    {
        if (EntityThreatData.bypassesThreat(searcher)) return;

        ThreatData threatData = threatMap.get(searcher);
        if (threatData != null) threatData.target = target;
    }

    public static void setThreat(EntityLivingBase searcher, int threat)
    {
        if (EntityThreatData.bypassesThreat(searcher)) return;

        if (threat <= 0)
        {
            CombatTracker.setIdleTime(searcher);
            remove(searcher);
        }
        else
        {
            if (threat > serverSettings.threat.maxThreat) threat = serverSettings.threat.maxThreat;

            ThreatData threatData = threatMap.get(searcher);
            if (threatData != null)
            {
                if (threatData.threatLevel <= 0) CombatTracker.setIdleTime(searcher, currentTick() - 1);
                threatData.threatLevel = threat;
            }
            else
            {
                CombatTracker.setIdleTime(searcher, currentTick() - 1);
                threatMap.put(searcher, new ThreatData(searcher, null, threat));
            }
        }
    }


    public static class ThreatData
    {
        public EntityLivingBase searcher;
        public EntityLivingBase target;
        public int threatLevel;
        public String searcherName;

        public ThreatData(EntityLivingBase searcherIn, EntityLivingBase targetIn, int threatLevelIn)
        {
            searcher = searcherIn;
            target = targetIn;
            threatLevel = threatLevelIn;

            searcherName = searcher.getName();
        }

        public ThreatData copy()
        {
            return new ThreatData(searcher, target, threatLevel);
        }

        public boolean equals(ThreatData threatData)
        {
            return threatData != null && threatData.searcher == searcher && threatData.target == target && threatData.threatLevel == threatLevel && threatData.searcherName.equals(searcherName);
        }

        public String toString()
        {
            return searcherName + ", " + target.getName() + ", " + threatLevel;
        }
    }
}
