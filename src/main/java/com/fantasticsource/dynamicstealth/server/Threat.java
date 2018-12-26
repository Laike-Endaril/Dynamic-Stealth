package com.fantasticsource.dynamicstealth.server;

import com.fantasticsource.dynamicstealth.common.DynamicStealthConfig;
import com.fantasticsource.dynamicstealth.common.Network;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.LinkedHashMap;
import java.util.Map;

public class Threat
{
    private static final int ITERATION_FREQUENCY = 72000; //Server ticks

    private static int timer = ITERATION_FREQUENCY;

    //Searcher, target, threat level
    private static Map<EntityLiving, ThreatData> threatMap = new LinkedHashMap<>(200);

    //Watcher (player watching a searcher), searcher
    private static Map<EntityPlayerMP, ThreatData> watchedByPlayerMap = new LinkedHashMap<>(20);


    public static void update()
    {
        if (--timer == 0)
        {
            timer = ITERATION_FREQUENCY;
            removeAllUnused();
        }

        if (DynamicStealthConfig.serverSettings.threat.allowClientHUD == 2)
        {
            for (Map.Entry<EntityPlayerMP, ThreatData> entry : watchedByPlayerMap.entrySet())
            {
                ThreatData oldThreatData = entry.getValue();
                ThreatData newThreatData = threatMap.get(oldThreatData.searcher);

                if (oldThreatData.threatLevel != newThreatData.threatLevel || oldThreatData.target != newThreatData.target || oldThreatData.searcher != newThreatData.searcher)
                {
                    Network.sendThreatData(entry.getKey(), newThreatData.searcher, newThreatData.target, newThreatData.threatLevel);
                    entry.setValue(newThreatData.copy());
                }
            }
        }
        else if (DynamicStealthConfig.serverSettings.threat.allowClientHUD == 1)
        {
            for (Map.Entry<EntityPlayerMP, ThreatData> entry : watchedByPlayerMap.entrySet())
            {
                EntityPlayerMP player = entry.getKey();
                if (FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().canSendCommands(player.getGameProfile()))
                {
                    ThreatData oldThreatData = entry.getValue();
                    ThreatData newThreatData = threatMap.get(oldThreatData.searcher);

                    if (oldThreatData.threatLevel != newThreatData.threatLevel || oldThreatData.target != newThreatData.target || oldThreatData.searcher != newThreatData.searcher)
                    {
                        Network.sendThreatData(player, newThreatData.searcher, newThreatData.target, newThreatData.threatLevel);
                        entry.setValue(newThreatData.copy());
                    }
                }
            }
        }
    }

    private static void removeAllUnused()
    {
        watchedByPlayerMap.entrySet().removeIf(Threat::checkRemoveWatcher);
        threatMap.entrySet().removeIf(Threat::checkRemoveSearcher);
    }

    private static boolean checkRemoveSearcher(Map.Entry<EntityLiving, ThreatData> entry)
    {
        EntityLiving searcher = entry.getKey();
        return !searcher.world.loadedEntityList.contains(searcher);
    }

    private static boolean checkRemoveWatcher(Map.Entry<EntityPlayerMP, ThreatData> entry)
    {
        EntityPlayerMP watcher = entry.getKey();
        return !watcher.world.loadedEntityList.contains(watcher);
    }


    public static void removeTargetFromAll(EntityLivingBase target)
    {
        for (Map.Entry<EntityLiving, ThreatData> entry : threatMap.entrySet())
        {
            ThreatData threatData = entry.getValue();
            if (threatData.target == target) threatData.target = null;
        }
    }


    public static void remove(EntityLiving searcher)
    {
        threatMap.remove(searcher);
    }


    public static ThreatData get(EntityLiving searcher)
    {
        ThreatData threatData = threatMap.get(searcher);
        if (threatData != null) return threatData;
        return new ThreatData(null, null, 0);
    }

    public static EntityLivingBase getTarget(EntityLiving searcher)
    {
        ThreatData threatData = threatMap.get(searcher);
        if (threatData != null) return threatData.searcher;
        return null;
    }

    public static Integer getThreat(EntityLiving searcher)
    {
        ThreatData threatData = threatMap.get(searcher);
        if (threatData != null) return threatData.threatLevel;
        return 0;
    }


    public static void set(EntityLiving searcher, EntityLivingBase target, int threat)
    {
        if (threat <= 0) remove(searcher);
        else
        {
            ThreatData threatData = threatMap.get(searcher);
            if (threatData != null)
            {
                threatData.target = target;
                threatData.threatLevel = threat;
            }
            else threatMap.put(searcher, new ThreatData(searcher, target, threat));
        }
    }

    public static void setTarget(EntityLiving searcher, EntityLivingBase target)
    {
        ThreatData threatData = threatMap.get(searcher);
        if (threatData != null) threatData.target = target;
        else threatMap.put(searcher, new ThreatData(searcher, target, 0));
    }

    public static void setThreat(EntityLiving searcher, int threat)
    {
        if (threat <= 0) remove(searcher);
        else
        {
            ThreatData threatData = threatMap.get(searcher);
            if (threatData != null) threatData.threatLevel = threat;
            else threatMap.put(searcher, new ThreatData(searcher, null, threat));
        }
    }


    public static class ThreatData
    {
        public EntityLiving searcher;
        public EntityLivingBase target;
        public int threatLevel;

        public ThreatData(EntityLiving searcherIn, EntityLivingBase targetIn, int threatLevelIn)
        {
            searcher = searcherIn;
            target = targetIn;
            threatLevel = threatLevelIn;
        }

        public ThreatData copy()
        {
            return new ThreatData(searcher, target, threatLevel);
        }
    }
}
