package com.fantasticsource.dynamicstealth.server;

import com.fantasticsource.dynamicstealth.common.DynamicStealthConfig;
import com.fantasticsource.dynamicstealth.common.Network;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.fantasticsource.dynamicstealth.common.DynamicStealth.TRIG_TABLE;
import static com.fantasticsource.dynamicstealth.common.DynamicStealthConfig.*;

public class Threat
{
    private static final int ITERATION_FREQUENCY = 72000; //Server ticks

    private static int timer = ITERATION_FREQUENCY;

    //Searcher, target, threat level
    private static Map<EntityLiving, ThreatData> threatMap = new LinkedHashMap<>(200);

    //Watcher (player watching a searcher), searcher
    private static Map<EntityPlayerMP, ThreatData> watcherMap = new LinkedHashMap<>(20);

    public static Watchers watchers = new Watchers();


    @SubscribeEvent
    public static void update(TickEvent.ServerTickEvent event)
    {
        if (--timer == 0)
        {
            timer = ITERATION_FREQUENCY;
            removeAllUnused();
        }

        for (Map.Entry<EntityPlayerMP, ThreatData> entry : watcherMap.entrySet())
        {
            ThreatData oldThreatData = entry.getValue();
            if (oldThreatData != null && oldThreatData.searcher != null)
            {
                ThreatData newThreatData = Threat.get(oldThreatData.searcher);

                if (!oldThreatData.equals(newThreatData))
                {
                    Network.sendThreatData(entry.getKey(), newThreatData);
                    entry.setValue(newThreatData.copy());
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP)event.player;
            EntityLiving searcher = focusedEntityLiving(player);
            Threat.watchers.set(player, searcher);
        }
    }

    private static void removeAllUnused()
    {
        watcherMap.entrySet().removeIf(Threat::checkRemoveWatcher);
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


    @Nonnull
    public static ThreatData get(EntityLiving searcher)
    {
        ThreatData threatData = threatMap.get(searcher);
        if (threatData != null) return threatData;
        return new ThreatData(searcher, null, 0);
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

    public static void setTarget(EntityLiving searcher, EntityLivingBase target)
    {
        if (bypassesThreat(searcher)) return;

        ThreatData threatData = threatMap.get(searcher);
        if (threatData != null) threatData.target = target;
        else threatMap.put(searcher, new ThreatData(searcher, target, 0));
    }

    public static void setThreat(EntityLiving searcher, int threat)
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


    public static void sendToAll(int allowHUDMode)
    {
        if (allowHUDMode == 2)
        {
            for (Map.Entry<EntityPlayerMP, ThreatData> entry : watcherMap.entrySet())
            {
                Network.sendThreatData(entry.getKey(), entry.getValue(), true);
            }
        }
        else if (allowHUDMode == 1)
        {
            for (Map.Entry<EntityPlayerMP, ThreatData> entry : watcherMap.entrySet())
            {
                EntityPlayerMP player = entry.getKey();
                if (MCTools.isOP(player)) Network.sendThreatData(entry.getKey(), entry.getValue(), true);
                else Network.sendThreatData(player, null, null, 0, true);
            }
        }
        else
        {
            for (Map.Entry<EntityPlayerMP, ThreatData> entry : watcherMap.entrySet())
            {
                Network.sendThreatData(entry.getKey(), null, null, 0, true);
            }
        }
    }


    public static EntityLiving focusedEntityLiving(EntityPlayerMP player)
    {
        ExplicitPriorityQueue<EntityLiving> queue = new ExplicitPriorityQueue<>(10);

        for (Entity entity : player.world.loadedEntityList)
        {
            if (entity instanceof EntityLiving && entity != player)
            {
                double distSquared = player.getDistanceSq(entity);
                if (distSquared <= 900)
                {
                    //Can see in 360*, but forward still has higher priority

                    double angleDif = Vec3d.fromPitchYaw(player.rotationPitch, player.rotationYawHead).normalize().dotProduct(new Vec3d(entity.posX - player.posX, entity.posY - player.posY, entity.posZ - player.posZ).normalize());

                    //And because Vec3d.fromPitchYaw occasionally returns values barely out of the range of (-1, 1)...
                    if (angleDif < -1) angleDif = -1;
                    else if (angleDif > 1) angleDif = 1;

                    angleDif = TRIG_TABLE.arccos(angleDif); //0 in front, pi in back

                    queue.add((EntityLiving) entity, Math.pow(angleDif, 2) * distSquared);
                }
            }
        }

        boolean usePlayerSenses = false; //TODO When the "player senses" system is ready, replace this with the server config setting

        if (usePlayerSenses)
        {
            EntityLiving result = queue.poll();
            while(result != null && EntitySensesEdit.stealthCheck(player, result)) result = queue.poll();
            return result;
        }

        return queue.poll();
    }


    public static boolean bypassesThreat(EntityLivingBase livingBase)
    {
        return livingBase instanceof EntityDragon || livingBase instanceof EntitySlime;
    }


    public static class ThreatData
    {
        public EntityLiving searcher;
        public EntityLivingBase target;
        public int threatLevel;
        public String searcherName;

        public ThreatData(EntityLiving searcherIn, EntityLivingBase targetIn, int threatLevelIn)
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


    public static class Watchers
    {
        public ThreatData get(EntityPlayerMP player)
        {
            return watcherMap.get(player);
        }

        public void set(EntityPlayerMP player, EntityLiving searcher)
        {
            if (player != null)
            {
                ThreatData oldData = watcherMap.get(player);

                if (searcher == null)
                {
                    if (oldData != null && oldData.searcher != null) Network.sendThreatData(player, null, null, 0);
                    watcherMap.remove(player);
                }
                else
                {
                    ThreatData newData = Threat.get(searcher);

                    if (!newData.equals(oldData))
                    {
                        watcherMap.put(player, newData.copy());
                        Network.sendThreatData(player, newData);
                    }
                }
            }
        }

        public void remove(EntityPlayerMP player)
        {
            watcherMap.remove(player);
        }
    }
}
