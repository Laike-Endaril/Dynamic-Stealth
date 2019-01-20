package com.fantasticsource.dynamicstealth.server.threat;

import com.fantasticsource.mctools.MCTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.fantasticsource.dynamicstealth.common.DynamicStealthConfig.serverSettings;

public class Threat
{
    private static final int ITERATION_FREQUENCY = 72000; //Server ticks
    private static int timer = ITERATION_FREQUENCY;
    //Searcher, target, threat level
    private static Map<EntityLivingBase, ThreatData> threatMap = new LinkedHashMap<>(200);

    @SubscribeEvent
    public static void update(TickEvent.ServerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START && --timer == 0)
        {
            timer = ITERATION_FREQUENCY;
            removeAllUnused();
        }
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
        if (threatData != null) return threatData;
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
        if (bypassesThreat(searcher)) return;

        if (threat <= 0) remove(searcher);
        else
        {
            if (threat > serverSettings.threat.maxThreat) threat = serverSettings.threat.maxThreat;

            ThreatData threatData = threatMap.get(searcher);
            if (threatData != null)
            {
                threatData.target = target;
                threatData.threatLevel = threat;
            }
            else threatMap.put(searcher, new ThreatData(searcher, target, threat));
        }
    }

    public static void setTarget(EntityLivingBase searcher, EntityLivingBase target)
    {
        if (bypassesThreat(searcher)) return;

        ThreatData threatData = threatMap.get(searcher);
        if (threatData != null) threatData.target = target;
        else threatMap.put(searcher, new ThreatData(searcher, target, 0));
    }

    public static void setThreat(EntityLivingBase searcher, int threat)
    {
        if (bypassesThreat(searcher)) return;

        if (threat <= 0) remove(searcher);
        else
        {
            if (threat > serverSettings.threat.maxThreat) threat = serverSettings.threat.maxThreat;

            ThreatData threatData = threatMap.get(searcher);
            if (threatData != null) threatData.threatLevel = threat;
            else threatMap.put(searcher, new ThreatData(searcher, null, threat));
        }
    }


    public static boolean bypassesThreat(EntityLivingBase livingBase)
    {
        if (serverSettings.threat.bypassThreatSystem || livingBase == null) return true;

        for (Class<? extends Entity> clss : EntityThreatData.threatBypass)
        {
            if (clss.isAssignableFrom(livingBase.getClass())) return true;
        }

        return false;
    }

    public static boolean isPassive(EntityLivingBase livingBase)
    {
        if (livingBase == null || bypassesThreat(livingBase)) return false;

        for (Class<? extends Entity> clss : EntityThreatData.isPassive)
        {
            if (clss.isAssignableFrom(livingBase.getClass())) return true;
        }

        for (Class<? extends Entity> clss : EntityThreatData.isNonPassive)
        {
            if (clss.isAssignableFrom(livingBase.getClass())) return false;
        }

        return MCTools.isPassive(livingBase);
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
