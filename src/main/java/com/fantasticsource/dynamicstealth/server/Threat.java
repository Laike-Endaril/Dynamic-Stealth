package com.fantasticsource.dynamicstealth.server;

import com.fantasticsource.dynamicstealth.common.Network;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.fantasticsource.dynamicstealth.common.DynamicStealth.TRIG_TABLE;
import static com.fantasticsource.dynamicstealth.common.DynamicStealthConfig.serverSettings;

public class Threat
{
    private static final int ITERATION_FREQUENCY = 72000; //Server ticks
    public static Watchers watchers = new Watchers();
    private static int timer = ITERATION_FREQUENCY;
    //Searcher, target, threat level
    private static Map<EntityLivingBase, ThreatData> threatMap = new LinkedHashMap<>(200);
    //Watcher (player watching a searcher), searcher
    private static Map<EntityPlayerMP, ThreatData> watcherMap = new LinkedHashMap<>(20);

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
        if (event.side == Side.SERVER)
        {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            EntityLivingBase searcher = seenEntities(player).poll();
            Threat.watchers.set(player, searcher);
        }
    }

    private static void removeAllUnused()
    {
        watcherMap.entrySet().removeIf(Threat::checkRemoveWatcher);
        threatMap.entrySet().removeIf(Threat::checkRemoveSearcher);
    }

    private static boolean checkRemoveSearcher(Map.Entry<EntityLivingBase, ThreatData> entry)
    {
        EntityLivingBase searcher = entry.getKey();
        return !searcher.world.loadedEntityList.contains(searcher);
    }

    private static boolean checkRemoveWatcher(Map.Entry<EntityPlayerMP, ThreatData> entry)
    {
        EntityPlayerMP watcher = entry.getKey();
        return !watcher.world.loadedEntityList.contains(watcher);
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


    public static ExplicitPriorityQueue<EntityLivingBase> seenEntities(EntityPlayerMP player)
    {
        ExplicitPriorityQueue<EntityLivingBase> queue = new ExplicitPriorityQueue<>(10);
        double stealthLevel;

        if (serverSettings.senses.usePlayerSenses)
        {
            for (Entity entity : player.world.loadedEntityList)
            {
                if (entity instanceof EntityLivingBase && entity != player)
                {
                    stealthLevel = EntitySensesEdit.stealthLevel(player, entity);
                    if (stealthLevel <= 1) queue.add((EntityLivingBase) entity, stealthLevel);
                }
            }
        }
        else
        {
            for (Entity entity : player.world.loadedEntityList)
            {
                if (entity instanceof EntityLivingBase && entity != player)
                {
                    double distSquared = player.getDistanceSq(entity);
                    if (distSquared <= 2500)
                    {
                        double angleDif = Vec3d.fromPitchYaw(player.rotationPitch, player.rotationYawHead).normalize().dotProduct(new Vec3d(entity.posX - player.posX, entity.posY - player.posY, entity.posZ - player.posZ).normalize());

                        //And because Vec3d.fromPitchYaw occasionally returns values barely out of the range of (-1, 1)...
                        if (angleDif < -1) angleDif = -1;
                        else if (angleDif > 1) angleDif = 1;

                        angleDif = TRIG_TABLE.arccos(angleDif); //0 in front, pi in back

                        if (angleDif / Math.PI * 180 <= 70) queue.add((EntityLivingBase) entity, Math.pow(angleDif, 2) * distSquared);
                    }
                }
            }
        }

        return queue;
    }


    public static boolean bypassesThreat(EntityLivingBase livingBase)
    {
        return livingBase instanceof EntityPlayer || livingBase instanceof EntityDragon || livingBase instanceof EntitySlime;
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


    public static class Watchers
    {
        public ThreatData get(EntityPlayerMP player)
        {
            return watcherMap.get(player);
        }

        public void set(EntityPlayerMP player, EntityLivingBase searcher)
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
